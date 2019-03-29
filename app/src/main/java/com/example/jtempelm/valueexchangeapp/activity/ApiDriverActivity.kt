package com.example.jtempelm.valueexchangeapp.activity

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jtempelm.valueexchangeapp.R
import com.example.jtempelm.valueexchangeapp.adapter.ForSaleItem
import com.example.jtempelm.valueexchangeapp.adapter.ForSaleItemAdapter
import kotlinx.android.synthetic.main.activity_api_driver.recyclerView
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class ApiDriverActivity : EncryptedTransferActivity() {

    private var appProperties: Properties = Properties()
    private var responseCode: Int = -1
    private var responseBody: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.jtempelm.valueexchangeapp.R.layout.activity_api_driver)

        appProperties.load(baseContext.assets.open("app.properties"))

        val list: List<ForSaleItem> = getData()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ForSaleItemAdapter(list, this)
        recyclerView.isNestedScrollingEnabled = false
    }

    private fun getData(): List<ForSaleItem> {
        val list = ArrayList<ForSaleItem>()
        list.add(ForSaleItem("Meow Mix", "https://img.chewy.com/is/image/catalog/149744_MAIN._AC_SL400_V1530105151_.jpg", "$5.00", "I want chicken, I want liver, meow mix meow mix please deliver!"))
        list.add(ForSaleItem("Pack of Socks", "https://s7.landsend.com/is/image/LandsEnd/461716_A515_LF_M28", "$10.00", "Comfy socks, a whole pack"))
        list.add(ForSaleItem("Holy Hand Grenade Of Antioch", "https://vignette.wikia.nocookie.net/readyplayerone/images/b/b9/Holy-Hand-Grenade.png/revision/latest", "$9,999,999.99", "20xD10 Holy Damage"))
        list.add(ForSaleItem("Plush Eagle", "https://cdn3.volusion.com/9nxdj.fchy5/v/vspfiles/photos/WR-18315-2.jpg?1442479716", "$20.00", "'Murica"))
        list.add(ForSaleItem("Maple Syrup", "https://cdn.shopify.com/s/files/1/0628/8453/products/Leaf_Bottle.jpg?v=1415141353", "$8.00", "The real deal, not that soft core corn syrup knockoff"))
        list.add(ForSaleItem("Russian Doll", "https://images-na.ssl-images-amazon.com/images/I/71v8RItTZwL._SL1000_.jpg", "$10.00", "Contains smaller dolls of itself"))
        return list
    }

    private fun sendValidRequest() {
        val apiRequest = ApiRequest(
            verb = "POST",
            endpoint = "valueExchange",
            requestBody =
            encryptRequestBody(
                "{\n" +
                        "\"merchantId\":1,\n" +
                        "\"customerId\":1,\n" +
                        "\"currency\":\"USD\",\n" +
                        "\"amount\":\"5.00\",\n" +
                        "\"productDescription\":\"Pack of socks\"\n" +
                        "}"
            )
        )
        apiRequest.execute()
    }

    inner class ApiRequest internal constructor(private val verb: String, private val endpoint: String, private val requestBody: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean {
            try {
                val apiUrl = appProperties.getProperty("rest.api.url")
                val apiProtocol = appProperties.getProperty("rest.api.protocol")
                val restApi = URL("$apiProtocol$apiUrl/$endpoint")

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
                titleCode = resources.getString(R.string.apiResponse)
                message = resources.getString(R.string.apiResponseMessage, responseCode) + responseBody
            } else {
                titleCode = resources.getString(R.string.apiError)
                message = resources.getString(R.string.networkError)
            }

            AlertDialog.Builder(this@ApiDriverActivity)
                .setTitle(titleCode.toString())
                .setPositiveButton(android.R.string.ok) { dialog, which -> }
                .setMessage(message)
                .show()
        }
    }
}
