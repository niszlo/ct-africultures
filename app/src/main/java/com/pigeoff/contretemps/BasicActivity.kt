package com.pigeoff.contretemps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pigeoff.contretemps.client.HTTPClient
import com.pigeoff.contretemps.client.JSONSection
import com.pigeoff.contretemps.fragments.PostsFragment
import com.pigeoff.contretemps.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasicActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var progressBar: ProgressBar
    lateinit var http: HTTPClient

    var currentCategory = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        http = (application as CTApp).clientHttp

        val intentType = intent.getStringExtra(Util.ACTION_TYPE)
        val intentSlug = intent.getStringExtra(Util.ACTION_SLUG)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        frameLayout = findViewById(R.id.fragment_container)
        progressBar = findViewById(R.id.progressBar2)

        //Actionbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(Dispatchers.IO).launch {
            if (!intentSlug.isNullOrEmpty()) {
                var section = JSONSection()
                try {
                    when (intentType) {
                        Util.INTENT_CATEGORY -> {
                            section = http.service.getCategoryFromSlug(intentSlug).first()
                            val title = section.name

                            withContext(Dispatchers.Main) {
                                supportActionBar?.title = title
                                updateFragment(Util.FRAG_CATEGORY, section.id)
                            }
                        }
                        Util.INTENT_TAG -> {
                            section = http.service.getTagFromSlug(intentSlug).first()
                            val title = section.name

                            withContext(Dispatchers.Main) {
                                supportActionBar?.title = title
                                updateFragment(Util.FRAG_TAG, section.id)
                            }
                        }                                }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Util.alertError(findViewById(android.R.id.content), true)
                    }
                }
            }
            else {
                progressBar.visibility = View.GONE
                Util.alertError(findViewById(android.R.id.content), false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {

                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateFragment(type: Int, id: Int) {
        currentCategory = id
        progressBar.visibility = View.VISIBLE
        val fragment = PostsFragment(type, id, true,null)

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