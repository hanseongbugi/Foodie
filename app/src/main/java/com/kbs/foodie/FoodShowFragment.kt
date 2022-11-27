package com.kbs.foodie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text


class FoodShowFragment: Fragment(R.layout.food_show_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var foodEditViewModel:MyInfoViewModel
    private lateinit var foodContentCollectionRef: CollectionReference
    private lateinit var UserContentCollectionRef: CollectionReference
    private lateinit var user:String

    lateinit var storeName: String
    lateinit var storeLocation : String
    lateinit var storeScore: String
    lateinit var storeImage : String
    lateinit var storeReview : String
    lateinit var storeId : String

    val storage = Firebase.storage
    private val FoodStorageRef = storage.reference

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val main = activity as MainActivity
        main.backMainMenu=true
        main.hiddenMenu()
        val rootView = inflater.inflate(R.layout.food_show_fragment, container, false) as ViewGroup
        user=main.user
        val foodPos = main.myInfoPos
        val showFoodNameText = rootView.findViewById<TextView>(R.id.list_tv_name)
        val showFoodLocationText = rootView.findViewById<TextView>(R.id.list_tv_address)
        val showFoodScoreEditText = rootView.findViewById<TextView>(R.id.list_tv_number)
        val showFoodReviewEditText = rootView.findViewById<TextView>(R.id.list_tv_review)
        val showFoodImage = rootView.findViewById<ImageView>(R.id.real_image)

        val showUserName = rootView.findViewById<TextView>(R.id.userName)
        val showUserImage = rootView.findViewById<ImageView>(R.id.userImage)

        val updateFoodContentButton = rootView.findViewById<ImageButton>(R.id.updateFoodButton)
        foodEditViewModel= ViewModelProvider(requireActivity())[MyInfoViewModel::class.java]
        foodContentCollectionRef=db.collection("user").document(user)
            .collection("content")
        UserContentCollectionRef = db.collection("user")
        println(foodEditViewModel)
        foodEditViewModel.myFoodData.observe(viewLifecycleOwner) {

        }
        //화면 USER 정보
        UserContentCollectionRef.document(user).get()
            .addOnSuccessListener {
                showUserName.text = it["username"].toString()
                val profileImageRef = FoodStorageRef.child("/${it["userimage"]}")
                loadImage(profileImageRef, showUserImage)
            }.addOnFailureListener {}

        showFood(foodPos,showFoodNameText,showFoodLocationText,showFoodScoreEditText,showFoodReviewEditText,showFoodImage)
        //수정화면으로 전환
        updateFoodContentButton.setOnClickListener {
            findNavController().navigate(R.id.action_foodShowFragment_to_foodEditFragment)
        }
        //화면 음식정보 SHOW

        return rootView
    }
    private fun loadImage(imageRef: StorageReference, view: ImageView) {
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }.addOnFailureListener {
            view.setImageResource(R.drawable.img)
        }
    }
    fun showFood(foodPos: Int,showFoodNameText:TextView,showFoodLocationText: TextView, showFoodScoreEditText: TextView, showFoodReviewEditText: TextView, showFoodImage:ImageView) {

        foodContentCollectionRef.get().addOnCompleteListener { task ->
            val getPositionFood = foodEditViewModel.getContent(foodPos)
            if (task.isSuccessful) {
            storeId = SpannableStringBuilder(getPositionFood?.id).toString()
            storeName = SpannableStringBuilder(getPositionFood?.name).toString()
            storeLocation =
                SpannableStringBuilder(getPositionFood?.address).toString()
            storeScore =
                SpannableStringBuilder(getPositionFood?.score.toString()).toString()
            storeReview =
                SpannableStringBuilder(getPositionFood?.review).toString()
            storeImage = getPositionFood?.image.toString()
            showFoodNameText.text =storeName
            showFoodLocationText.text =storeLocation
            showFoodScoreEditText.text =storeScore
            showFoodReviewEditText.text =storeReview
            val profileImageRef = FoodStorageRef.child("/${storeImage}")
            loadImage(profileImageRef, showFoodImage)
        }

    }
    }
}