package com.example.shakirtestproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.shakirtestproject.OnItemClickListener
import com.example.shakirtestproject.R
import com.example.shakirtestproject.models.CategoryModel

private const val TAG = "CategoriesListAdapter"
class CategoriesListAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val categoriesList: ArrayList<CategoryModel>
) : RecyclerView.Adapter<CategoriesListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item_category,
            parent, false
        )
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = categoriesList[position]
        holder.bind(data, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clItemParent: ConstraintLayout = itemView.findViewById(R.id.cl_item_parent)
        private val viewLine: View = itemView.findViewById(R.id.view_line)
        private val imgCategory: ImageView = itemView.findViewById(R.id.img_category)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)

        fun bind(categoryModel: CategoryModel, onItemClickListener: OnItemClickListener) {
            tvCategoryName.text = categoryModel.category_name
            if (categoryModel.show_selected_view) {
                viewLine.visibility = View.VISIBLE
            } else {
                viewLine.visibility = View.INVISIBLE
            }
            when (categoryModel.category_name) {
                "jewelery" -> {
                    imgCategory.load(R.drawable.jewellery) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                }
                "electronics" -> {
                    imgCategory.load(R.drawable.electronics) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                }
                "men's clothing" -> {
                    imgCategory.load(R.drawable.menclothing){
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                }
                "women's clothing" -> {
                    imgCategory.load(R.drawable.womenclothing){
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                }

            }

            clItemParent.setOnClickListener {
                onItemClickListener.onItemClick(categoryModel)
            }
        }

    }

}