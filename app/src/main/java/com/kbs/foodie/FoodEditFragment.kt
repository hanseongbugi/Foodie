package com.kbs.foodie

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FoodEditFragment: Fragment(R.layout.food_edit_fragment) {

    private val db: FirebaseFirestore = Firebase.firestore
    private val foodEditViewModel by viewModels<MyInfoViewModel>()
    private lateinit var foodContentCollectionRef: CollectionReference
    private lateinit var user:String


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val main = activity as MainActivity
        main.hiddenMenu()
        val rootView = inflater.inflate(R.layout.food_edit_fragment, container, false) as ViewGroup
        user=main.user
        val editFoodText = rootView.findViewById<TextView>(R.id.textView12)
        println(main.myInfoPos)
        editFoodText.text = "ㅇ ㅏ "

        return rootView
    }

}