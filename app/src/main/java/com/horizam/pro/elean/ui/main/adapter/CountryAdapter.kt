package com.horizam.pro.elean.ui.main.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.databinding.ItemCountryListBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.fragments.SelectCountryBottomSheet
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class CountryAdapter(
    private var countriesList: ArrayList<SpinnerModel>,
    private var onItemClickListener: OnItemClickListener,
    private var selectCountryBottomSheet: SelectCountryBottomSheet
) :
    RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCountryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        holder.binding.apply {
            tvCountryName.text = countriesList[position].value
            Glide.with(holder.itemView)
                .load("${Constants.BASE_URL}${countriesList[position].image}")
                .centerCrop()
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.ic_error)
                .apply(requestOptions)
                .into(ivCountryFlag)
        }
        setClickListener(holder.binding, position)
    }

    private fun setClickListener(binding: ItemCountryListBinding, position: Int) {
        binding.root.setOnClickListener {
            onItemClickListener.onItemClick(countriesList[position])
            selectCountryBottomSheet.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return countriesList.size
    }

    fun filterList(filterdList: ArrayList<SpinnerModel>) {
        countriesList = filterdList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemCountryListBinding) : RecyclerView.ViewHolder(binding.root)
}