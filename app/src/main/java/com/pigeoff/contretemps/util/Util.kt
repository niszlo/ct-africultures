package com.pigeoff.contretemps.util

import android.content.Context
import android.util.Base64
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.pigeoff.contretemps.R
import com.pigeoff.contretemps.client.JSONPost

class Util {
    companion object {
        val css="*,:after,:before{box-sizing:border-box;max-width:100% !important;}body{color:#444;font:16px/1.6 Georgia,Times New Roman,Times,serif;margin:40px auto;max-width:760px;padding:0 20px}img{max-width:100%;height:auto!important;}h1,h2,h3,h4,h5{font-family:Helvetica Neue,Helvetica,Arial,sans-serif;line-height:1.2}h1{display:block;padding-top:0;margin-top:0;}a{color:#07c;text-decoration:none}a:hover{color:#059;text-decoration:underline}hr{border:0;margin:25px 0}table{border-collapse:collapse;border-spacing:0;padding-bottom:25px;text-align:left}hr,td,th{border-bottom:1px solid #ddd}button,select{background:#ddd;border:0;font-size:14px;padding:9px 20px}input,table{font-size:1pc}blockquote{margin-left:0;margin-right:0;padding-left:1em;border-left:2px solid #444;}input,td,th{padding:5px;vertical-align:bottom}button:hover,code,pre{background:#eee}pre{padding:8px;white-space:pre-wrap}textarea{border-color:#ccc}.row{display:block;min-height:1px;width:auto}.row:after{clear:both;content:\"\";display:table}.row .c{float:left}.g2,.g3,.g3-2,.m2,.m3,.m3-2,table{width:100%}@media(min-width:8in){.g2{width:50%}.m2{margin-left:50%}.g3{width:33.33%}.g3-2{width:66.66%}.m3{margin-left:33.33%}.m3-2{margin-left:66.66%}}"
        val htmlHead = "<style>${css}</style><meta charset=\"utf-8\" > <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">"

        //Clés ACTIONS (Intent)
        val ACTION_TYPE = "actiontype"
        val ACTION_SLUG = "actionslug"
        val ACTION_ID = "actionsid"
        val ACTION_URL = "actionurl"

        //Clés INTENT (Intent)
        val INTENT_HOME = "intentauthor"
        val INTENT_AUTHOR = "intentauthor"
        val INTENT_CATEGORY = "intentcategory"
        val INTENT_TAG = "intenttag"
        val INTENT_POST = "intentpost"

        //Clés Fragment Type
        val FRAG_CATEGORY = 0
        val FRAG_TAG = 1
        val FRAG_AUHTOR = 2

        fun wpDateToString(str: String) : String {
            return if (str.length > 11) {
                val jour = str.subSequence(8, 10)
                val mois = str.subSequence(5, 7)
                val an = str.subSequence(0, 4)
                "${jour}/${mois}/${an}"
            } else {
                ""
            }
        }

        fun urlToSlug(url: String) : String {
            var txt = ""
            if (url.length > 25) {
                txt = url.substring(0, url.length - 1)
                txt = if (url.contains("https")) {
                    txt.substring(27)
                } else {
                    txt.substring(26)
                }
            }
            return txt
        }

        fun postToHTML(post: JSONPost, authors: ArrayList<String>) : String {
            val authors = if (!authors.isNullOrEmpty()) {
                " - par "+authors.joinToString(", ")+"</small><br/>"
            } else {
                "</small><br/>"
            }
            val content = post.content.get("rendered")
            val date = "<small style=\"color:#999\">Publié le ${Util.wpDateToString(post.date)}"
            val title = "<h1 style=\"display:block;padding-top:0;margin-top:-1em;\">${post.title.get("rendered")}</h1>"
            val html = htmlHead+title+date+authors+content
            val encodedHtml = Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
            return encodedHtml
        }

        fun alertError(view: View, internet: Boolean) {
            val text = if (internet) R.string.error_internet else R.string.error
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
        }
    }
}