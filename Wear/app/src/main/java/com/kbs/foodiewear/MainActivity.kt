package com.kbs.foodiewear

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import com.kbs.foodiewear.databinding.ActivityMainBinding

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : Activity(), LifecycleOwner {
    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var binding: ActivityMainBinding
    lateinit var user:String
    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var adapter: HomeAdapter
    private lateinit var  homeViewModel :HomeViewModel
    private lateinit var contentCollectionRef: CollectionReference
    private lateinit var friendCollectionRef: CollectionReference
    private var userName:String?=null
    private lateinit var myName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        user = intent.getStringExtra("user") ?: ""
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        val recyclerView = binding.homeRecyclerview
        homeViewModel = HomeViewModel()
        db.runTransaction {
            val docRef = db.collection("user").document(user)
            val snapshot = it.get(docRef)
            myName = snapshot.getString("username") ?: ""
        }
        contentCollectionRef = db.collection("user").document(user)
            .collection("content")
        friendCollectionRef = db.collection("user").document(user)
            .collection("friend")
        db.runTransaction {
            val docRef = db.collection("user").document(user)
            val snapshot = it.get(docRef)
            userName = snapshot.getString("username") ?: ""
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HomeAdapter(homeViewModel)
        recyclerView.adapter = adapter


        homeViewModel.contentData.observe(this) {
            // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            adapter.notifyDataSetChanged() // 어댑터가 리사이클러뷰에게 알려 내용을 갱신함
        }
        homeViewModel.emailData.observe(this) {
            homeViewModel.deleteContentAll()
            for (email in it) {
                db.collection("user").document(email).collection("content").get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            homeViewModel.addContent(Content(email, doc))
                        }
                    }
            }
            contentCollectionRef.get().addOnSuccessListener {
                for (doc in it) {
                    homeViewModel.addContent(Content(user, doc))
                }
            }
        }

        contentCollectionRef.addSnapshotListener { snapshot, error ->
            for (doc in snapshot!!.documentChanges) {
                homeViewModel.addContent(Content(user, doc))
            }

        }
        friendCollectionRef.addSnapshotListener { snapshot, error ->
            homeViewModel.deleteEmailAll()
            friendCollectionRef.get().addOnSuccessListener {
                for (doc in it) {
                    homeViewModel.addFriendEmail(doc["friendEmail"].toString(),
                        doc["friendName"].toString())
                }
            }
        }
    }
        override fun onStart() {
            super.onStart()
            lifecycleRegistry.markState(Lifecycle.State.STARTED)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

}