package com.yanbin.ptpsample.storage

enum class FileExtension {
    Jpeg, Jpg, Png, Gif, Mp4, Json, Txt;

    fun matchesFilePath(path: String?): Boolean {
        return path?.endsWith(".$name", true) ?: false
    }
}