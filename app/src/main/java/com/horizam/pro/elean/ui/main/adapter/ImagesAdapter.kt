package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.Image
import com.horizam.pro.elean.databinding.ItemAddImageBinding
import com.horizam.pro.elean.databinding.ItemImageBinding
import com.horizam.pro.elean.ui.main.callbacks.ImagesHandler


class ImagesAdapter(private val imagesHandler: ImagesHandler) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding =
                ItemAddImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataViewHolderAdd(binding)
        } else {
            val binding =
                ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = list.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) {
            0
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) {
            holder.bind(list[position])
        }
    }

    fun addImages(list: ArrayList<Image>) {
        this.list = list
        notifyItemRangeChanged(0, list.size)
    }

    inner class DataViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivClose.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    //imagesHandler.removeImage(position)
                    val item = fetchItem()
                    if (item != null) {
                        imagesHandler.removeImage(item)
                    }
                }
            }
        }

        private fun fetchItem(): Image? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                list[position]
            } else {
                null
            }
        }

        fun bind(image: Image) {
            binding.apply {
                if (image.id == 0) {
                    showImage(image.path)
                } else if (image.id == 1) {
                    showImage(Constants.BASE_URL.plus(image.path))
                }
            }
        }

        private fun showImage(path: String) {
            Glide.with(itemView)
                .load(path)
                .placeholder(R.drawable.bg_splash)
                .into(binding.ivMain)
        }
    }

    inner class DataViewHolderAdd(private val binding: ItemAddImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                imagesHandler.addImages()
            }
        }

        fun bind(image: Image) {
            binding.apply {

            }
        }
    }
}