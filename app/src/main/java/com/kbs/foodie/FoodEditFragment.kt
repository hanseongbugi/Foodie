package com.kbs.foodie

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FoodEditFragment: Fragment(R.layout.food_edit_fragment) {

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
        val rootView = inflater.inflate(R.layout.food_edit_fragment, container, false) as ViewGroup
        user=main.user

        val foodPos = main.myInfoPos
        val editFoodNameText = rootView.findViewById<EditText>(R.id.nameEditText)
        val editFoodLocationText = rootView.findViewById<EditText>(R.id.locationEditText)
        val editFoodScoreEditText = rootView.findViewById<EditText>(R.id.scoreEditText)
        val editFoodReviewEditText = rootView.findViewById<EditText>(R.id.reviewEditText)
        val editFoodImage = rootView.findViewById<ImageView>(R.id.editFoodImage)
        val editFoodUpdateButton = rootView.findViewById<Button>(R.id.saveAndBackButton)
        val editFoodDeleteButton = rootView.findViewById<Button>(R.id.editFoodDeleteButton)

        foodEditViewModel= ViewModelProvider(requireActivity())[MyInfoViewModel::class.java]
        foodContentCollectionRef=db.collection("user").document(user)
            .collection("content")
        println(foodEditViewModel)

        //기존 foodContent SHOW
        foodContentCollectionRef.get().addOnCompleteListener { task ->
            val getPositionFood = foodEditViewModel.getContent(foodPos)
            if (task.isSuccessful) {
                editFoodNameText.text=
                    SpannableStringBuilder(getPositionFood?.name)
                editFoodLocationText.text =
                    SpannableStringBuilder(getPositionFood?.address)
                editFoodScoreEditText.text =
                    SpannableStringBuilder(getPositionFood?.score.toString())
                editFoodReviewEditText.text =
                    SpannableStringBuilder(getPositionFood?.review)
                val profileImageRef = FoodStorageRef.child("/${getPositionFood?.image}")
                loadImage(profileImageRef, editFoodImage)
            }
        }

        //food Content Update
        editFoodUpdateButton.setOnClickListener {
            val mScore = editFoodScoreEditText.text.toString()
            val mReview = editFoodReviewEditText.text.toString()
            val map = hashMapOf<String, Any>()
            map["score"] = mScore
            map["review"] = mReview
            foodContentCollectionRef.get().addOnCompleteListener { task ->
                val getPositionFood = foodEditViewModel.getContent(foodPos)
                val getPositionId = SpannableStringBuilder(getPositionFood?.id)
                if (task.isSuccessful) {
                    foodContentCollectionRef.document(getPositionId.toString()).update(map)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "UPDATE FOOD CONTENT COMPLETE!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.action_foodEditFragment_to_foodShowFragment)
                            }
                        }

                    }
            }
        }
        //food Content DELETE
        editFoodDeleteButton.setOnClickListener {

            val builder= AlertDialog.Builder(requireActivity())
            builder.setTitle("삭제하시겠습니까?")
                .setMessage("삭제하면 끝!")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        foodContentCollectionRef.get().addOnCompleteListener { task ->
                            val getPositionFood = foodEditViewModel.getContent(foodPos)
                            val getPositionId = SpannableStringBuilder(getPositionFood?.id)
                            if (task.isSuccessful) {
                                foodContentCollectionRef.document(getPositionId.toString()).delete().addOnSuccessListener {

                                }
                                FoodStorageRef.child(getPositionFood?.image.toString()).delete()
                                    .addOnSuccessListener {
                                         }
                            }

                        //navigation 이동

                            findNavController().navigate(R.id.action_foodEditFragment_to_foodShowFragment)
                        }.addOnFailureListener {}

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(requireContext(),"CANCEL", Toast.LENGTH_SHORT).show()

                    })
            // 다이얼로그를 띄워주기
            builder.show()

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

}