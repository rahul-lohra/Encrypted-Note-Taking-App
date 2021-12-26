package com.rahul.notetaking.crypto

import android.security.keystore.KeyProperties
import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.Flow

interface CryptoContract {
    companion object {
        const val KEY_ALIAS = "key2"
        const val KEY_PURPOSE = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    }

    fun encrypt(dataToEncrypt: DataToEncrypt): Flow<EncryptStatusWrapper>
    fun decrypt(dataForDecrypt: DataForDecrypt): DecryptedBody
}

data class DataToEncrypt(val title: String, val body: String, val fileName: String? = null) {
    fun isUpdating() = !fileName.isNullOrEmpty()
}

data class DataForDecrypt(val id: Int, val fileName: String)
data class DecryptedData(val id: Int, val title:String, val body: String)
typealias DecryptedBody = String
