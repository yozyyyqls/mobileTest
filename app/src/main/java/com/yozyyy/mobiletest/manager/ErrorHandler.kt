package com.yozyyy.mobiletest.manager

import android.util.Log

class ErrorHandler {
    fun handleException(error: Throwable): String {
        logError(error)
        return when(error) {
            is java.io.IOException -> {
                "Unable to connect to the server, please check your network connection and try again"
            }
            is org.json.JSONException,
            is com.google.gson.JsonParseException,
            is IllegalStateException,
            is NullPointerException -> {
                "There was an error in data processing, please refresh or try again later"
            }
            else -> {
                "An unknown error has occurred, please try again or contact customer service"
            }
        }
    }

    private fun logError(error: Throwable) {
        error.message?.let { Log.e("ErrorHandler", "fetch booking data fail, cause: $it") }
    }
}