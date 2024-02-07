package com.example.chatmateai

import org.json.JSONObject

class Message(var role: String, var content: String) {
    companion object {
        const val SENT_BY_ME = "user"
        const val SENT_BY_BOT = "assistant"
    }
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("role", role)
            put("content", content)
        }
    }
}