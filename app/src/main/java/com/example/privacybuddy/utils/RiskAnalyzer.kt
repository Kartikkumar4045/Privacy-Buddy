package com.example.privacybuddy.utils

object RiskAnalyzer {

    val permissionRisks = mapOf(
        // Calendar
        "android.permission.READ_CALENDAR" to "Allows the app to read calendar events and details.",
        "android.permission.WRITE_CALENDAR" to "Allows the app to add or modify calendar events.",

        // Camera
        "android.permission.CAMERA" to "Allows the app to take pictures and record videos.",

        // Contacts
        "android.permission.READ_CONTACTS" to "Allows the app to read your contacts.",
        "android.permission.WRITE_CONTACTS" to "Allows the app to modify your contacts.",
        "android.permission.GET_ACCOUNTS" to "Allows access to the list of accounts in the Accounts Service.",

        // Location
        "android.permission.ACCESS_FINE_LOCATION" to "Allows access to precise location.",
        "android.permission.ACCESS_COARSE_LOCATION" to "Allows access to approximate location.",

        // Microphone
        "android.permission.RECORD_AUDIO" to "Allows the app to record audio.",

        // Phone
        "android.permission.READ_PHONE_STATE" to "Allows read-only access to phone state, like number and network info.",
        "android.permission.CALL_PHONE" to "Allows the app to initiate phone calls without user intervention.",
        "android.permission.READ_CALL_LOG" to "Allows the app to read your call log.",
        "android.permission.WRITE_CALL_LOG" to "Allows the app to modify your call log.",
        "android.permission.ADD_VOICEMAIL" to "Allows the app to add voicemails into the system.",
        "android.permission.USE_SIP" to "Allows the app to use SIP service.",
        "android.permission.PROCESS_OUTGOING_CALLS" to "Allows the app to monitor, modify, or abort outgoing calls.",

        // Sensors
        "android.permission.BODY_SENSORS" to "Allows access to body sensors like heart rate monitors.",

        // SMS
        "android.permission.SEND_SMS" to "Allows the app to send SMS messages.",
        "android.permission.RECEIVE_SMS" to "Allows the app to receive SMS messages.",
        "android.permission.READ_SMS" to "Allows the app to read SMS messages.",
        "android.permission.RECEIVE_WAP_PUSH" to "Allows the app to receive WAP push messages.",
        "android.permission.RECEIVE_MMS" to "Allows the app to receive MMS messages.",

        // Storage
        "android.permission.READ_EXTERNAL_STORAGE" to "Allows the app to read from external storage.",
        "android.permission.WRITE_EXTERNAL_STORAGE" to "Allows the app to write to external storage."
    )



    fun analyzePermissions(permissions: List<String>): Pair<String, Int> {
        var score = 0

        val highRiskKeywords = listOf("READ_SMS", "RECEIVE_SMS", "SEND_SMS", "RECORD_AUDIO", "CAMERA", "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION", "READ_CONTACTS", "READ_CALL_LOG", "WRITE_EXTERNAL_STORAGE", "READ_PHONE_STATE")
        val mediumRiskKeywords = listOf("INTERNET", "ACCESS_NETWORK_STATE", "VIBRATE", "WAKE_LOCK", "BLUETOOTH", "FOREGROUND_SERVICE")

        permissions.forEach { permission ->
            when {
                highRiskKeywords.any { keyword -> permission.contains(keyword) } -> score += 2
                mediumRiskKeywords.any { keyword -> permission.contains(keyword) } -> score += 1
            }
        }

        val level = when {
            score >= 7 -> "High"
            score in 4..6 -> "Medium"
            score in 1..3 -> "Low"
            else -> "Safe"
        }

        return Pair(level, score)
    }

    fun getRiskExplanation(permissions: List<String>): String {
        val riskyPermissions = permissions.filter { permissionRisks.containsKey(it) }
        return if (riskyPermissions.isEmpty()) {
            "This app doesn't request any risky permissions. It's considered safe."
        } else {
            val riskDescriptions = riskyPermissions.mapNotNull { permissionRisks[it] }
            "This app is considered risky because it requests the following permissions:\n\n" +
                    riskDescriptions.joinToString("\n") { "• $it" }

        }
    }

}
