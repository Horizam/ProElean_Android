package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.data.model.SellerActionModel
import com.horizam.pro.elean.databinding.SellerActionListItemBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class SellerActionAdapter(
    var sellerActionList: ArrayList<SellerActionModel>,
    var listener: OnItemClickListener
) : RecyclerView.Adapter<SellerActionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SellerActionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTitle.text = sellerActionList[position].title
        holder.binding.ivImage.setImageResource(sellerActionList[position].image)
        holder.binding.sellerActionListItem.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return sellerActionList.size
    }

    class ViewHolder(val binding: SellerActionListItemBinding) : RecyclerView.ViewHolder(binding.root)

}