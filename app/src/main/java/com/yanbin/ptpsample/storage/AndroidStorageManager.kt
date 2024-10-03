package com.yanbin.ptpsample.storage

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.util.UUID
import javax.inject.Inject

class AndroidStorageManager @Inject constructor(
    private val context: Context
): StorageManager {

    private val sharedPreferences = context.getSharedPreferences("AndroidStorageManager", Context.MODE_PRIVATE)
    private val maxCacheSizeFlow = MutableStateFlow(getMaxCacheSize())
    private val usedCacheSizeFlow = MutableStateFlow(0L)

    override fun createRandomExternalFile(ext: FileExtension): File {
        val dir = context.getExternalFilesDir(null) ?: throw IllegalStateException("Missing external files dir")
        val fileName = "${createRandomFileName()}.${ext.name}"
        return File(dir, fileName)
    }

    override fun createThumbnailFile(hwId: String, fileName: String): File {
        return createImageFile(hwId, "thumb", fileName)
    }

    override fun createImageFile(hwId: String, fileDir: String, fileName: String): File {
        val rootDir = context.getExternalFilesDir(null) ?: throw IllegalStateException("Missing external files dir")
        val imageDir = File(rootDir, "${hwId}/${fileDir}")
        if (!imageDir.exists()) imageDir.mkdirs()

        return File(imageDir, fileName)
    }

    override fun createImageFile(hwId: String, fileName: String): File {
        val rootDir = context.getExternalFilesDir(null) ?: throw IllegalStateException("Missing external files dir")
        val imageDir = File(rootDir, hwId)
        if (!imageDir.exists()) imageDir.mkdirs()

        return File(imageDir, fileName)
    }

    private fun createRandomFileName(): String {
        return UUID.randomUUID().toString().substring(10)
    }

    override fun getImageFileDir(hwId: String): File {
        val rootDir = context.getExternalFilesDir(null) ?: throw IllegalStateException("Missing external files dir")
        val imageDir = File(rootDir, hwId)
        if (!imageDir.exists()) imageDir.mkdirs()
        return imageDir
    }

    override fun clearImageFiles(hwId: String) {
        val rootDir = context.getExternalFilesDir(null) ?: throw IllegalStateException("Missing external files dir")
        val imageDir = File(rootDir, hwId)
        if (imageDir.exists()) {
            imageDir.deleteRecursively()
        }
        notifyStorageStatusChanged()
    }

    override fun notifyStorageStatusChanged() {
        val rootDir = context.getExternalFilesDir(null) ?: throw IllegalStateException("Missing external files dir")

        val usedSize = rootDir.walkTopDown().sumOf { it.length() }
        usedCacheSizeFlow.value = usedSize
    }

    override fun setMaxCacheSize(sizeInMb: Int) {
        sharedPreferences.edit().putInt(KEY_MAX_CACHE_SIZE, sizeInMb).apply()
        maxCacheSizeFlow.value = sizeInMb
    }

    private fun getMaxCacheSize(): Int {
        return sharedPreferences.getInt(KEY_MAX_CACHE_SIZE, DEFAULT_MAX_CACHE_SIZE)
    }

    companion object {
        private const val KEY_MAX_CACHE_SIZE = "maxCacheSize"
        private const val DEFAULT_MAX_CACHE_SIZE = 200
    }
}