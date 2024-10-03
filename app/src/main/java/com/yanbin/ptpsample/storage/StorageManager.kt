package com.yanbin.ptpsample.storage

import java.io.File

interface StorageManager {

    fun createRandomExternalFile(ext: FileExtension): File

    fun createThumbnailFile(hwId: String, fileName: String): File

    fun createImageFile(hwId: String, fileDir: String, fileName: String): File

    fun createImageFile(hwId: String, fileName: String): File

    fun getImageFileDir(hwId: String): File

    fun clearImageFiles(hwId: String)

    fun notifyStorageStatusChanged()

    fun setMaxCacheSize(sizeInMb: Int)
}