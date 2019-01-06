package com.example.jtempelm.valueexchangeapp

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_api_driver.badRequestButton
import kotlinx.android.synthetic.main.activity_api_driver.goodRequestButton
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class ApiDriverActivity : AppCompatActivity() {

    private var appProperties: Properties = Properties()
    private var responseCode: Int = -1
    private var responseBody: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_driver)

        appProperties.load(baseContext.assets.open("app.properties"))

        goodRequestButton.setOnClickListener { sendValidRequest() }
        badRequestButton.setOnClickListener { sendInvalidRequest() }
    }

    private fun sendValidRequest() {
        val apiRequest = ApiRequest(
            verb = "POST",
            endpoint = "valueExchange",
            requestBody =
            "{\n" +
                    "\"merchantId\":1,\n" +
                    "\"customerId\":1,\n" +
                    "\"currency\":\"USD\",\n" +
                    "\"amount\":\"5.00\",\n" +
                    "\"productDescription\":\"Pack of socks\"\n" +
                    "}"
        )
        apiRequest.execute()
    }

    private fun sendInvalidRequest() {
        val apiRequest = ApiRequest(
            verb = "POST",
            endpoint = "valueExchange",
            requestBody =
            "{\n" +
                    "\"merchantId\": 1,\n" +
                    "\"customerId\": 1\n" +
                    "}" //create an invalid request simply by omitting required fields
        )
        apiRequest.execute()
    }

    inner class ApiRequest internal constructor(private val verb: String, private val endpoint: String, private val requestBody: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean {
            val apiUrl = appProperties.getProperty("rest.api.url")
            val apiProtocol = appProperties.getProperty("rest.api.protocol")
            val restApi = URL("$apiProtocol$apiUrl/$endpoint")

            val connection = restApi.openConnection() as HttpURLConnection
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
        }

        override fun onPostExecute(success: Boolean) {
            AlertDialog.Builder(this@ApiDriverActivity)
                .setTitle("Api Response")
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    // continue with delete
                }
                .setMessage("ResponseCode: $responseCode\nResponseBody:$responseBody")
                .show()
        }
    }
}
