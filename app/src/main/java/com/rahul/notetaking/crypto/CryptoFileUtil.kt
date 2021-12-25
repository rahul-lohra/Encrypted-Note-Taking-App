package com.rahul.notetaking.crypto

import android.content.Context
import java.io.File

object CryptoFileUtil {

    private fun createFileName(title: String): String {
        return title + "_" + System.currentTimeMillis()
    }

    fun createEncryptedFile(context: Context, title: String): File {
        val encryptedFile = File(context.filesDir, "app_encrypted")
        if (!encryptedFile.exists())
            encryptedFile.mkdirs()
        return File(encryptedFile, createFileName(title))
    }
}