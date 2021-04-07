package com.pigeoff.contretemps.client

class JSONPost {
    var id: Int = 0
    var date: String = ""
    var link: String = ""
    var title: HashMap<String, String> = HashMap<String, String>()
    var content: HashMap<String, String> = HashMap<String, String>()
    var featured_media: Int = 0
    var excerpt: HashMap<String, String> = HashMap<String, String>()
    var categories: ArrayList<Int> = arrayListOf()
    var tags: ArrayList<Int> = arrayListOf()
    var _links: HashMap<String, ArrayList<HashMap<String, String>>> = HashMap<String, ArrayList<HashMap<String, String>>>()
}