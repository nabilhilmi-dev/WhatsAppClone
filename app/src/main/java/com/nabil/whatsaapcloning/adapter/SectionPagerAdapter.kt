package com.nabil.whatsaapcloning.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nabil.whatsaapcloning.activity.MainActivity
import com.nabil.whatsaapcloning.fragment.ChatsFragment
import com.nabil.whatsaapcloning.fragment.StatusListFragment
import com.nabil.whatsaapcloning.fragment.StatusUpdateFragment

class SectionPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){

    private val chatsFragment = ChatsFragment()
    private val statusUpdateFragment = StatusUpdateFragment()
    private val statusListFragment = StatusListFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> statusUpdateFragment // menempatkan StatusUpdateFragment di posisi pertama
            1 -> chatsFragment // ChatsFragment posisi kedua dalam adapter
            2 -> statusListFragment // StatusListFragment posisi ketiga dalam adapter
            else -> chatsFragment // menjadikan ChatsFragment default position
        }

    }

    override fun getCount(): Int {
        return 3

    }
}