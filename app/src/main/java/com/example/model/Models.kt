package com.example.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val aliases: List<String> = emptyList(),
    val launchCount: Int = 0
)

enum class MessageSender {
    USER,
    TAU
}

enum class ActionType {
    APP_LAUNCH,
    SYSTEM_ACTION,
    CONVERSATION,
    ERROR,
    HELP
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val actionType: ActionType = ActionType.CONVERSATION,
    val targetApp: AppInfo? = null,
    val actionDetails: String? = null,
    val latencyMs: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)
