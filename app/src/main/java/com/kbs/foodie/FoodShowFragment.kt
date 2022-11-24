package com.kbs.foodie

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


@Suppress("DEPRECATION")
class FoodShowFragment: Fragment(R.layout.food_show_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var foodEditViewModel:MyInfoViewModel
    private lateinit var foodContentCollectionRef: CollectionReference
    private lateinit var user:String
    val storage = Firebase.storage
    val FoodStorageRef = storage.reference

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
        println(foodEditViewModel)
        foodEditViewModel.myFoodData.observe(viewLifecycleOwner) {
            val ft = requireFragmentManager().beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false)
            }
            ft.detach(this).attach(this).commit()
        }
        //화면 USER 정보
        db.runTransaction{
            val docRef=db.collection("user").document(user)
            val snapshot=it.get(docRef)
            val userName=snapshot.getString("username")?:""
            val userImage=snapshot.getString("userimage")?:""
            showUserName.text=userName
            val userImageRef=FoodStorageRef.child(userImage)
            loadImage(userImageRef,showUserImage)
        }
        //수정화면으로 전환
        updateFoodContentButton.setOnClickListener {

            findNavController().navigate(R.id.action_foodShowFragment_to_foodEditFragment)
        }
        //화면 음식정보 SHOW
        foodContentCollectionRef.get().addOnCompleteListener { task ->
            val getPositionFood = foodEditViewModel.getContent(foodPos)
            if (task.isSuccessful) {
                showFoodNameText.text=
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
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

}