package com.pigeoff.contretemps.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.iterator
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pigeoff.contretemps.CTApp
import com.pigeoff.contretemps.R
import com.pigeoff.contretemps.client.HTTPClient
import com.pigeoff.contretemps.client.JSONPost
import com.pigeoff.contretemps.client.JSONSection
import com.pigeoff.contretemps.fragments.PostsFragment
import com.pigeoff.contretemps.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    lateinit var http: HTTPClient
    var categoriesId = arrayListOf(0, 4, 2, 3, 9, 17, 22, 8, 7)
    var categoriesNames = arrayListOf(
        R.string.nav_home,
        R.string.nav_conjoncture,
        R.string.nav_theorie,
        R.string.nav_strategie,
        R.string.nav_culture,
        R.string.nav_histoire,
        R.string.nav_recits,
        R.string.nav_enquete,
        R.string.nav_dossier
    )
    var currentCategory = 0

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        http = (application as CTApp).clientHttp

        //INTENT
        val intentType = intent.getStringExtra(Util.ACTION_TYPE)
        val intentSlug = intent.getStringExtra(Util.ACTION_SLUG)

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        frameLayout = findViewById(R.id.fragment_container)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        //init Fragment 1
        CoroutineScope(Dispatchers.IO).launch {
            when (intentType) {
                Util.INTENT_CATEGORY -> {
                    if (!intentSlug.isNullOrEmpty()) {
                        var section = JSONSection()
                        try {
                            section = http.service.getCategoryFromSlug(intentSlug).first()
                            val title = section.name
                            val id = section.id
                            val slug = section.slug

                            withContext(Dispatchers.Main) {
                                uncheckedAllMenuItems()
                                supportActionBar?.title = title
                                updateFragment(Util.FRAG_CATEGORY, section.id)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progressBar.visibility = View.GONE
                                Util.alertError(findViewById(android.R.id.content), true)
                            }
                        }
                    }
                }
                Util.INTENT_TAG -> {
                    if (!intentSlug.isNullOrEmpty()) {
                        var section = JSONSection()
                        try {
                            section = http.service.getTagFromSlug(intentSlug).first()
                            val title = section.name
                            val id = section.id
                            val slug = section.slug

                            withContext(Dispatchers.Main) {
                                uncheckedAllMenuItems()
                                supportActionBar?.title = title
                                updateFragment(Util.FRAG_TAG, section.id)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progressBar.visibility = View.GONE
                                Util.alertError(findViewById(android.R.id.content), true)
                            }
                        }
                    }
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        updateFragment(Util.FRAG_CATEGORY, 0)
                    }
                }
            }
        }

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemHome -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(0))
                    toolbar.title = applicationContext.getString(R.string.app_name)
                }
                R.id.itemConjoncture -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(1))
                    toolbar.title = it.title
                }
                R.id.itemTheorie -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(2))
                    toolbar.title = it.title
                }
                R.id.itemStrategie -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(3))
                    toolbar.title = it.title
                }
                R.id.itemCulture -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(4))
                    toolbar.title = it.title
                }
                R.id.itemHistoire -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(5))
                    toolbar.title = it.title
                }
                R.id.itemRecits -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(6))
                    toolbar.title = it.title
                }
                R.id.itemEnquete -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(7))
                    toolbar.title = it.title
                }
                R.id.itemDossiers -> {
                    updateFragment(Util.FRAG_CATEGORY, categoriesId.get(8))
                    toolbar.title = it.title
                }
                R.id.itemInfo -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            it.isChecked = true
            drawerLayout.closeDrawer(Gravity.START)
            true
        }

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

    fun uncheckedAllMenuItems() {
        for (i in navigationView.menu) {
            i.isChecked = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(Gravity.START)
            }
            R.id.menu_main_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        }
        else {
            if (currentCategory == 0) {
                finish()
            }
            else {
                navigationView.menu.getItem(0).isChecked = true
                currentCategory = 0
                updateFragment(Util.FRAG_CATEGORY, 0)
                supportActionBar?.title = this.getString(categoriesNames.get(0))
            }
        }
    }
}