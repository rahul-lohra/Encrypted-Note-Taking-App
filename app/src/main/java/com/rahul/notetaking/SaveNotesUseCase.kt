package com.rahul.notetaking

import android.content.Context
import com.rahul.notetaking.crypto.CryptoFileUtil
import com.rahul.notetaking.crypto.CryptoUseCase
import com.rahul.notetaking.crypto.DataToEncrypt
import com.rahul.notetaking.room.EncryptFileDatabase
import com.rahul.notetaking.room.EncryptFileEntity
import com.rahul.notetaking.room.EncryptStatus
import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class SaveNotesUseCase(val context: Context) {
    val TAG = "SaveNotesUseCase "
    private val cryptoUseCase = CryptoUseCase(context)
    private val dao = EncryptFileDatabase.getDatabase(context).encFileDao()

    suspend fun saveNotes(title: String, body: String) {
        cryptoUseCase.encrypt(DataToEncrypt(title, body))
            .catch {
                Timber.e(TAG + it)
            }
            .collect {
                when (it) {
                    is EncryptStatusWrapper.OnProgress -> {
                        dao.insert(
                            EncryptFileEntity(
                                it.fileName,
                                title,
                                EncryptStatus.ENC_ON_PROGRESS
                            )
                        )
                        Timber.d(TAG + it.fileName + "Enc onprogress")
                    }
                    is EncryptStatusWrapper.OnCompleted -> {
                        dao.update(it.fileName, EncryptStatus.ENC_COMPLETED)
                        Timber.d(TAG + it.fileName + "Enc finished")
                    }
                    is EncryptStatusWrapper.OnError -> {
                        Timber.e(TAG + it.fileName + "Enc error")
                        if (it.fileName.isNotEmpty()) {
                            dao.delete(it.fileName)
                        }
                    }
                }

            }
    }

    suspend fun updateNotes(id: Int, title: String, body: String) {
        val encryptFileEntity = dao.get(id)
        dao.update(id,title)

        cryptoUseCase.encrypt(DataToEncrypt(title, body, encryptFileEntity.fileName))
            .catch {
                Timber.e(TAG + it)
            }
            .collect {
                when (it) {
                    is EncryptStatusWrapper.OnProgress -> {
                        dao.update(id,EncryptStatus.ENC_ON_PROGRESS)
                        Timber.d(TAG + it.fileName + "Enc onprogress")
                    }
                    is EncryptStatusWrapper.OnCompleted -> {
                        dao.update(it.fileName, EncryptStatus.ENC_COMPLETED)
                        Timber.d(TAG + it.fileName + "Enc finished")
                    }
                    is EncryptStatusWrapper.OnError -> {
                        Timber.e(TAG + it.fileName + "Enc error")
                        //Do nothing
                    }
                }

            }
    }
}