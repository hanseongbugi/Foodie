package com.kbs.foodie

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("UNREACHABLE_CODE")
class FriendAddFragment:Fragment(R.layout.friend_add_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter: FriendAddAdapter? = null
    private val friendAddViewModel by viewModels<FriendAddViewModel>()
    private lateinit var friendContentCollectionRef: CollectionReference
    val currentUser = Firebase.auth.currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main = activity as MainActivity
        main.hiddenMenu()
        val rootView = inflater.inflate(R.layout.friend_add_fragment, container, false) as ViewGroup
        val userRecyclerView = rootView.findViewById<RecyclerView>(R.id.search_recyclerview)
        val searchFriendText = rootView.findViewById<EditText>(R.id.search_friend_edittext)
        friendContentCollectionRef = db.collection("user")

        userRecyclerView.setHasFixedSize(true)
        userRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = FriendAddAdapter(friendAddViewModel)

        userRecyclerView.adapter = adapter
        friendAddViewModel.userInfoData.observe(viewLifecycleOwner) {
            adapter!!.notifyDataSetChanged()
        }

        friendContentCollectionRef.addSnapshotListener { snapshot, error ->
            friendAddViewModel.deleteAll()
            friendContentCollectionRef.get().addOnSuccessListener {
                for (doc in it) {
                    if (!doc.id.equals(currentUser)) {
                        friendAddViewModel.addUserInfo(UserInfo(doc))
                    }
                }
            }
        }

        searchFriendText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(searchText: Editable?) {
                println("${searchText}")

                friendContentCollectionRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        friendAddViewModel.deleteAll()
                        for (doc in task.result!!) {
                            val friendEmail = doc.id
                            val friendName = doc.get("username").toString()
                            if (friendEmail.contains(searchText?:"") || friendName.contains(searchText?:"")) {
                                friendAddViewModel.addUserInfo(UserInfo(doc))
                            }
                        }
                    }
                }
            }

        })

            return rootView
        }
    }

