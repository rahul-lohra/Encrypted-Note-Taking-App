package com.rahul.notetaking.crypto

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.rahul.notetaking.room.EncryptStatus
import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File


class ModernCryptography(private val context: Context) : CryptoContract {
    private val encryptionAlgo: EncryptionAlgo = AppEncryptionAlgo()

//    private fun loadExistingKey(): Key {
//        val keyStore = KeyStore.getInstance("AndroidKeyStore");
//        keyStore.load(null)
//        return keyStore.getKey("key2", null)
//    }

//    fun getKey(): Key {
//        try {
//            val key = loadExistingKey()
//            if (key != null) return key
//        } catch (th: Throwable) {
//            //Do Nothing
//        }
//        return generateKey()
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateMasterKey(): MasterKey {
        val keyGenSpec = KeyGenParameterSpec.Builder(
            CryptoContract.KEY_ALIAS,
            CryptoContract.KEY_PURPOSE
        ).setKeySize(256)
            .setBlockModes(encryptionAlgo.blockMode)
            .setEncryptionPaddings(encryptionAlgo.padding)
            .setRandomizedEncryptionRequired(false)
            .build()

        return MasterKey.Builder(context, CryptoContract.KEY_ALIAS)
            .setKeyGenParameterSpec(keyGenSpec)
            .build()


    }

//    fun generateKey(): Key {
//
//        val keyGenerator = KeyGenerator.getInstance(encryptionAlgo.algo, "AndroidKeyStore")
//        keyGenerator.init(
//            KeyGenParameterSpec.Builder(
//                CryptoContract.KEY_ALIAS,
//                CryptoContract.KEY_PURPOSE
//            )
//                .setBlockModes(encryptionAlgo.blockMode)
//                .setEncryptionPaddings(encryptionAlgo.padding)
//                .setRandomizedEncryptionRequired(false)
//                .build()
//        )
//        val key = keyGenerator.generateKey();
//        return key
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun encrypt(dataToEncrypt: DataToEncrypt): Flow<EncryptStatusWrapper> {
        return flow {
//                    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val mainKeyAlias = MasterKeys.getOrCreate(generateMasterKey)
            var secretFile: File? = null
            try {
                secretFile = CryptoFileUtil.createEncryptedFile(context, dataToEncrypt.title)
                if (secretFile.exists())
                    secretFile.delete()

                emit(EncryptStatusWrapper.OnProgress(secretFile.name))

                val encryptedFile = EncryptedFile.Builder(
                    context,
                    secretFile,
                    generateMasterKey(),
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()

                encryptedFile.openFileOutput()
                    .use {
                        return@use it.write(dataToEncrypt.body.toByteArray())
                    }
                emit(EncryptStatusWrapper.OnCompleted(secretFile.name))
            } catch (th: Throwable) {
                th.printStackTrace()
                Timber.e(th)
                emit(EncryptStatusWrapper.OnError(secretFile?.name))
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun decrypt(dataForDecrypt: DataForDecrypt): DecryptedData {
//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val secretFile = CryptoFileUtil.createEncryptedFile(context, dataForDecrypt.title)

//        val encryptedFile = EncryptedFile.Builder(
//            secretFile,
//            context,
//            mainKeyAlias,
//            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//        ).build()

        val encryptedFile = EncryptedFile.Builder(
            context,
            secretFile,
            generateMasterKey(),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return encryptedFile.openFileInput().use { inputStream ->
            return@use inputStream.readBytes().decodeToString()
        }
    }
}