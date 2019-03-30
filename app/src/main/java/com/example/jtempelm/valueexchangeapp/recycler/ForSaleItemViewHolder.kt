package com.example.jtempelm.valueexchangeapp.recycler

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jtempelm.valueexchangeapp.R

class ForSaleItemViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var name: TextView = itemView.findViewById(R.id.name)
    internal var image: ImageView = itemView.findViewById(R.id.itemImage)
    internal var price: TextView = itemView.findViewById(R.id.itemPrice)
    internal var description: TextView = itemView.findViewById(R.id.description)
    internal var buyNowButton: Button= itemView.findViewById(R.id.buyNowButton)
}