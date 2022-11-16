package com.kbs.foodie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendAddFragment:Fragment(R.layout.friend_add_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter: FriendAddAdapter? = null
    private val friendAddViewModel by viewModels<FriendAddViewModel>()
    private lateinit var contentCollectionRef: CollectionReference
    private var user:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main= activity as MainActivity
        main.hiddenMenu()
        val rootView = inflater.inflate(R.layout.friend_add_fragment,container,false) as ViewGroup
        val userRecyclerView=rootView.findViewById<RecyclerView>(R.id.search_recyclerview)
        val searchFriendButton = rootView.findViewById<Button>(R.id.search_friend_button)
        val searchFriendText = rootView.findViewById<EditText>(R.id.search_friend_edittext)
        contentCollectionRef=db.collection("user")

        //userRecyclerView.setHasFixedSize(true)
        userRecyclerView.layoutManager=LinearLayoutManager(activity)
        adapter= FriendAddAdapter(friendAddViewModel)

        userRecyclerView.adapter=adapter
        friendAddViewModel.userInfoData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            adapter!!.notifyDataSetChanged()
        }

        contentCollectionRef.addSnapshotListener{snapshot,error->
            friendAddViewModel.deleteAll()
            contentCollectionRef.get().addOnSuccessListener {
                for(doc in it){
                    if(doc.id.equals(user)) {
                        friendAddViewModel.addUserInfo(UserInfo(doc))
                    }
                }
            }
        }
        searchFriendButton.setOnClickListener{

        }
        return rootView
    }

}