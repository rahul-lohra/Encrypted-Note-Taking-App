package com.rahul.notetaking.crypto

import android.security.keystore.KeyProperties

/*
* Ex -
* const val ENCRYPTION_ALGO = KeyProperties.KEY_ALGORITHM_AES
* const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
* const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
* */
abstract class EncryptionAlgo(val algo: String, val blockMode: String, val padding: String) {
    fun getCipherTrans() =  "$algo/$blockMode/$padding"
}

class AppEncryptionAlgo : EncryptionAlgo(
    KeyProperties.KEY_ALGORITHM_AES,
    KeyProperties.BLOCK_MODE_CBC,
    KeyProperties.ENCRYPTION_PADDING_PKCS7
)