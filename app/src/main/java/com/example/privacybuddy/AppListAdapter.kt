package com.example.privacybuddy

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class AppListAdapter(private var appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppViewHolder>(),
    FastScrollRecyclerView.SectionedAdapter {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appNameText: TextView = view.findViewById(R.id.appNameText)
        val packageText: TextView = view.findViewById(R.id.packageText)
        val permissionCountText: TextView = view.findViewById(R.id.permissionCountText)
        val riskLevelText: TextView = view.findViewById(R.id.riskLevelText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.appNameText.text = app.appName
        holder.packageText.text = app.packageName
        holder.permissionCountText.text = "Permissions: ${app.permissions.size} | Risk Score: ${app.riskScore}"
        holder.riskLevelText.text = "Risk: ${app.riskLevel}"

        // Set background color based on risk level
        val bgColor = when (app.riskLevel) {
            "High" -> Color.parseColor("#D32F2F")   // Vibrant Red (unchanged)
            "Medium" -> Color.parseColor("#FFA000")  // Amber/Orange (unchanged)
            "Low" -> Color.parseColor("#0288D1")     // Blue (new - better for "Low")
            "Safe" -> Color.parseColor("#388E3C")    // Green (moved from "Low")
            else -> Color.DKGRAY
        }
        holder.riskLevelText.setBackgroundColor(bgColor)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AppDetailActivity::class.java).apply {
                putExtra("appName", app.appName)
                putExtra("packageName", app.packageName)
                putStringArrayListExtra("permissions", ArrayList(app.permissions))
                putExtra("riskLevel", app.riskLevel)
                putExtra("riskScore", app.riskScore)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = appList.size

    fun updateData(newList: List<AppInfo>) {
        appList = newList
        notifyDataSetChanged()
    }

    override fun getSectionName(position: Int): String {
        return appList[position].appName.firstOrNull()?.uppercase() ?: "#"
    }
}
