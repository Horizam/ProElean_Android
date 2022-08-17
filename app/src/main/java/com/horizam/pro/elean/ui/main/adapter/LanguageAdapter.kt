package com.horizam.pro.elean.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.data.model.LanguageChangeListeners
import com.horizam.pro.elean.data.model.LanguageList
import com.horizam.pro.elean.databinding.LanguageItemsBinding
import com.horizam.pro.elean.utils.PrefManager

class LanguagesAdapter(
    private var languageList: ArrayList<LanguageList>,
    var languageChangeListeners: LanguageChangeListeners
): RecyclerView.Adapter<LanguagesAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding:LanguageItemsBinding =
            LanguageItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class Holder(
        binding: LanguageItemsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var manager: PrefManager = PrefManager(App.getAppContext()!!)
        var binding: LanguageItemsBinding? = binding

        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int) {
            val language = languageList[position]
            binding!!.tvName.text=language.lName
           // binding!!.ivTick.isVisible = manager.setLanguage!!.equals(position.toInt())
            itemView.setOnClickListener {
                manager.setLanguage=position.toString()
            //    binding!!.ivTick.isVisible = true
                notifyDataSetChanged()
                languageChangeListeners.onLanguageChange()
            }
        }
    }
}

//class LanguageAdapter
//    //(val listener: OnItemClickListener) :
//    (var languageChangeListeners: LanguageChangeListeners
//): RecyclerView.Adapter<LanguagesAdapter.Holder>()
////    private var languageList: ArrayList<Language>,
////    //ListAdapter<String
////            , LanguageAdapter.DataViewHolder>(COMPARATOR) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
//        val binding = ItemSkillBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return DataViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    inner class DataViewHolder(private val binding: ItemSkillBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.root.setOnClickListener {
//                val position = bindingAdapterPosition
//                if (position!= RecyclerView.NO_POSITION){
//                    val lang = getItem(position)
//                    if (lang!=null){
//                        listener.onItemClick(lang)
//                    }
//                }
//            }
//        }
//
//        fun bind(lang: String) {
//            binding.apply {
//                tvSkillUser.text = lang
//            }
//        }
//    }
//
//    companion object{
//        private val COMPARATOR = object : DiffUtil.ItemCallback<String>(){
//            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
//                return oldItem == newItem
//            }
//
//            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
//                return oldItem == newItem
//            }
//
//        }
//    }
//}
//
