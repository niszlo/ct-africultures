package com.pigeoff.africultures.util

import com.pigeoff.africultures.client.JSONPost

data class ListJSONPost(
    var list: ArrayList<JSONPost> = arrayListOf(),
    var nbPosts: Int = 0
)
