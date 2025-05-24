package com.example.privacybuddy.utils

object RiskAnalyzer {

    // Map of permission → explanation
    private val permissionExplanations = mapOf(
        "android.permission.READ_SMS" to "Allows the app to read your SMS messages. Could be used to intercept OTPs or personal communication.",
        "android.permission.SEND_SMS" to "Allows the app to send SMS messages on your behalf. Be cautious with apps that might use this without your consent.",
        "android.permission.RECEIVE_SMS" to "Lets the app listen for incoming SMS messages. Often used for auto OTP read but could be abused.",
        "android.permission.READ_CONTACTS" to "Grants access to your contacts. May be used for social features or unsolicited communication.",
        "android.permission.WRITE_CONTACTS" to "Allows modifying your contacts. This can be dangerous if used improperly.",
        "android.permission.ACCESS_FINE_LOCATION" to "Allows precise location tracking. Useful for maps, but risky if unnecessary.",
        "android.permission.ACCESS_COARSE_LOCATION" to "Allows approximate location access. Still a privacy concern if not essential.",
        "android.permission.CAMERA" to "Gives access to your camera. Can be used to take photos or videos without you knowing.",
        "android.permission.RECORD_AUDIO" to "Allows recording audio from the microphone. Could be a major privacy issue if misused.",
        "android.permission.READ_PHONE_STATE" to "Grants access to phone info like IMEI, network status, etc. Often not essential for normal apps.",
        "android.permission.CALL_PHONE" to "Lets the app initiate phone calls without your interaction.",
        "android.permission.READ_EXTERNAL_STORAGE" to "Allows reading files from your device. Could expose personal photos, documents, etc.",
        "android.permission.WRITE_EXTERNAL_STORAGE" to "Allows modifying or deleting your files. Be careful with apps that don’t need this.",
        "android.permission.INTERNET" to "Allows the app to access the internet. Common, but should be monitored in combination with other sensitive permissions.",
        "android.permission.BLUETOOTH" to "Can interact with Bluetooth devices, which may include sensitive health data.",
        "android.permission.BODY_SENSORS" to "Accesses health sensor data. Used in fitness apps but may be privacy-invasive elsewhere.",
        "android.permission.GET_ACCOUNTS" to "Accesses account list on device — can be used for impersonation or targeted phishing.",
        "android.permission.SYSTEM_ALERT_WINDOW" to "Allows the app to display overlays — can be used for phishing attacks.",
        "android.permission.REQUEST_INSTALL_PACKAGES" to "Can request installing other apps. Dangerous if misused.",
        "android.permission.RECEIVE_BOOT_COMPLETED" to "Lets the app auto-start when your phone boots up. Can affect performance or spy silently.",
        "android.permission.ACCESS_BACKGROUND_LOCATION" to "Tracks your location even in the background. Highly privacy-invasive."
    )

    // Risk explanation based on permissions
    fun getRiskExplanation(permissions: List<String>): String {
        if (permissions.isEmpty()) return "This app does not use any sensitive permissions."

        val matched = permissions.mapNotNull { permission ->
            permissionExplanations.entries.firstOrNull {
                permission.contains(it.key, ignoreCase = true)
            }?.value
        }

        return if (matched.isNotEmpty()) {
            "This app uses the following sensitive permissions:\n\n" + matched.distinct().joinToString("\n\n")
        } else {
            "This app uses some permissions that might affect your privacy, but no critical risks were detected based on known patterns."
        }
    }

    // Suggestions for dangerous permissions
    private val permissionSuggestions = mapOf(
        "android.permission.READ_SMS" to "Avoid apps that can read your SMS unless absolutely necessary.",
        "android.permission.SEND_SMS" to "Sending SMS can cost money or be used maliciously. Be cautious.",
        "android.permission.RECEIVE_SMS" to "Receiving SMS allows reading your OTPs or private messages — only allow in trusted apps.",
        "android.permission.READ_CONTACTS" to "Access to contacts can leak private relationships and identities.",
        "android.permission.ACCESS_FINE_LOCATION" to "Precise location access can track your movements.",
        "android.permission.ACCESS_COARSE_LOCATION" to "Approximate location is still sensitive — prefer turning it off.",
        "android.permission.CAMERA" to "Camera access may allow apps to record photos or videos secretly.",
        "android.permission.RECORD_AUDIO" to "Microphone access may allow eavesdropping. Disable if not essential.",
        "android.permission.READ_CALENDAR" to "Access to calendar events could leak private appointments or meetings.",
        "android.permission.WRITE_EXTERNAL_STORAGE" to "Writing to storage can expose your files to manipulation.",
        "android.permission.READ_EXTERNAL_STORAGE" to "Reading storage may let apps scan your personal files.",
        "android.permission.CALL_PHONE" to "This permission lets the app make phone calls — be extra careful.",
        "android.permission.READ_PHONE_STATE" to "Allows access to your phone number, current call state, and more.",
        "android.permission.INTERNET" to "While common, internet access can be abused to send data remotely.",
        "android.permission.BLUETOOTH" to "Bluetooth access may allow device pairing or location estimation.",
        "android.permission.ACCESS_BACKGROUND_LOCATION" to "Background location is highly invasive. Avoid apps with this unless necessary.",
        "android.permission.GET_ACCOUNTS" to "Allows access to account list — could be used to impersonate users.",
        "android.permission.USE_CREDENTIALS" to "Can be used to access account credentials. Avoid unless essential.",
        "android.permission.SYSTEM_ALERT_WINDOW" to "This can create overlay attacks. Grant only to trusted apps.",
        "android.permission.REQUEST_INSTALL_PACKAGES" to "Allows silent installation of other apps — very dangerous if misused.",
        "android.permission.RECEIVE_BOOT_COMPLETED" to "Runs app code at boot — check why this is needed."
    )

    // Generates suggestion string
    fun getPermissionSuggestion(permissions: List<String>): String {
        val suggestions = permissions.mapNotNull { permission ->
            permissionSuggestions.entries.firstOrNull {
                permission.contains(it.key, ignoreCase = true)
            }?.value
        }

        return if (suggestions.isNotEmpty())
            suggestions.distinct().joinToString("\n\n")
        else
            "No serious threats found, but always review permission usage in app settings."
    }

    // Analyze score and risk level
    fun analyzePermissions(permissions: List<String>): Pair<String, Int> {
        val riskyPermissions = listOf(
            "android.permission.READ_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.SEND_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_CALL_LOG",
            "android.permission.REQUEST_INSTALL_PACKAGES",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.ACCESS_BACKGROUND_LOCATION"
        )

        var score = 0
        for (permission in permissions) {
            if (riskyPermissions.any { permission.contains(it, ignoreCase = true) }) {
                score += 10
            }
        }

        val level = when {
            score >= 30 -> "High"
            score >= 10 -> "Medium"
            else -> "Low"
        }

        return Pair(level, score)
    }
}
