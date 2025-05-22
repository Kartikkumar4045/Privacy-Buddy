package com.example.privacybuddy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private var fullAppList: List<AppInfo> = listOf()


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val radioGroup = findViewById<RadioGroup>(R.id.categoryRadioGroup)

        fun updateAppList(type: AppType) {
            fullAppList = AppScanner.getFilteredApps(this, type)
            adapter = AppListAdapter(fullAppList)
            recyclerView.adapter = adapter
        }


        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                R.id.userAppsRadio -> AppType.USER
                R.id.systemAppsRadio -> AppType.SYSTEM
                R.id.allAppsRadio -> AppType.ALL
                else -> AppType.USER
            }
            updateAppList(type)
        }

        // Load default: Installed Apps
        updateAppList(AppType.USER)

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = fullAppList.filter {
                    it.appName.contains(newText.orEmpty(), ignoreCase = true)
                }
                adapter.updateData(filteredList)
                return true
            }
        })

        val sortSpinner = findViewById<Spinner>(R.id.sortSpinner)
        val sortOptions = listOf("Default", "Risk: High to Low", "Risk: Low to High")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = spinnerAdapter

        fullAppList = AppScanner.getFilteredApps(this, AppType.ALL)
        adapter.updateData(fullAppList)


        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val sortedList = when (position) {
                    1 -> fullAppList.sortedByDescending { it.riskScore }  // High to Low
                    2 -> fullAppList.sortedBy { it.riskScore }            // Low to High
                    else -> fullAppList.sortedBy { it.appName.lowercase() } // Default: Alphabetical
                }
                adapter.updateData(sortedList)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



    }
}

