package com.kbs.foodie

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.databinding.FriendAddItemBinding

class FriendAddAdapter (private val friendAddViewModel:FriendAddViewModel,val currentUser:String) : RecyclerView.Adapter<FriendAddAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: FriendAddItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val db: FirebaseFirestore = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference
        val addFriendbutton = binding.friendAddButton
        val dbFriendEmail = db.collection("user").document(currentUser).collection("friend")

        fun setContents(pos: Int) {
            with(friendAddViewModel.userInfos[pos]) {
                    binding.friendNameText.text = username
                    binding.friendEmailText.text = email
                    val userimageRef = storageRef.child("/$userImage")
                    loadImage(userimageRef, binding.friendImage)
            }
            checkFriendCheck(pos)

        }
        private fun checkFriendCheck(pos: Int){
            val friendItems = friendAddViewModel.userInfos[pos]
            dbFriendEmail.get().addOnCompleteListener{
                task -> if(task.isSuccessful){
                    for(document in task.result!!){
                        if(document.id.equals(friendItems.email)){
                            addFriendbutton.text = "Friend"
                            addFriendbutton.isEnabled= false
                        }
                    }
            }
            }

        }
        fun addFriendItem(pos: Int){
            val friendItems = friendAddViewModel.userInfos[pos]
            val mfriendName = friendItems.username
            val mfriendemail = friendItems.email

            val itemMap = hashMapOf(
                "friendName" to mfriendName,
                "friendEmail" to mfriendemail

                )
            dbFriendEmail.document(mfriendemail).set(itemMap)
                .addOnSuccessListener {
                    println("친구추가 성공")

                }.addOnFailureListener {  }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: FriendAddItemBinding =
            FriendAddItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setContents(position)

        holder.addFriendbutton.setOnClickListener{
            //친구 추가 DB
            holder.addFriendItem(position)

        }

    }


    override fun getItemCount() = friendAddViewModel.userInfos.size

}