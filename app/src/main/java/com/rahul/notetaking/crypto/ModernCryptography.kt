package com.rahul.notetaking.crypto

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.rahul.notetaking.room.EncryptStatusWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File


class ModernCryptography(private val context: Context) : CryptoContract {
    private val encryptionAlgo: EncryptionAlgo = AesGcmNoPaddingAlgo()

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
                if(dataToEncrypt.isUpdating()){
                    secretFile = CryptoFileUtil.getEncryptedFile(context, dataToEncrypt.fileName!!)
                }else{
                    secretFile = CryptoFileUtil.createEncryptedFile(context, dataToEncrypt.title)
                }

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
                Timber.e(th)
                emit(EncryptStatusWrapper.OnError(secretFile?.name))
            }

        }
    }

    /*
    * 2021-12-26 01:49:18.744 9943-9972/com.rahul.notetaking W/AndroidKeysetManager: keyset not found, will generate a new one
    java.io.FileNotFoundException: can't read keyset; the pref value __androidx_security_crypto_encrypted_file_keyset__ does not exist
        at com.google.crypto.tink.integration.android.SharedPrefKeysetReader.readPref(SharedPrefKeysetReader.java:71)
        at com.google.crypto.tink.integration.android.SharedPrefKeysetReader.readEncrypted(SharedPrefKeysetReader.java:89)
        at com.google.crypto.tink.KeysetHandle.read(KeysetHandle.java:105)
        at com.google.crypto.tink.integration.android.AndroidKeysetManager$Builder.read(AndroidKeysetManager.java:311)
        at com.google.crypto.tink.integration.android.AndroidKeysetManager$Builder.readOrGenerateNewKeyset(AndroidKeysetManager.java:287)
        at com.google.crypto.tink.integration.android.AndroidKeysetManager$Builder.build(AndroidKeysetManager.java:238)
        at androidx.security.crypto.EncryptedFile$Builder.build(EncryptedFile.java:200)
        at com.rahul.notetaking.crypto.ModernCryptography$encrypt$1.invokeSuspend(ModernCryptography.kt:90)
        at com.rahul.notetaking.crypto.ModernCryptography$encrypt$1.invoke(Unknown Source:8)
        at com.rahul.notetaking.crypto.ModernCryptography$encrypt$1.invoke(Unknown Source:4)
        at kotlinx.coroutines.flow.SafeFlow.collectSafely(Builders.kt:61)
        at kotlinx.coroutines.flow.AbstractFlow.collect(Flow.kt:212)
        at kotlinx.coroutines.flow.FlowKt__ErrorsKt.catchImpl(Errors.kt:230)
        at kotlinx.coroutines.flow.FlowKt.catchImpl(Unknown Source:1)
        at kotlinx.coroutines.flow.FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1.collect(SafeCollector.common.kt:113)
        at com.rahul.notetaking.SaveNotesUseCase.saveNotes(SaveNotesUseCase.kt:47)
        at com.rahul.notetaking.notesDetail.viewmodels.NotesDetailViewModel$saveNotes$1.invokeSuspend(NotesDetailViewModel.kt:21)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
        at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
2021-12-26 01:49:18.779 9943-9972/com.rahul.notetaking I/EngineFactory: Provider GmsCore_OpenSSL not available
2021-12-26 01:49:18.782 9943-9972/com.rahul.notetaking W/System.err: kotlinx.coroutines.JobCancellationException: Job was cancelled; job=SupervisorJobImpl{Cancelling}@996ef5
    *
    * */

    //TODO Rahul - we can optimise to read stream buffer by buffer
    @RequiresApi(Build.VERSION_CODES.M)
    override fun decrypt(dataForDecrypt: DataForDecrypt): DecryptedBody {
//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val secretFile = CryptoFileUtil.getEncryptedFile(context, dataForDecrypt.fileName)

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
//            return@use DecryptedData(dataForDecrypt.id, )
        }
    }
}