package com.linein.ptplib

import timber.log.Timber

object PtpLog {

    private val loggerTree = Timber.tag("PtpLib")

    @JvmStatic
    fun d(message: String) {
        loggerTree.d(message)
    }

    @JvmStatic
    fun e(message: String) {
        loggerTree.e(message)
    }

    @JvmStatic
    fun e(throwable: Throwable) {
        loggerTree.e(throwable)
    }
}