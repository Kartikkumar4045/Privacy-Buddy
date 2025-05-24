package com.example.privacybuddy

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.privacybuddy.utils.RiskAnalyzer

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private var fullAppList: List<AppInfo> = listOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppListAdapter

    private lateinit var sortSpinner: Spinner
    private lateinit var riskFilterSpinner: Spinner

    private var currentSearchQuery = ""
    private var currentRiskFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val radioGroup = findViewById<RadioGroup>(R.id.categoryRadioGroup)
        sortSpinner = findViewById(R.id.sortSpinner)
        riskFilterSpinner = findViewById(R.id.riskFilterSpinner)
        searchView = findViewById(R.id.searchView)

        adapter = AppListAdapter(fullAppList)
        recyclerView.adapter = adapter



        fun applyFilters() {
            var filtered = fullAppList

            // Apply risk level filter
            filtered = when (currentRiskFilter) {
                "High" -> filtered.filter { it.riskLevel == "High" }
                "Medium" -> filtered.filter { it.riskLevel == "Medium" }
                "Low" -> filtered.filter { it.riskLevel == "Low" }
                "Safe" -> filtered.filter { it.riskLevel == "Safe" }
                else -> filtered
            }

            // Apply search filter
            if (currentSearchQuery.isNotBlank()) {
                filtered = filtered.filter {
                    it.appName.contains(currentSearchQuery, ignoreCase = true)
                }
            }

            // Apply sorting
            val sorted = when (sortSpinner.selectedItemPosition) {
                1 -> filtered.sortedByDescending { it.riskScore } // High to Low
                2 -> filtered.sortedBy { it.riskScore }           // Low to High
                else -> filtered.sortedBy { it.appName.lowercase() } // Default
            }

            adapter.updateData(sorted)
        }

        fun updateAppList(type: AppType) {
            fullAppList = AppScanner.getFilteredApps(this, type)
            applyFilters()
        }

        // Category filter (radio buttons)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                R.id.userAppsRadio -> AppType.USER
                R.id.systemAppsRadio -> AppType.SYSTEM
                R.id.allAppsRadio -> AppType.ALL
                else -> AppType.USER
            }
            updateAppList(type)
        }

        // Spinner for sorting
        val sortOptions = listOf("Default", "Risk: High to Low", "Risk: Low to High")
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortAdapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Spinner for risk level filter
        val riskLevels = listOf("All", "High", "Medium", "Low", "Safe")
        val riskAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, riskLevels)
        riskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        riskFilterSpinner.adapter = riskAdapter

        riskFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                currentRiskFilter = riskLevels[position]
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // SearchView logic
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText.orEmpty()
                applyFilters()
                return true
            }
        })

        // Initial load
        updateAppList(AppType.USER)
    }
}
