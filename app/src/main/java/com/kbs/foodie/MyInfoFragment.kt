package com.kbs.foodie

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.*
import androidx.fragment.app.Fragment

class MyInfoFragment : Fragment(R.layout.my_info_fragment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main= activity as MainActivity
        main.showSearchMenu()

        return super.onCreateView(inflater, container, savedInstanceState)
    }



}