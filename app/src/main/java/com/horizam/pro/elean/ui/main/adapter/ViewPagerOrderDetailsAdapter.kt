package com.horizam.pro.elean.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.ui.main.view.fragments.MessagesFragment
import com.horizam.pro.elean.ui.main.view.fragments.OrderDetailsFragment
import com.horizam.pro.elean.ui.main.view.fragments.OrderMessagesFragment
import com.horizam.pro.elean.ui.main.view.fragments.TimelineFragment


internal class ViewPagerOrderDetailsAdapter(
    fragmentActivity: FragmentActivity,
    listFragmentTitles: ArrayList<String>,
    val order: Order,
    val pair: Pair<Int, Int>
) :
    FragmentStateAdapter(fragmentActivity) {

    val titlesList = listFragmentTitles

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return OrderDetailsFragment(order,pair)
            1 -> return TimelineFragment(order.orderNo)
            2 -> return OrderMessagesFragment(order)
        }
        return OrderDetailsFragment(order, pair)
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}