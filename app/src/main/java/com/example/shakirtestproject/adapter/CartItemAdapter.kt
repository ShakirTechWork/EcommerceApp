package com.example.shakirtestproject.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.shakirtestproject.OnItemClickListener
import com.example.shakirtestproject.R
import com.example.shakirtestproject.models.CartItemModel
import com.example.shakirtestproject.models.QuantityClickActionModel
import com.example.shakirtestproject.utils.Utils

private const val TAG = "CartItemAdapter"

class CartItemAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val cartItemsList: ArrayList<CartItemModel>
) : RecyclerView.Adapter<CartItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item_cart,
            parent, false
        )
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = cartItemsList[position]
        holder.bind(position, data, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return cartItemsList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        private val tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val tvPricePerItem: TextView = itemView.findViewById(R.id.tv_price_per_item)
        private val tvMinus: TextView = itemView.findViewById(R.id.tv_minus)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        private val tvPlus: TextView = itemView.findViewById(R.id.tv_plus)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tv_total_price)
        @SuppressLint("SetTextI18n")
        fun bind(position: Int, data: CartItemModel, onItemClickListener: OnItemClickListener) {
            imgProduct.load(data.product_image)
            tvProductName.text = data.product_name
            tvPricePerItem.text = "Price per Item: ₹ ${data.price}"
            tvQuantity.text = data.quantity.toString()

            val productPrice = data.price

            tvPlus.setOnClickListener {
                val itemQuantity = data.quantity.toString().toInt()
                val quantityClickActionModel = QuantityClickActionModel(
                    data.product_id,
                    data.product_name,
                    data.product_image,
                    itemQuantity + 1,
                    productPrice,
                    (productPrice * (itemQuantity + 1)),
                    "increaseItemQuantity",
                    position
                )
                Log.d(TAG, "binder: $quantityClickActionModel")
                onItemClickListener.onItemClick(quantityClickActionModel)
            }

            tvMinus.setOnClickListener {
                val itemQuantity = data.quantity.toString().toInt()
                if (itemQuantity > 1) {
                    val quantityClickActionModel = QuantityClickActionModel(
                        data.product_id,
                        data.product_name,
                        data.product_image,
                        itemQuantity - 1,
                        productPrice,
                        (productPrice * (itemQuantity - 1)),
                        "increaseItemQuantity",
                        position
                    )
                    Log.d(TAG, "binder: $quantityClickActionModel")
                    onItemClickListener.onItemClick(quantityClickActionModel)
                } else {
                    val quantityClickActionModel = QuantityClickActionModel(
                        data.product_id,
                        data.product_name, data.product_image, itemQuantity,
                        productPrice, (productPrice * (itemQuantity)), "removeThisItem", position
                    )
                    Log.d(TAG, "removingThisItem: $quantityClickActionModel")
                    onItemClickListener.onItemClick(quantityClickActionModel)
                }
            }

            tvTotalPrice.text = "₹ ${Utils.formatDoubleValue(data.total_item_price)}"
        }
    }


}