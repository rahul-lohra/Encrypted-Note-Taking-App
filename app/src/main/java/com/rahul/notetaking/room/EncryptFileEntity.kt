package com.rahul.notetaking.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.IntDef
import com.rahul.notetaking.notesList.adapters.NoteListItem
import com.rahul.notetaking.room.EncryptStatus.Companion.ENC_COMPLETED
import com.rahul.notetaking.room.EncryptStatus.Companion.ENC_ON_PROGRESS


@Entity(tableName = "enc_file_table")
data class EncryptFileEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "file_name") val fileName: String?,
    @ColumnInfo(name = "enc_status") @EncryptStatus val encryptStatus: Int?
) {
    constructor(fileName: String?, encryptStatus: Int?) : this(0, fileName, encryptStatus)
}

sealed class EncryptStatusWrapper(val fileName: String) {
    class OnProgress(fileName: String) : EncryptStatusWrapper(fileName)
    class OnCompleted(fileName: String) : EncryptStatusWrapper(fileName)
    class OnError(fileName: String?) : EncryptStatusWrapper(fileName ?: "")
}

@IntDef(ENC_ON_PROGRESS, ENC_COMPLETED)
annotation class EncryptStatus {
    companion object {
        const val ENC_ON_PROGRESS = 0
        const val ENC_COMPLETED = 1
    }
}