package com.rahul.notetaking.notesDetail.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.notetaking.SaveNotesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotesDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val notesFlow = MutableStateFlow(0)
    private val useCase = SaveNotesUseCase(app)

    fun saveNotes(title: String, body: String) {
        viewModelScope.launch(Dispatchers.IO) {

            if (title.isEmpty() && body.isEmpty()) {
                return@launch
            }
            useCase.saveNotes(title, body)
        }

    }
}