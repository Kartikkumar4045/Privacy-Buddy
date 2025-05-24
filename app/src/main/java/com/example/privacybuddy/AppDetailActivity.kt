package com.example.privacybuddy

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.privacybuddy.ui.RiskExplanationBottomSheet
import com.example.privacybuddy.utils.RiskAnalyzer

class AppDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)

        val appName = intent.getStringExtra("appName") ?: "Unknown"
        val packageName = intent.getStringExtra("packageName") ?: "N/A"
        val permissions = intent.getStringArrayListExtra("permissions") ?: arrayListOf()
        val riskLevel = intent.getStringExtra("riskLevel") ?: "Unknown"
        val riskScore = intent.getIntExtra("riskScore", 0)

        val appNameText = findViewById<TextView>(R.id.detailAppName)
        val packageText = findViewById<TextView>(R.id.detailPackage)
        val riskText = findViewById<TextView>(R.id.detailRisk)
        val permissionsText = findViewById<TextView>(R.id.detailPermissions)
        val explanationText = findViewById<TextView>(R.id.detailExplanation)
        val explainRiskButton = findViewById<Button>(R.id.explainRiskButton)

        appNameText.text = appName
        packageText.text = "Package: $packageName"

        // Set color & icon based on risk level
        val (color, iconRes) = when (riskLevel) {
            "High" -> Color.parseColor("#D32F2F") to R.drawable.dangerous
            "Medium" -> Color.parseColor("#FFA000") to R.drawable.warning
            "Low" -> Color.parseColor("#0288D1") to R.drawable.info
            "Safe" -> Color.parseColor("#388E3C") to R.drawable.safe
            else -> Color.BLACK to R.drawable.info
        }

        riskText.text = "Risk: $riskLevel (Score: $riskScore)"
        riskText.setTextColor(color)

        val drawable = ContextCompat.getDrawable(this, iconRes)
        drawable?.setTint(color)
        riskText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        // Permissions
        if (permissions.isEmpty()) {
            permissionsText.text = "No permissions found."
        } else {
            val spannableBuilder = SpannableStringBuilder()
            permissions.forEach {
                val line = SpannableString(it)
                line.setSpan(BulletSpan(16), 0, line.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableBuilder.append(line).append("\n")
            }
            permissionsText.text = spannableBuilder
        }

        // AI-style explanation (simple local version)
        val explanation = RiskAnalyzer.getRiskExplanation(permissions)
        explanationText.text = explanation

        // Show AI bottom sheet on button click
        explainRiskButton.setOnClickListener {
            val bottomSheet = RiskExplanationBottomSheet(permissions)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }
}
