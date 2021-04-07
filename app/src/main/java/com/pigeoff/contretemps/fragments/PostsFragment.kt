package com.pigeoff.contretemps.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pigeoff.contretemps.CTApp
import com.pigeoff.contretemps.R
import com.pigeoff.contretemps.adapters.PostAdapter
import com.pigeoff.contretemps.client.HTTPClient
import com.pigeoff.contretemps.client.JSONPost
import com.pigeoff.contretemps.util.Util
import kotlinx.coroutines.*
import java.lang.Runnable

class PostsFragment(private var type: Int, private var num: Int) : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var httpClient: HTTPClient
    lateinit var swiperefresh: SwipeRefreshLayout

    var allPosts = arrayListOf<JSONPost>()
    var mDataLoaded: OnDatasLoaded? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_posts, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiperefresh = view.findViewById(R.id.swiperefresh)
        recyclerView = view.findViewById(R.id.recyclerViewMain)

        //Init HTTP Client
        httpClient = (context?.applicationContext as CTApp).getHTTPClient()

        //Init RecyclerView (without nothing inside...)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = PostAdapter(context!!, recyclerView, httpClient, arrayListOf())


        //
        CoroutineScope(Dispatchers.IO).launch {
            addTenArticles()
        }

        swiperefresh.setOnRefreshListener {
            Log.i("INFO", "Refreshing view")
            CoroutineScope(Dispatchers.IO).launch {
                update(recyclerView)
            }
        }



    }

    override fun onResume() {
        super.onResume()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val adapter = recyclerView.adapter as PostAdapter

                if (!adapter.isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) { //bottom of list!
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i("Bttm recycler reached", "YES")
                            addTenArticles()
                        }
                    }
                }
            }
        })
    }

    interface OnDatasLoaded {
        fun onOnDatasLoadedListener(load: Boolean)
    }

    fun setOnDataLoaded(listener: OnDatasLoaded) {
        this.mDataLoaded = listener
    }

    suspend fun addTenArticles() {
        //On affiche la barre de progression
        withContext(Dispatchers.Main) {
            mDataLoaded?.onOnDatasLoadedListener(true)
        }

        //On récupère l'adapter
        val adapter = recyclerView.adapter as PostAdapter

        //On récupère les articles
        try {
            val newPosts = httpClient.posts(adapter.page, type, num)
            allPosts.addAll(newPosts)

            if (newPosts.count() != 0) {
                //On ajoute une page SEULEMENT si on a eu des résultats
                //Sinon ça veut dire soit qu'on a pas réussi à choper des articles
                //soit qu'on a
                adapter.page++
                Log.i("Pages", adapter.page.toString())
            }

            withContext(Dispatchers.Main) {
                adapter.insertItems(newPosts)
                mDataLoaded?.onOnDatasLoadedListener(false)
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
                adapter.isLoading = false
            }
        }
        catch (e: Exception) {
            Log.e("ERROR", "Error Occurred: ${e.message}")
            withContext(Dispatchers.Main) {
                mDataLoaded?.onOnDatasLoadedListener(false)
                Util.alertError(requireView(), true)
            }
        }
    }

    suspend fun update(recyclerView: RecyclerView) {
        Log.i("INFO", "Chargement page")
        val adapter = recyclerView.adapter as PostAdapter
        val newPosts = ArrayList<JSONPost>()
        var i = 1

        while (i <= adapter.itemCount/10) {
            try {
                val aPosts = httpClient.posts(i, type, num)
                newPosts.addAll(aPosts)
            }
            catch (e: Exception) {
                Log.e("Erreur updating recyclerview", e.message.toString())
            }
            i++
        }

        withContext(Dispatchers.Main) {
            adapter.updatePosts(newPosts)
            if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
        }
    }
}