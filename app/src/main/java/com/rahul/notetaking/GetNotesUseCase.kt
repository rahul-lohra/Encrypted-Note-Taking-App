package com.rahul.notetaking

import android.content.Context
import com.rahul.notetaking.crypto.*
import com.rahul.notetaking.room.EncryptFileDatabase
import com.rahul.notetaking.room.EncryptFileEntity
import com.rahul.notetaking.room.EncryptStatus
import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class GetNotesUseCase(val context: Context) {
    val TAG = "SaveNotesUseCase "
    private val cryptoUseCase = CryptoUseCase(context)
    private val dao = EncryptFileDatabase.getDatabase(context).encFileDao()

    suspend fun getNotes(id: Int): DecryptedData? {
        val encryptFileEntity = dao.get(id)
        if (!encryptFileEntity.fileName.isNullOrEmpty()) {
            val decryptedBody =
                cryptoUseCase.decrypt(DataForDecrypt(id, encryptFileEntity.fileName))
            return DecryptedData(id, encryptFileEntity.title, decryptedBody)
        }
        return null
    }
}