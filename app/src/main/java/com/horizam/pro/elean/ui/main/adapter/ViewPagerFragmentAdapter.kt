package com.horizam.pro.elean.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.AboutUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.GigsUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.ReviewsUserFragment
import com.horizam.pro.elean.utils.PrefManager


internal class ViewPagerFragmentAdapter(
    fragmentActivity: Fragment,
    listFragmentTitles: ArrayList<String>,
    prefManager: PrefManager
) :
    FragmentStateAdapter(fragmentActivity) {

    private val titlesList = listFragmentTitles
    private val prefManager = prefManager

    override fun createFragment(position: Int): Fragment {
        if (prefManager.sellerMode == 1) {
            when (position) {
                0 -> return AboutUserFragment()
                1 -> return GigsUserFragment()
                2 -> return ReviewsUserFragment()
            }
        } else if (prefManager.sellerMode == 0) {
            when (position) {
                0 -> return AboutUserFragment()
                1 -> return ReviewsUserFragment()
            }
        }
        return AboutUserFragment()
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}