package com.example.read_app.core.util

import java.security.MessageDigest

object IdUtil {
    fun stableIdFrom(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-1")
            .digest(text.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
