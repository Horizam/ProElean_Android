package com.horizam.pro.elean.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.AboutUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.GigsUserFragment
import com.horizam.pro.elean.ui.main.view.fragments.aboutUser.ReviewsUserFragment


internal class ViewPagerFragmentAdapter(
    fragmentActivity: FragmentActivity,
    listFragmentTitles: ArrayList<String>
) :
    FragmentStateAdapter(fragmentActivity) {

    private val titlesList = listFragmentTitles

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AboutUserFragment()
            1 -> return GigsUserFragment()
            2 -> return ReviewsUserFragment()
        }
        return AboutUserFragment()
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}