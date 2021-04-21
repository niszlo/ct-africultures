package com.pigeoff.contretemps.client

import android.util.Log
import com.pigeoff.contretemps.util.ListJSONPost
import com.pigeoff.contretemps.util.Util
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HTTPClient {

    var service: HTTPInterface
    val baseUrl = "https://www.contretemps.eu/wp-json/wp/v2/"
    val maxPagesConstante = 10

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(HTTPInterface::class.java)
    }

    suspend fun posts(page: Int, type: Int, id: Int, q: String?) {
        var request: Call<ArrayList<JSONPost>>? = null

        if (q.isNullOrEmpty()) {
            when (type) {
                Util.FRAG_CATEGORY -> {
                    request = if (id == 0) {
                        service.getPostsByCategory(maxPagesConstante, page)
                    } else {
                        service.getPostsByCategory(maxPagesConstante, page, id)
                    }
                }
                Util.FRAG_TAG -> {
                    request = service.getPostsByTags(maxPagesConstante, page, id)
                }
                Util.FRAG_AUHTOR -> {
                    request = service.getPostsByAuthor(maxPagesConstante, page, id)
                }
                else -> {
                    request = service.getPostsByCategory(maxPagesConstante, page)
                }
            }
        }

        else {
            request = service.searchForPost(maxPagesConstante, page, q)
        }

        request.enqueue(object : Callback<ArrayList<JSONPost>> {
            override fun onResponse(call: Call<ArrayList<JSONPost>>, response: Response<ArrayList<JSONPost>>) {
                val posts = response.body()
                val maxPosts = response.headers().get("X-WP-Total")?.toInt()
                val maxPages = response.headers().get("X-WP-TotalPages")?.toInt()

                onPostsFetchedListener?.onPostsFetchedListener(posts, maxPosts, maxPages)
            }

            override fun onFailure(call: Call<ArrayList<JSONPost>>, t: Throwable) {
                onPostsFetchedListener?.onPostsFetchedListener(null, null, null)
            }
        })
    }


    suspend fun searchForPost(page: Int, search: String) {
        val request = service.searchForPost(maxPagesConstante, page, search)

        request.enqueue(object : Callback<ArrayList<JSONPost>> {
            override fun onResponse(call: Call<ArrayList<JSONPost>>, response: Response<ArrayList<JSONPost>>) {
                val posts = response.body()
                val maxPosts = response.headers().get("X-WP-Total")?.toInt()
                val maxPages = response.headers().get("X-WP-TotalPages")?.toInt()

                onPostsFetchedListener?.onPostsFetchedListener(posts, maxPosts, maxPages)
            }

            override fun onFailure(call: Call<ArrayList<JSONPost>>, t: Throwable) {
                onPostsFetchedListener?.onPostsFetchedListener(null, null, null)
            }
        })
    }


    suspend fun getPostFromId(id: Int) : JSONPost {
        var post = JSONPost()
        try {
            post = service.getPostFromId(id)
            return if (post.id == 0) {
                try {
                    post = service.getPageFromId(id)
                    if (post.id == 0) {
                        post
                    } else {
                        post
                    }
                } catch (e:Exception) {
                    post
                }
            } else {
                post
            }
        }
        catch (e:Exception) {
            return post
        }
    }

    suspend fun getPostFromSlug(slug: String) : JSONPost {
        var post = JSONPost()
        try {
            post = service.getPostFromSlug(slug).first()
            return if (post.id == 0) {
                Log.i("INFO", "Pas de post")
                try {
                    post = service.getPageFromSlug(slug).first()
                    if (post.id == 0) {
                        post
                    } else {
                        post
                    }
                } catch (e:Exception) {
                    Log.e("ERROR", e.message.toString())
                    post
                }
            } else {
                post
            }
        }
        catch (e:Exception) {
            Log.i("INFO", "Pas de post")
            return try {
                post = service.getPageFromSlug(slug).first()
                if (post.id == 0) {
                    post
                } else {
                    post
                }
            } catch (e:Exception) {
                post
            }
        }
    }

    suspend fun returnImgCoverUrl(post: JSONPost) : String? {
        val idMedia = post.featured_media
        if (idMedia != 0) {
            val jsonMedia = service.getPostImgCover(idMedia)
            return if (jsonMedia != null) {
                val url = jsonMedia.source_url
                if (!url.isNullOrEmpty()) {
                   return url
                } else {
                    null
                }
            } else {
                null
            }
        }
        else {
            return null
        }
    }

    fun returnAuthorsFromPost(post: JSONPost) : ArrayList<String> {
        val authors = arrayListOf<String>()
        val doc = Jsoup.connect(post.link).get()
        val elmts = doc.getElementsByAttributeValue("rel", "author")
        for (e in elmts) {
            authors.add(e.text())
        }
        return authors
    }

    //Interface onPostsFetchedListener

    private var onPostsFetchedListener: OnPostsFetchedListener? = null

    interface OnPostsFetchedListener {
        fun onPostsFetchedListener(posts: ArrayList<JSONPost>?, maxPosts: Int?, maxPages: Int?)
    }

    fun setOnPostsFetchedListener(listener: OnPostsFetchedListener) {
        this.onPostsFetchedListener = listener
    }
}