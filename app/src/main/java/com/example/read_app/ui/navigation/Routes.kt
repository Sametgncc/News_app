package com.example.read_app.ui.navigation


object Routes {
    const val HOME = "home"
    const val SAVED = "saved"
    const val DETAIL = "detail"
    const val DETAIL_ARG_ID = "id"

    fun detail(id: String) = "$DETAIL/$id"
}
