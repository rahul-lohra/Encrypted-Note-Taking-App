package com.rahul.notetaking.crypto

import android.content.Context
import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.Flow

class CryptoUseCase(context: Context) {

    private val legacyCryptography: CryptoContract = LegacyCryptography()
    private val modernCryptography: CryptoContract = ModernCryptography(context)

    private fun getCrypto():CryptoContract{
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            return modernCryptography
        }
        return legacyCryptography
    }
    fun encrypt(dataToEncrypt: DataToEncrypt): Flow<EncryptStatusWrapper> {
        return getCrypto().encrypt(dataToEncrypt)
    }

    fun decrypt(dataForDecrypt: DataForDecrypt) {
        getCrypto().decrypt(dataForDecrypt)
    }

}