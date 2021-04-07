package com.pigeoff.contretemps.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.pigeoff.contretemps.util.Util
import java.net.URL

class IntentActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Intent DATA
        val action: String? = intent?.action
        var intentUrl = intent?.dataString

        if (intent.getStringExtra(Util.ACTION_URL).toString().contains("http")) {
            intentUrl = intent.getStringExtra(Util.ACTION_URL)
        }

        if (!intentUrl.isNullOrEmpty() && intentUrl.contains("http")) {
            val url = URL(intentUrl)
            val urlPath = url.path.split("/").toMutableList()
            val path = mutableListOf<String>()
            for (i in urlPath) {
                if (!i.isNullOrEmpty()) {
                    path.add(i)
                }
            }

            //On vérifie qu'il ne s'agit pas de la page d'accueil
            openActivity(path)

        }
        else {
            finish()
        }
    }

    fun openActivity(path: List<String>) {
        when (path.count()) {
            0 -> {
                //Cas de la PAGE D'ACCUEIL
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            1 -> {
                //Cas d'une POST ou d'une PAGE
                Log.i("PATH", path.last())
                val intent = Intent(this, PostActivity::class.java)
                intent.putExtra(Util.ACTION_TYPE, Util.INTENT_POST)
                intent.putExtra(Util.ACTION_SLUG, path.last())
                startActivity(intent)
            }
            else -> {
                //Cas d'autre chose
                when (path.first()) {
                    "category" -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(Util.ACTION_TYPE, Util.INTENT_CATEGORY)
                        intent.putExtra(Util.ACTION_SLUG, path.last())
                        startActivity(intent)
                    }
                    "author" -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(Util.ACTION_TYPE, Util.INTENT_AUTHOR)
                        intent.putExtra(Util.ACTION_SLUG, path.last())
                        startActivity(intent)
                    }
                    "tag" -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(Util.ACTION_TYPE, Util.INTENT_TAG)
                        intent.putExtra(Util.ACTION_SLUG, path.last())
                        startActivity(intent)

                    }
                    else -> {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}