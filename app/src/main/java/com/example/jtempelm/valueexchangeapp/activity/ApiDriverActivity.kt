package com.example.jtempelm.valueexchangeapp.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jtempelm.valueexchangeapp.recycler.ForSaleItem
import com.example.jtempelm.valueexchangeapp.recycler.ForSaleItemAdapter
import com.example.jtempelm.valueexchangeapp.util.ApiRequest
import kotlinx.android.synthetic.main.activity_api_driver.recyclerView
import java.util.*

class ApiDriverActivity : EncryptedTransferActivity() {
    private var appProperties: Properties = Properties()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.jtempelm.valueexchangeapp.R.layout.activity_api_driver)

        appProperties.load(baseContext.assets.open("app.properties"))

        val list: List<ForSaleItem> = getData()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ForSaleItemAdapter(list, this)
        recyclerView.isNestedScrollingEnabled = false
    }

    fun sendBuyItemNowRequest(forSaleItem: ForSaleItem) {
        val apiRequest = ApiRequest(
            apiURL = appProperties.getProperty("rest.api.url"),
            apiProtocol = appProperties.getProperty("rest.api.protocol"),
            verb = "POST",
            endpoint = "valueExchange",
            requestBody =
            encryptRequestBody(
                "{\n" +
                        "\"merchantId\":1,\n" +
                        "\"customerId\":1,\n" +
                        "\"currency\":\"USD\",\n" +
                        "\"amount\":\"${forSaleItem.price}\",\n" + //we obviously shouldn't trust the client for the price of an item, it should be an ID the the server amount is used
                        "\"productDescription\":\"${forSaleItem.name}:${forSaleItem.description}\"\n" +
                        "}"
            ),
            context = this
        )
        apiRequest.execute()
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

}
