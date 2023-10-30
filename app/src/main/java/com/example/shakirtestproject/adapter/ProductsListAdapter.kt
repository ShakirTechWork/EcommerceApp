package com.example.shakirtestproject.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.shakirtestproject.OnItemClickListener
import com.example.shakirtestproject.R
import com.example.shakirtestproject.models.ProductsListItem
import com.example.shakirtestproject.models.QuantityClickActionModel

private const val TAG = "ProductsListAdapter"
class ProductsListAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val productsList: ArrayList<ProductsListItem>
) : RecyclerView.Adapter<ProductsListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item_product,
            parent, false
        )
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = productsList[position]
        holder.bind(data, onItemClickListener, position)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemParentLayout: CardView = itemView.findViewById(R.id.item_parent_layout)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        private val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        private val tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        private val tvAddProduct: TextView = itemView.findViewById(R.id.tv_add_product)
        private val llQuantityLayout: LinearLayout = itemView.findViewById(R.id.ll_quantity_layout)
        private val tvPlus: TextView = itemView.findViewById(R.id.tv_plus)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        private val tvMinus: TextView = itemView.findViewById(R.id.tv_minus)


        @SuppressLint("SetTextI18n")
        fun bind(data: ProductsListItem, onItemClickListener: OnItemClickListener, position: Int) {
            tvRating.text = "Rated ${data.rating.rate}"
            tvProductName.text =data.title
            imgProduct.load(data.image) {
                memoryCachePolicy(CachePolicy.ENABLED)
            }
            val productPrice = data.price

            tvProductPrice.text = "Price: ₹$productPrice"

            val itemQuantity = data.quantity.toString().toInt()

            tvQuantity.text = itemQuantity.toString()

            if (itemQuantity>0) {
                tvAddProduct.visibility = View.GONE
                llQuantityLayout.visibility = View.VISIBLE
            } else {
                tvAddProduct.visibility = View.VISIBLE
                llQuantityLayout.visibility = View.GONE
            }

            itemParentLayout.setOnClickListener {
                onItemClickListener.onItemClick(data.id)
            }

            tvAddProduct.setOnClickListener {
                val quantityClickActionModel = QuantityClickActionModel(data.id, data.title,data.image, itemQuantity+1,
                    productPrice, (productPrice * (itemQuantity+1)), "addThisItem", position)
                onItemClickListener.onItemClick(quantityClickActionModel)
            }

            tvPlus.setOnClickListener {
                val updatedItemQuantity = itemQuantity + 1
                val updateTotalPrice = updatedItemQuantity * productPrice
                val quantityClickActionModel = QuantityClickActionModel(data.id, data.title,data.image, updatedItemQuantity,
                    productPrice, updateTotalPrice, "increaseItemQuantity", position)
                Log.d(TAG, "tvPlus:   id:${quantityClickActionModel.productId}     quantity: ${quantityClickActionModel.quantity}         price: ${quantityClickActionModel.price}     total: ${quantityClickActionModel.totalItemPrice}   ")
                onItemClickListener.onItemClick(quantityClickActionModel)
            }

            tvMinus.setOnClickListener {

                if (itemQuantity > 1) {
                    val quantityClickActionModel = QuantityClickActionModel(data.id, data.title, data.image,
                        itemQuantity-1, productPrice, (productPrice * (itemQuantity-1)), "decreaseItemQuantity", position)
                    onItemClickListener.onItemClick(quantityClickActionModel)
                } else {
                    val quantityClickActionModel = QuantityClickActionModel(data.id, data.title, data.image, itemQuantity-1, productPrice,
                        (productPrice * (itemQuantity-1)), "removeThisItem",position)
                    onItemClickListener.onItemClick(quantityClickActionModel)
                }
            }
        }

    }
}
