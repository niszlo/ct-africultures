package com.pigeoff.contretemps.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pigeoff.contretemps.R
import com.pigeoff.contretemps.client.HTTPClient
import com.pigeoff.contretemps.client.JSONPost
import com.pigeoff.contretemps.util.Util
import kotlinx.coroutines.*

class PostActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var appBar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var swiperefresh: SwipeRefreshLayout

    var post = JSONPost()
    var authors = arrayListOf<String>()
    val http = HTTPClient()
    var webViewClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //BINDING
        appBar = findViewById(R.id.toolbar)
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swiperefresh = findViewById(R.id.swiperefresh)

        //ACTION BAR
        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        //INTENT
        val intentId = intent.getIntExtra(Util.ACTION_ID, 0)
        val intentSlug = intent.getStringExtra(Util.ACTION_SLUG)

        //WEBVIEW
        webView.settings.javaScriptEnabled = true

        CoroutineScope(Dispatchers.IO).launch {
            //Check if post is not null
            if (intentId != 0) {
                try {
                    post = http.getPostFromId(intentId)
                    try {
                        authors = http.returnAuthorsFromPost(post)
                    }
                    catch (e: Exception) {
                        println(e)
                    }
                }
                catch (e: Exception) {
                    Log.e("Error fetching post", e.message.toString())
                    problemLoadingPage(true)
                }
            }
            else if (!intentSlug.isNullOrEmpty()) {
                try {
                    post = http.getPostFromSlug(intentSlug)
                }
                catch (e: Exception) {
                    Log.e("Error fetching post", e.message.toString())
                    problemLoadingPage(true)
                }
            }

            //Setting webview
            if (post.id != 0) {
                updateWebView(post, authors)
            }
            else {
                problemLoadingPage(false)
            }


        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                if (swiperefresh.isRefreshing) {
                    swiperefresh.isRefreshing = false
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (request != null) {
                    if (request.isForMainFrame) {
                        val host = request.url.host
                        if (host.toString().contains("www.contretemps.eu")) {
                            val intent = Intent(this@PostActivity, IntentActivity::class.java)
                            intent.putExtra(Util.ACTION_URL, request.url.toString())
                            startActivity(intent)
                            return true
                        } else {
                            Log.i("URL Click", request.url.toString())
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = request.url
                            startActivity(intent)
                            return true
                        }
                    }
                    else {
                        return false
                    }
                } else {
                    return false
                }
            }
        }

        swiperefresh.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                updateWebView(post, authors)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_post_fav -> {

            }
            R.id.menu_post_share -> {
                val sendIntent: Intent = Intent().apply {
                    val str = "${post.title.get("rendered")} ${post.link}"
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, str)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            R.id.menu_post_browser -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(post.link)
                startActivity(intent)
            }
            R.id.menu_post_refresh -> {
                CoroutineScope(Dispatchers.IO).launch {
                    updateWebView(post, authors)
                }
            }
        }
        return true
    }

    suspend fun updateWebView(post: JSONPost, authors: ArrayList<String>) {
        withContext(Dispatchers.Main) {
            val encodedHtml = Util.postToHTML(post, authors)
            progressBar.visibility = View.VISIBLE
            webView.loadData(encodedHtml, "text/html; charset=utf-8", "base64")
            Log.i("POST title", post.title.toString())
        }
    }

    suspend fun problemLoadingPage(internet: Boolean) {
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE
            Util.alertError(findViewById(android.R.id.content), false)
        }
    }

}