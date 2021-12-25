package com.rahul.notetaking.notesList.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rahul.notetaking.notesList.viewmodels.NotesListViewModel
import com.rahul.notetaking.R
import com.rahul.notetaking.activity.MainActivity
import com.rahul.notetaking.notesDetail.fragments.NotesDetailFragment
import com.rahul.notetaking.notesList.adapters.NotesListAdapter
import com.rahul.notetaking.notesList.viewmodels.NotesListState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotesListFragment : Fragment() {

    companion object {
        fun newInstance() = NotesListFragment()
    }

    private lateinit var viewModel: NotesListViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var rv: RecyclerView
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: NotesListAdapter
    private val CHILD_RV = 0
    private val CHILD_TV = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.notes_list_fragment, container, false)
        fab = v.findViewById(R.id.fab)
        tvEmpty = v.findViewById(R.id.tvEmpty)

        rv = v.findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = NotesListAdapter()
        rv.adapter = adapter

        viewFlipper = v.findViewById(R.id.viewFlipper)
        viewFlipper.displayedChild = CHILD_TV

        viewModel = ViewModelProvider(this)[NotesListViewModel::class.java]

        setListeners()
        getData()
        return v
    }

    private fun getData() {
        viewModel.getNotes()
    }

    private fun setListeners() {
        fab.setOnClickListener {
            openDetailFragment()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listStateFlow.collect {
                    when (it) {
                        is NotesListState.LoadingState -> {
                            tvEmpty.text = "Loading"
                            viewFlipper.displayedChild = CHILD_TV
                        }
                        is NotesListState.SuccessState -> {
                            if (adapter.itemCount == 0 && it.list.isEmpty()) {
                                //show empty state
                                tvEmpty.text = "Empty"
                                viewFlipper.displayedChild = CHILD_TV
                            } else {
                                //show filled state
                                viewFlipper.displayedChild = CHILD_RV
                                adapter.submit(it.list)
                            }
                        }
                        is NotesListState.ErrorState -> {}
                    }
                }
            }
        }

    }

    private fun openDetailFragment() {
        if (activity is MainActivity) {
            val containerId = (activity as MainActivity).getFragmentContainerId()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(containerId, NotesDetailFragment.newInstance(), "notes_detail")
                ?.addToBackStack(null)
                ?.commit()
        }
    }
}