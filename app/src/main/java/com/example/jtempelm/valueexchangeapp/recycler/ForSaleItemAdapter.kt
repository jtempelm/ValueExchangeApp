package com.example.jtempelm.valueexchangeapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jtempelm.valueexchangeapp.R
import com.example.jtempelm.valueexchangeapp.activity.ApiDriverActivity
import com.squareup.picasso.Picasso


class ForSaleItemAdapter(private val itemList: List<ForSaleItem>, private val apiDriverActivity: ApiDriverActivity) : RecyclerView.Adapter<ForSaleItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForSaleItemViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val photoView = inflater.inflate(
            R.layout.card_item,
            parent, false
        )

        return ForSaleItemViewHolder(photoView)
    }


    override fun onBindViewHolder(viewHolder: ForSaleItemViewHolder, position: Int) {
        viewHolder.name.text = itemList[position].name
        Picasso.get()
            .load(itemList[position].imageURL)
            .resize(64, 64)
            .into(viewHolder.image)

        viewHolder.price.text = itemList[position].price
        viewHolder.description.text = itemList[position].description
        viewHolder.buyNowButton.setOnClickListener { apiDriverActivity.sendBuyItemNowRequest(itemList[position]) }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }
}
