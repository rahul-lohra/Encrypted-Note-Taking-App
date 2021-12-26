package com.rahul.notetaking.notesList.adapters

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.AsyncListDiffer
import com.rahul.notetaking.R

import android.view.LayoutInflater

class NotesListAdapter(val onItemClick: (NoteListItem) -> Unit) :
    RecyclerView.Adapter<NotesListViewHolder>() {
    private val mDiffer: AsyncListDiffer<NoteListItem> = AsyncListDiffer(this, DiffCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(NotesListViewHolder.LAYOUT_ID, parent, false)
        return NotesListViewHolder(onItemClick, view)
    }

    override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
        val item = mDiffer.currentList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size;
    }

    fun submit(list: List<NoteListItem>) {
        mDiffer.submitList(list)
    }
}

class NotesListViewHolder(val onItemClick: (NoteListItem) -> Unit, item: View) :
    RecyclerView.ViewHolder(item) {

    companion object {
        val LAYOUT_ID = com.rahul.notetaking.R.layout.list_item_notes
    }

    private val tvTitle = item.findViewById<AppCompatTextView>(R.id.tvTitle)
    private val tvBody = item.findViewById<AppCompatTextView>(R.id.tvBody)
    private val parent = item.findViewById<View>(R.id.list_item_parent)

    fun setData(data: NoteListItem) {
        tvTitle.text = data.title
        tvBody.text = data.body

        parent.setOnClickListener {
            onItemClick(data)
        }
    }
}


private class DiffCallback : DiffUtil.ItemCallback<NoteListItem>() {

    override fun areItemsTheSame(oldItem: NoteListItem, newItem: NoteListItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: NoteListItem, newItem: NoteListItem) =
        oldItem == newItem
}

data class NoteListItem(val id: Int, val title: String, val body: String)
