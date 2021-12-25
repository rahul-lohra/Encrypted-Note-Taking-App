package com.rahul.notetaking.notesList.usecase

import android.content.Context
import com.rahul.notetaking.room.EncryptFileDatabase
import com.rahul.notetaking.room.EncryptFileEntity
import kotlinx.coroutines.flow.Flow

class NotesListUseCase(private val context: Context) {
    fun getAllNotes(): Flow<List<EncryptFileEntity>> {
        return EncryptFileDatabase.getDatabase(context).encFileDao()
            .getAll()
    }

    fun getAllNotesNonFlow(): List<EncryptFileEntity> {
        return EncryptFileDatabase.getDatabase(context).encFileDao()
            .getAllNonFlow()
    }
}