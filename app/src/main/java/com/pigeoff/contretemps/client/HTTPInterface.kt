package com.pigeoff.contretemps.client

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HTTPInterface {
    /*
        @GET("users/{user}/repos")
        fun listRepos(@Path("user") user: String?): Call<MutableList<JSONPost?>?>?
    */

    //Get liste de POSTS
    @GET("posts")
    suspend fun getPostsByCategory(@Query("per_pages") maxPages: Int?, @Query("page") page: Int?) : ArrayList<JSONPost>

    @GET("posts")
    suspend fun getPostsByCategory(@Query("per_pages") maxPages: Int?, @Query("page") page: Int?, @Query("categories") categories: Int?) : ArrayList<JSONPost>

    @GET("posts")
    suspend fun getPostsByTags(@Query("per_pages") maxPages: Int?, @Query("page") page: Int?, @Query("tags") tags: Int?) : ArrayList<JSONPost>

    @GET("posts")
    suspend fun getPostsByAuthor(@Query("per_pages") maxPages: Int?, @Query("page") page: Int?, @Query("author") author: Int?) : ArrayList<JSONPost>


    //Get THING from SLUG
    @GET("posts")
    suspend fun getPostFromSlug(@Query("slug") slug: String?) : ArrayList<JSONPost>

    @GET("pages")
    suspend fun getPageFromSlug(@Query("slug") slug: String?) : ArrayList<JSONPost>

    @GET("tags")
    suspend fun getTagFromSlug(@Query("slug") slug: String?) : ArrayList<JSONSection>

    @GET("categories")
    suspend fun getCategoryFromSlug(@Query("slug") slug: String?) : ArrayList<JSONSection>

    //Get THING from ID
    @GET("posts/{id}")
    suspend fun getPostFromId(@Path("id") id: Int?) : JSONPost

    @GET("pages/{id}")
    suspend fun getPageFromId(@Path("id") id: Int?) : JSONPost

    //Get media
    @GET("media/{id}")
    suspend fun getPostImgCover(@Path("id") id: Int?) : JSONMedia

    @GET("posts")
    suspend fun searchForPost(@Query("per_pages") maxPages: Int?, @Query("page") page: Int?, @Query("search") search: String?) : ArrayList<JSONPost>

}