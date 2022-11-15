package com.kbs.foodie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

class FriendAddFragment:Fragment(R.layout.friend_add_fragment) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main= activity as MainActivity
        main.hiddenMenu()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}