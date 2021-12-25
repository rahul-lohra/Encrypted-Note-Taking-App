package com.rahul.notetaking.notesDetail.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.rahul.notetaking.notesDetail.viewmodels.NotesDetailViewModel
import com.rahul.notetaking.R
import com.rahul.notetaking.activity.BackClickListeners
import com.rahul.notetaking.activity.MainActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotesDetailFragment : Fragment() {

    companion object {
        fun newInstance() = NotesDetailFragment()
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
        (activity as? MainActivity)?.registerBackClick(backClickListeners)
        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.unRegisterBackClick(backClickListeners)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[NotesDetailViewModel::class.java]
    }

}