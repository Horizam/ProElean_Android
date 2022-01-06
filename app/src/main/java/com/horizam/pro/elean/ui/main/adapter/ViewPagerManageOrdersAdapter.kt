package com.horizam.pro.elean.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.horizam.pro.elean.ui.main.view.fragments.manageOrders.*


internal class ViewPagerManageOrdersAdapter(
    fragmentActivity: Fragment,
    listFragmentTitles: ArrayList<String>
) :
    FragmentStateAdapter(fragmentActivity) {

    val titlesList = listFragmentTitles

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ActiveOrdersFragment()
            1 -> return LateOrdersFragment()
            2 -> return ManageOrdersFragment()
            3 -> return RevisionOrdersFragment()
            4 -> return CompletedOrdersFragment()
            5 -> return CancelOrdersFragment()
            6 -> return DisputedOrdersFragment()
        }
        return ActiveOrdersFragment ()
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}