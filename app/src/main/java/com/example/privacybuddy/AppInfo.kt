package com.example.privacybuddy

data class AppInfo(
    val appName: String,
    val packageName: String,
    val permissions: List<String>,
    val riskScore: Int = 0, // Number of risky permissions
    val riskLevel: String = "Unknown" // e.g. Safe, Low, Medium, High
)
