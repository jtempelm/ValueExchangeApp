package com.example.jtempelm.valueexchangeapp.util

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AlertDialog
import com.example.jtempelm.valueexchangeapp.R
import java.net.HttpURLConnection
import java.net.URL

class ApiRequest internal constructor(private val apiURL: String, private val apiProtocol: String, private val verb: String, private val endpoint: String, private val requestBody: String, private val context: Context) : AsyncTask<Void, Void, Boolean>() {
    private var responseCode: Int = -1
    private var responseBody: String = ""

    override fun doInBackground(vararg params: Void): Boolean {
        try {

            val restApi = URL("$apiProtocol$apiURL/$endpoint")

            val connection = restApi.openConnection() as HttpURLConnection //TODO check if connection is bad, then abort with error!
            connection.requestMethod = verb
            connection.setRequestProperty("Content-Type", "application/json")

            if (requestBody != "") {
                connection.outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
            }

            connection.connect()

            responseCode = connection.responseCode
            responseBody = if (responseCode in 200..399) {
                connection.inputStream.bufferedReader().readText()
            } else {
                connection.errorStream.bufferedReader().readText()
            }

            connection.disconnect()

            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun onPostExecute(success: Boolean) {
        val titleCode: String
        val message: String
        if (success) {
            titleCode = context.resources.getString(R.string.apiResponse)
            message = context.resources.getString(R.string.apiResponseMessage, responseCode) + responseBody
        } else {
            titleCode = context.resources.getString(R.string.apiError)
            message = context.resources.getString(R.string.networkError)
        }

        AlertDialog.Builder(context)
            .setTitle(titleCode)
            .setPositiveButton(android.R.string.ok) { dialog, which -> }
            .setMessage(message)
            .show()
    }
}