package com.example.privacybuddy.utils

object AISuggester {

    fun getSuggestion(riskLevel: String, permissions: List<String>): String {
        return when (riskLevel) {
            "High" -> "⚠️ This app has a high privacy risk. Consider removing it if you don't use it regularly or if it requests sensitive permissions unnecessarily."
            "Medium" -> "This app has a moderate privacy risk. Review the permissions to ensure you're comfortable with them."
            "Low" -> "This app has low risk. It's generally safe, but still review permissions if it's from an unknown developer."
            "Safe" -> "✅ This app is considered safe and doesn't request any risky permissions."
            else -> "Privacy suggestion unavailable."
        }
    }
}
