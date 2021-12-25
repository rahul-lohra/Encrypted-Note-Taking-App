package com.rahul.notetaking.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.rahul.notetaking.R
import com.rahul.notetaking.notesList.fragments.NotesListFragment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val backClickListeners = arrayListOf<BackClickListeners>()

    fun registerBackClick(listener:BackClickListeners){
        backClickListeners.add(listener)
    }
    fun unRegisterBackClick(listener:BackClickListeners) {
        backClickListeners.remove(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        backClickListeners.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(getFragmentContainerId(), NotesListFragment.newInstance())
                .commit()
        }
    }

    fun getFragmentContainerId():Int = R.id.main_parent

    override fun onBackPressed() {
        backClickListeners.forEach {
            it.onBackClick()
        }
        super.onBackPressed()
    }
}

interface BackClickListeners{
    fun onBackClick()
}