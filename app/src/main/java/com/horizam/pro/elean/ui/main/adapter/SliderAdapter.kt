package com.horizam.pro.elean.ui.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.SliderItem
import com.horizam.pro.elean.ui.main.view.fragments.HomeFragment
import com.horizam.pro.elean.ui.main.view.fragments.HomeFragmentDirections
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter(private val context: HomeFragment) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {

    private var mSliderItems: MutableList<SliderItem> = ArrayList()

    fun renewItems(sliderItems: MutableList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: SliderItem = mSliderItems[position]
        viewHolder.textViewDescription.text = sliderItem.description
        viewHolder.textViewDescription.textSize = 16f
        viewHolder.textViewDescription.setTextColor(Color.WHITE)
        Glide.with(viewHolder.itemView)
            .load(sliderItem.url)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
        viewHolder.itemView.setOnClickListener {
            val id = "cU1VOWVkQVVpVElJdll4eThYOXpBZz09"
            val action = HomeFragmentDirections.actionHomeFragmentToServiceCategoriesFragment(id)
            context.findNavController().navigate(action)
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        var imageGifContainer: ImageView = itemView.findViewById(R.id.iv_gif_container)
        var textViewDescription: TextView = itemView.findViewById(R.id.tv_auto_image_slider)

    }

}