package com.kbs.foodiewear


import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kbs.foodiewear.databinding.HomeItemBinding


class HomeAdapter(private val homeViewModel:HomeViewModel) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: HomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val db= Firebase.firestore
        fun setContents(pos:Int) {
            with(homeViewModel.contents[pos]){
                db.runTransaction{
                    val docRef=db.collection("user").document(email)
                    val snapshot=it.get(docRef)
                    val userName=snapshot.getString("username")?:""
//                    val userImage=snapshot.getString("userimage")?:""
                    binding.userName.text=userName
//                    println(userImage)
//                    val userImageRef=storageRef.child(userImage)
//                    loadImage(userImageRef,binding.userImage)
                }
                binding.listTvName.text=name
                binding.listTvAddress.text=address
                binding.listTvNumber.text=score.toString()
                //binding.listTvReview.text=review
                val imageRef=storageRef.child("/$image")
                loadImage(imageRef,binding.realImage)
            }
        }
        private fun loadImage(imageRef:StorageReference,view:ImageView){
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp=BitmapFactory.decodeByteArray(it,0,it.size)
                view.setImageBitmap(bmp)
            }.addOnFailureListener{
                view.setImageResource(R.drawable.img)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: HomeItemBinding = HomeItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount()=homeViewModel.contents.size


}