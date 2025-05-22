package com.example.privacybuddy

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.privacybuddy.utils.RiskAnalyzer

enum class AppType {
    USER, SYSTEM, ALL
}

object AppScanner {
    fun getFilteredApps(context: Context, type: AppType): List<AppInfo> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<AppInfo>()

        for (app in apps) {
            val isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUserApp = pm.getLaunchIntentForPackage(app.packageName) != null

            val include = when (type) {
                AppType.USER -> isUserApp && !isSystemApp
                AppType.SYSTEM -> isSystemApp
                AppType.ALL -> true
            }

            if (include) {
                try {
                    val packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
                    val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
                    val appName = pm.getApplicationLabel(app).toString()

                    val (riskLevel, riskScore) = RiskAnalyzer.analyzePermissions(permissions)

                    appList.add(
                        AppInfo(
                            appName = appName,
                            packageName = app.packageName,
                            permissions = permissions,
                            riskScore = riskScore,
                            riskLevel = riskLevel
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return appList.sortedBy { it.appName.lowercase() }
    }
}
