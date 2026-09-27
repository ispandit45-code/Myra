package com.example.controller

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.BatteryManager
import android.provider.Settings
import com.example.model.AppInfo
import com.example.service.TauAccessibilityService

class PhoneController(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private var cachedApps: List<AppInfo>? = null
    private var isTorchOn: Boolean = false

    fun getInstalledApps(forceRefresh: Boolean = false): List<AppInfo> {
        if (!forceRefresh && cachedApps != null) {
            return cachedApps!!
        }

        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val myPackage = context.packageName

        val apps = resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == myPackage) return@mapNotNull null

            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and
                    android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

            val aliases = buildAliasesForApp(appName, packageName)

            AppInfo(
                appName = appName,
                packageName = packageName,
                icon = icon,
                isSystemApp = isSystem,
                aliases = aliases
            )
        }.sortedBy { it.appName.lowercase() }

        cachedApps = apps
        return apps
    }

    private fun buildAliasesForApp(appName: String, packageName: String): List<String> {
        val list = mutableListOf<String>()
        val lowerName = appName.lowercase()
        val lowerPkg = packageName.lowercase()

        list.add(lowerName)
        list.add(lowerPkg)

        when {
            lowerPkg.contains("whatsapp") || lowerName.contains("whatsapp") -> {
                list.addAll(listOf("whatsapp", "व्हाट्सएप", "व्हॉट्सऐप", "व्ट्सएप", "चैट", "मैसेज"))
            }
            lowerPkg.contains("youtube") || lowerName.contains("youtube") -> {
                list.addAll(listOf("youtube", "यूट्यूब", "यू ट्यूब", "yt", "गाणे", "गाने", "विडियो", "वीडियो"))
            }
            lowerPkg.contains("camera") || lowerName.contains("camera") -> {
                list.addAll(listOf("camera", "कैमरा", "कैमरो", "फोटो", "तस्वीर", "pic"))
            }
            lowerPkg.contains("chrome") || lowerName.contains("chrome") || lowerPkg.contains("browser") -> {
                list.addAll(listOf("chrome", "क्रोम", "गूगल", "ब्राउज़र", "इंटरनेट", "नेट"))
            }
            lowerPkg.contains("settings") || lowerName.contains("settings") -> {
                list.addAll(listOf("settings", "सेटिंग्स", "सेटिंग", "फोन सेटिंग"))
            }
            lowerPkg.contains("gallery") || lowerPkg.contains("photos") || lowerName.contains("gallery") || lowerName.contains("photos") -> {
                list.addAll(listOf("gallery", "photos", "गैलरी", "फोटो", "तस्वीरें"))
            }
            lowerPkg.contains("dialer") || lowerPkg.contains("phone") || lowerName.contains("phone") || lowerName.contains("dialer") -> {
                list.addAll(listOf("phone", "dialer", "कॉल", "फोन", "डायल", "नंबर मिला"))
            }
            lowerPkg.contains("messaging") || lowerPkg.contains("mms") || lowerName.contains("message") -> {
                list.addAll(listOf("message", "messages", "मैसेज", "संदेश", "sms"))
            }
            lowerPkg.contains("deskclock") || lowerPkg.contains("clock") || lowerName.contains("clock") -> {
                list.addAll(listOf("clock", "alarm", "घड़ी", "घड़ी", "अलार्म", "टाइम"))
            }
            lowerPkg.contains("calculator") || lowerName.contains("calculator") -> {
                list.addAll(listOf("calculator", "कैलकुलेटर", "हिसाब", "जोड़ घटा"))
            }
            lowerPkg.contains("maps") || lowerName.contains("maps") -> {
                list.addAll(listOf("maps", "गूगल मैप्स", "नक्शा", "रास्ता", "लोकेशन"))
            }
            lowerPkg.contains("music") || lowerPkg.contains("audio") || lowerName.contains("music") -> {
                list.addAll(listOf("music", "गाने", "म्यूजिक", "संगीत", "गीत"))
            }
            lowerPkg.contains("contacts") || lowerName.contains("contacts") -> {
                list.addAll(listOf("contacts", "कांटेक्ट", "नंबर", "संपर्क"))
            }
            lowerPkg.contains("instagram") -> {
                list.addAll(listOf("instagram", "इंस्टाग्राम", "इंस्टा", "रील"))
            }
        }
        return list.distinct()
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun goHome(): Boolean {
        if (TauAccessibilityService.isConnected()) {
            val handled = TauAccessibilityService.goHome()
            if (handled) return true
        }
        return try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(homeIntent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openRecents(): Boolean {
        if (TauAccessibilityService.isConnected()) {
            return TauAccessibilityService.openRecents()
        }
        return false
    }

    fun openNotifications(): Boolean {
        if (TauAccessibilityService.isConnected()) {
            return TauAccessibilityService.openNotifications()
        }
        return false
    }

    fun toggleTorch(enable: Boolean? = null): Pair<Boolean, Boolean> {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return Pair(false, isTorchOn)
            val newState = enable ?: !isTorchOn
            cameraManager.setTorchMode(cameraId, newState)
            isTorchOn = newState
            Pair(true, isTorchOn)
        } catch (e: Exception) {
            Pair(false, isTorchOn)
        }
    }

    fun getBatteryLevel(): Pair<Int, Boolean> {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
            val isCharging = batteryManager?.isCharging ?: false
            Pair(level, isCharging)
        } catch (e: Exception) {
            Pair(-1, false)
        }
    }

    fun openSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openWifiSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openBluetoothSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openSoundSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openDialer(phoneNumber: String = ""): Boolean {
        return try {
            val uri = if (phoneNumber.isNotBlank()) {
                Uri.parse("tel:${Uri.encode(phoneNumber)}")
            } else {
                Uri.parse("tel:")
            }
            val intent = Intent(Intent.ACTION_DIAL, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun searchYouTube(query: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }

    fun searchWeb(query: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra("query", query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}
