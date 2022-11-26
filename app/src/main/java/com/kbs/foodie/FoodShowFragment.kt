package com.kbs.foodie

import android.annotation.SuppressLint
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


@Suppress("DEPRECATION")
class FoodShowFragment: Fragment(R.layout.food_show_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var foodEditViewModel:MyInfoViewModel
    private lateinit var foodContentCollectionRef: CollectionReference
    private lateinit var UserContentCollectionRef: CollectionReference
    private lateinit var user:String
    val storage = Firebase.storage
    private val FoodStorageRef = storage.reference

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val main = activity as MainActivity
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
        println("FOOOODODODODODO SHOWWWWWW 왔냐")
        foodEditViewModel.myFoodData.observe(viewLifecycleOwner) {
           // main.onChangeFragment(this)
        }
        //화면 USER 정보
        UserContentCollectionRef.document(user).get()
            .addOnSuccessListener {
                showUserName.text = it["username"].toString()
                val profileImageRef = FoodStorageRef.child("/${it["userimage"]}")
                loadImage(profileImageRef, showUserImage)
            }.addOnFailureListener {}

        //수정화면으로 전환
        updateFoodContentButton.setOnClickListener {
            val FoodEditFragment = FoodEditFragment()
           println("updateFood")
        }
        //화면 음식정보 SHOW
        showFood(foodPos,showFoodNameText,showFoodLocationText,showFoodScoreEditText,showFoodReviewEditText,showFoodImage)

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
                showFoodNameText.text =
                    SpannableStringBuilder(getPositionFood?.name)
                showFoodLocationText.text =
                    SpannableStringBuilder(getPositionFood?.address)
                showFoodScoreEditText.text =
                    SpannableStringBuilder(getPositionFood?.score.toString())
                showFoodReviewEditText.text =
                    SpannableStringBuilder(getPositionFood?.review)
                val profileImageRef = FoodStorageRef.child("/${getPositionFood?.image}")
                loadImage(profileImageRef, showFoodImage)
            }

        }
    }
}