package com.example.jtempelm.valueexchangeapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jtempelm.valueexchangeapp.R
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.net.URL


class ForSaleItemAdapter(private val itemList: List<ForSaleItem>, val context: Context) : RecyclerView.Adapter<ForSaleItemViewHolder>() {

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
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    private fun LoadImageFromWebOperations(url: String): Drawable? {
        val inputStream = URL(url).getContent() as InputStream
        return Drawable.createFromStream(inputStream, "src name") //src name useless arg here?
    }
}
