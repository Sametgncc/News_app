package com.example.read_app.core.util

sealed class NewsType(val countryCode: String) {
    object Local : NewsType(Constants.DEFAULT_COUNTRY)
    object Foreign : NewsType("us")
}
