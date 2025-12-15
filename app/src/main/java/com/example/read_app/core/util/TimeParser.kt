package com.example.read_app.core.util

import java.time.Instant

object TimeParser {
    fun isoToEpochMs(iso: String?): Long? {
        if (iso.isNullOrBlank()) return null
        return runCatching { Instant.parse(iso).toEpochMilli() }.getOrNull()
    }
}
