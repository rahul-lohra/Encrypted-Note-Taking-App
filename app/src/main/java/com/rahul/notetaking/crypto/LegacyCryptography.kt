package com.rahul.notetaking.crypto

import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.Flow

class LegacyCryptography: CryptoContract {

    override fun encrypt(dataToEncrypt: DataToEncrypt): Flow<EncryptStatusWrapper> {
        TODO("Not yet implemented")
    }

    override fun decrypt(dataForDecrypt: DataForDecrypt): DecryptedData {
        TODO("Not yet implemented")
    }
}