package com.rahul.notetaking.notesDetail.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.notetaking.GetNotesUseCase
import com.rahul.notetaking.SaveNotesUseCase
import com.rahul.notetaking.crypto.DecryptedData
import com.rahul.notetaking.notesList.adapters.NoteListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotesDetailViewModel(app: Application) : AndroidViewModel(app) {
    val notesFlow: MutableStateFlow<NotesDetailState> =
        MutableStateFlow(NotesDetailState.InitialState)
    private var oldDecryptedData: DecryptedData? = null
    private val getNotesUseCase = GetNotesUseCase(app)
    private val saveNotesUseCase = SaveNotesUseCase(app)

    fun saveNotes(title: String, body: String) {
        GlobalScope.launch(Dispatchers.IO) {

            if (title.isEmpty() && body.isEmpty()) {
                return@launch
            }
            if (oldDecryptedData != null) {
                if (oldDecryptedData!!.title == title && oldDecryptedData!!.body == body) {
                    return@launch
                }
                saveNotesUseCase.updateNotes(oldDecryptedData!!.id, title, body)
            }else{
                saveNotesUseCase.saveNotes(title, body)
            }

        }
    }

    fun getNotes(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                notesFlow.emit(NotesDetailState.LoadingState)
                oldDecryptedData = getNotesUseCase.getNotes(id)
                if (oldDecryptedData != null)
                    notesFlow.emit(NotesDetailState.SuccessState(oldDecryptedData!!))
                else
                    notesFlow.emit(NotesDetailState.ErrorState(Exception("Unknown")))
            } catch (th: Throwable) {
                notesFlow.emit(NotesDetailState.ErrorState(th))
            }
        }
    }
}

sealed class NotesDetailState {
    object InitialState : NotesDetailState()
    object LoadingState : NotesDetailState()
    class SuccessState(val data: DecryptedData) : NotesDetailState()
    class ErrorState(val th: Throwable) : NotesDetailState()
}