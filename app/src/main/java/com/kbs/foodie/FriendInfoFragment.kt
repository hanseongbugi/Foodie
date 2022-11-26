package com.kbs.foodie

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FriendInfoFragment : Fragment(R.layout.my_info_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter: MyInfoAdapter? = null
    private lateinit var profileViewModel:MyInfoViewModel
    private lateinit var profileContentCollectionRef: CollectionReference
    private lateinit var contentCollectionRef: CollectionReference
    private lateinit var friendNumcontentCollectionRef: CollectionReference
    lateinit var profileUser: String
    val storage = Firebase.storage
    val profileStorageRef = storage.reference
    lateinit var main:MainActivity
    var userName:String?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        main = activity as MainActivity
        main.backMainMenu=true
        main.hiddenMenu()
        main.removeBottomNavigation()
        userName=main.friendName
        val rootView = inflater.inflate(R.layout.my_info_fragment, container, false) as ViewGroup
        val profileUserRecyclerView = rootView.findViewById<RecyclerView>(R.id.profileRecyclerView)
        val profileUserNameText = rootView.findViewById<TextView>(R.id.profileNameText)
        val profileUserEmailText = rootView.findViewById<TextView>(R.id.profileEmailText)
        val profilePostNum = rootView.findViewById<TextView>(R.id.profilePostostNumText)
        val profileFriendNum = rootView.findViewById<TextView>(R.id.profileFriendNumText)
        val profileButton = rootView.findViewById<Button>(R.id.profileEditButton)
        val profileImage = rootView.findViewById<ImageView>(R.id.profileUserImage)

        profileUser = main.friendEmail!!
        println(profileUser)
        main.ImageTrueFalse = true
        main.FoodImageTrueFalse = true
        profileContentCollectionRef = db.collection("user")
        contentCollectionRef = db.collection("user").document(profileUser).collection("content")
        friendNumcontentCollectionRef =
            db.collection("user").document(profileUser).collection("friend")
        profileViewModel= ViewModelProvider(requireActivity())[MyInfoViewModel::class.java]
        profileUserRecyclerView.setHasFixedSize(true)
        profileUserRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        adapter = MyInfoAdapter(profileViewModel, profileUser)
        profileUserRecyclerView.adapter = adapter

        adapter!!.setOnItemclickListner(object: MyInfoAdapter.OnItemClickListner{
            override fun onItemClick(position: Int) {
                // val foodPosition = profileViewModel.myFoods[position].id
                main.rememberContent(position)
                main.removeBottomNavigation()
                findNavController().navigate(R.id.action_friendInfoFragment_to_friendFoodShowFragment)
            }
        })

        profileViewModel.myFoodData.observe(viewLifecycleOwner) {
            adapter!!.notifyDataSetChanged()
        }
        contentCollectionRef.get().addOnSuccessListener {
            profileViewModel.deleteAll()
            for (doc in it) {
                profileViewModel.addUserInfo(foodContent(doc))
            }
            println(profileViewModel.myFoods.size.toString())
            profilePostNum.text = profileViewModel.myFoods.size.toString()

        }

        //본인 프로필 설정
        profileContentCollectionRef.document(profileUser).get()
            .addOnSuccessListener {
                profileUserNameText.text = it["username"].toString()
                profileUserEmailText.text = it["useremail"].toString()
                val profileImageRef = profileStorageRef.child("/${it["userimage"]}")
                loadImage(profileImageRef, profileImage)
            }.addOnFailureListener {}

        //친구수 count
        friendNumcontentCollectionRef.get()
            .addOnSuccessListener {
                profileFriendNum.text = it.size().toString()
            }.addOnFailureListener {}


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