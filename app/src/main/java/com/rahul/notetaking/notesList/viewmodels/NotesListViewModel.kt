package com.rahul.notetaking.notesList.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.notetaking.notesList.adapters.NoteListItem
import com.rahul.notetaking.notesList.usecase.NotesListUseCase
import com.rahul.notetaking.room.EncryptFileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesListViewModel(app: Application) : AndroidViewModel(app) {

    private val useCase = NotesListUseCase(app.applicationContext)
    val listStateFlow: MutableStateFlow<NotesListState> =
        MutableStateFlow(NotesListState.InitialState)

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            listStateFlow.emit(NotesListState.LoadingState)
            useCase.getAllNotes()
                .catch { e ->
                    listStateFlow.emit(NotesListState.ErrorState(e))
                }
                .map {
                    val itemList = arrayListOf<NoteListItem>()
                    it.filter {
                        !it.fileName.isNullOrEmpty()
                    }.forEach {
                        itemList.add(NoteListItem(it.uid, it.title, "some body"))
                    }
                    itemList
                }.collect {
                    listStateFlow.emit(NotesListState.SuccessState(it))
                }
        }
    }
}

sealed class NotesListState {
    object InitialState : NotesListState()
    object LoadingState : NotesListState()
    class SuccessState(val list: List<NoteListItem>) : NotesListState()
    class ErrorState(th: Throwable) : NotesListState()
}