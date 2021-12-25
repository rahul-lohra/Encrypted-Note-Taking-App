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
    fun decrypt(dataForDecrypt: DataForDecrypt):DecryptedData
}

data class DataToEncrypt(val title:String, val body:String)
data class DataForDecrypt(val title:String)
typealias DecryptedData = String
