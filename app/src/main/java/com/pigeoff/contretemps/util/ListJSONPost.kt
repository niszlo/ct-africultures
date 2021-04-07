package com.pigeoff.contretemps.util

import com.pigeoff.contretemps.client.JSONPost

data class ListJSONPost(
    var list: ArrayList<JSONPost> = arrayListOf(),
    var nbPosts: Int = 0
)
