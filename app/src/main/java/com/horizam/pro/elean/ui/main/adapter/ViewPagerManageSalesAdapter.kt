package com.horizam.pro.elean.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.horizam.pro.elean.ui.main.view.fragments.LateSalesFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.AboutUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.GigsUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.ReviewsUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.manageOrders.*
import com.horizam.pro.elean.ui.main.view.fragments.manageSales.*


internal class ViewPagerManageSalesAdapter(
    fragmentActivity: Fragment,
    listFragmentTitles: ArrayList<String>
) :
    FragmentStateAdapter(fragmentActivity) {

    val titlesList = listFragmentTitles

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ActiveSalesFragment()
            1 -> return LateSalesFragment()
            2 -> return DeliveredSalesFragment()
            3 -> return RevisionSalesFragment()
            4 -> return CompletedSalesFragment()
            5 -> return CancelOrdersFragment()
            6 -> return DisputedSalesFragment()
        }
        return ActiveSalesFragment()
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}