package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry{
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String =""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            Name = $name
            Artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    // values only get updated when they are accessed - that is by lazy
    private val downloadData by lazy {DownloadData(this, xmlListView)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"On Create Called")
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG,"onCreate: Done")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    companion object {
        // 1st- string containing the address of the Rss feed
        // 2nd is the progress bar parameter - void means we dont want a progress bar
        //3rd parameter is the type of information we want back, the xml data
        private class DownloadData(context: Context, listView:ListView): AsyncTask<String, Void, String>(){
            private val TAG = "DownloadData"

            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
               // Log.d(TAG,"onPostExecute: parameter is $result")
                val parseApplications = ParseApplications()
                parseApplications.parse(result)

//                val arrayAdapter = ArrayAdapter(propContext, R.layout.list_item, parseApplications.applications)
//                propListView.adapter = arrayAdapter
                val feedAdapter = FeedAdapter(propContext,R.layout.list_record, parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg URL: String?): String {
                Log.d(TAG,"doInBackground: starts with ${URL[0]}")
                val rssFeed = downloadXML(URL[0])
                if (rssFeed.isEmpty()){
                    Log.e(TAG,"doInBackground: Error Downloading")
                    // log.d is for debugs - debugs alerts are removed
                    // log.e is for errors
                }
                return rssFeed
            }
            private fun downloadXML(urlPath: String?): String{
            return URL(urlPath).readText()
            }
        }

    }


}
