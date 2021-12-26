package com.rahul.notetaking.notesDetail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rahul.notetaking.R
import com.rahul.notetaking.activity.BackClickListeners
import com.rahul.notetaking.activity.MainActivity
import com.rahul.notetaking.notesDetail.viewmodels.NotesDetailState
import com.rahul.notetaking.notesDetail.viewmodels.NotesDetailViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class NotesDetailFragment : Fragment() {

    companion object {
        private const val FILE_ID = "id"
        fun newInstance(id: Int? = null): NotesDetailFragment {
            val fragment = NotesDetailFragment()
            val bundle = Bundle()
            if (id != null) {
                bundle.putInt(FILE_ID, id)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var viewModel: NotesDetailViewModel
    private lateinit var etBody: EditText
    private lateinit var etTitle: EditText
    private lateinit var toolbar: Toolbar

    private val backClickListeners: BackClickListeners = object : BackClickListeners {
        override fun onBackClick() {
            viewModel.saveNotes(etTitle.text.toString(), etBody.text.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.notes_detail_fragment, container, false)
        etBody = v.findViewById(R.id.etBody)
        etTitle = v.findViewById(R.id.etTitle)
        toolbar = v.findViewById(R.id.toolbar)
        viewModel = ViewModelProvider(this)[NotesDetailViewModel::class.java]

        (activity as? MainActivity)?.registerBackClick(backClickListeners)
        setListeners()
        getNotes()
        return v
    }

    private fun setListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notesFlow.collect {
                    when (it) {
                        is NotesDetailState.LoadingState -> {
                            Timber.d("Loading")
                        }
                        is NotesDetailState.SuccessState -> {
                            etBody.setText(it.data.body)
                            etTitle.setText(it.data.title)
                        }
                        is NotesDetailState.ErrorState -> {
                            Timber.e(it.th)
                        }
                    }
                }
            }
        }
    }

    private fun getNotes() {
        val id = arguments?.getInt(FILE_ID)
        if (id != null && id != 0) {
            viewModel.getNotes(id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.unRegisterBackClick(backClickListeners)
    }
}