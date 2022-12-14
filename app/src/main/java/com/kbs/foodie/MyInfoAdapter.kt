package com.kbs.foodie

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.databinding.FriendAddItemBinding
import com.kbs.foodie.databinding.MyInfoItemBinding

class MyInfoAdapter (private val myInfoViewModel: MyInfoViewModel,val profileUser:String) : RecyclerView.Adapter<MyInfoAdapter.ViewHolder>() {

    private var activity: MainActivity? = null
    interface OnItemClickListner{
        fun onItemClick(position: Int)
    }

    //전달된 객체를 저장할 변수 정의
    private lateinit var itemClickListner: OnItemClickListner

    fun setOnItemclickListner(onItemClickListner: OnItemClickListner){
        itemClickListner = onItemClickListner
    }


    inner class ViewHolder(private val binding: MyInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val db: FirebaseFirestore = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference

        fun setClickItem(pos: Int) {
            itemView.setOnClickListener {
               itemClickListner.onItemClick(pos)

            }
        }
        fun setContents(pos: Int) {
            with(myInfoViewModel.myFoods[pos]) {
                val userimageRef = storageRef.child("/${image}")
                loadImage(userimageRef, binding.myFoodImage)
            }


        }
        }
        private fun loadImage(imageRef: StorageReference, view: ImageView) {
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                view.setImageBitmap(bmp)
            }.addOnFailureListener {
                view.setImageResource(R.drawable.img)
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: MyInfoItemBinding =
            MyInfoItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(position)
        holder.setClickItem(position)
    }


    override fun getItemCount() =myInfoViewModel.myFoods.size


}