package com.pigeoff.contretemps.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import com.pigeoff.contretemps.R
import com.pigeoff.contretemps.fragments.PostsFragment
import kotlinx.android.synthetic.main.activity_main.*

class SearchActivity : AppCompatActivity() {
    lateinit var btnBack: ImageButton
    lateinit var btnClear: ImageButton
    lateinit var editSearch: EditText
    lateinit var progressBar: ProgressBar
    lateinit var frameContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //Binding
        btnBack = findViewById(R.id.btnBack)
        btnClear = findViewById(R.id.btnClear)
        editSearch = findViewById(R.id.editSearch)
        progressBar = findViewById(R.id.progressBar)
        frameContainer = findViewById(R.id.fragment_container)

        //Action bar
        btnBack.setOnClickListener {
            this.finish()
        }

        btnClear.setOnClickListener {
            editSearch.requestFocus()
            editSearch.setText("")
        }

        showKeyboard(editSearch, true)

        //Handle EditText
        editSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val txt = editSearch.text.toString()
                progressBar.visibility = View.VISIBLE
                showKeyboard(editSearch, false)
                updateFragment(txt)
            }
            true
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showKeyboard(edit: EditText, open: Boolean) {
        if (open) {
            edit.requestFocus()

        }
        else {
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            edit.clearFocus()
        }
    }

    fun updateFragment(search: String) {
        val fragment = PostsFragment(0, 0, false, search)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        fragment.setOnDataLoaded(object : PostsFragment.OnDatasLoaded {
            override fun onOnDatasLoadedListener(load: Boolean) {
                if (load) progressBar.visibility = View.VISIBLE
                else progressBar.visibility = View.GONE
            }
        })
    }
}