package com.pigeoff.africultures.client

import android.util.Log
import com.pigeoff.africultures.util.ListJSONPost
import com.pigeoff.africultures.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HTTPClient {

    var service: HTTPInterface
    val baseUrl = "http://africultures.com/wp-json/wp/v2/"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(HTTPInterface::class.java)
    }

    suspend fun posts(page: Int, type: Int, id: Int) : ArrayList<JSONPost> {
        var posts = arrayListOf<JSONPost>()
        when (type) {
            Util.FRAG_CATEGORY -> {
                if (id == 0) {
                    posts.addAll(service.getPostsByCategory(10, page))
                }
                else {
                    posts.addAll(service.getPostsByCategory(10, page, id))
                }
            }
            Util.FRAG_TAG -> {
                posts.addAll(service.getPostsByTags(10, page, id))
            }
            Util.FRAG_AUHTOR -> {
                posts.addAll(service.getPostsByAuthor(10, page, id))
            }
            else -> {
                posts.addAll(service.getPostsByCategory(10, page))
            }
        }
        return if (posts.isNullOrEmpty()) {
            arrayListOf()
        } else {
            posts
        }
    }

    suspend fun searchForPost(page: Int, search: String) : ArrayList<JSONPost> {
        val posts = service.searchForPost(10, page, search)

        return if (posts.isNullOrEmpty()) {
            arrayListOf()
        } else {
            posts
        }
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
}