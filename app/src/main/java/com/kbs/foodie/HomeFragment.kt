package com.kbs.foodie

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var adapter: HomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var contentCollectionRef:CollectionReference
    private lateinit var friendCollectionRef:CollectionReference
    private lateinit var user:String
    private var userName:String?=null
    private lateinit var myName:String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val main= activity as MainActivity
        main.showHomeMenu()
        val rootView = inflater.inflate(R.layout.home_fragment,container,false) as ViewGroup
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.recyclerview)


        user=main.user
        db.runTransaction {
            val docRef=db.collection("user").document(user)
            val snapshot=it.get(docRef)
            myName=snapshot.getString("username")?:""
        }
        contentCollectionRef=db.collection("user").document(user)
            .collection("content")
        friendCollectionRef=db.collection("user").document(user)
            .collection("friend")
        db.runTransaction{
            val docRef=db.collection("user").document(user)
            val snapshot=it.get(docRef)
            userName=snapshot.getString("username")?:""
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(activity)
        adapter= HomeAdapter(homeViewModel)
        recyclerView.adapter=adapter
        adapter.setItemClickListener(object: HomeAdapter.OnItemClickListener {
            override fun onClick(v: View) {
                val userName=v.findViewById<TextView>(R.id.userName).text.toString()
                if(userName!=myName) {
                     main.friendEmail=homeViewModel.friendMap[userName]
                     findNavController().navigate(R.id.action_homeFragment_to_friendInfoFragment)
                }
                else{
                    Toast.makeText(activity, "??? ????????? ?????????. ??? ????????? ???????????????", Toast.LENGTH_SHORT).show();
                }
            }
        })


        homeViewModel.contentData.observe(viewLifecycleOwner) { // ???????????? ????????? ?????? ??? ??????????????? ????????? ??????
            adapter.notifyDataSetChanged() // ???????????? ???????????????????????? ?????? ????????? ?????????
        }
        homeViewModel.emailData.observe(viewLifecycleOwner){
            homeViewModel.deleteContentAll()
            for(email in it){
                db.collection("user").document(email).
                collection("content").get().addOnSuccessListener {
                    for(doc in it){
                        homeViewModel.addContent(Content(email,doc))
                    }
                }
            }
            contentCollectionRef.get().addOnSuccessListener {
                for(doc in it){
                    homeViewModel.addContent(Content(user,doc))
                }
            }
        }

        contentCollectionRef.addSnapshotListener{snapshot,error->
            for(doc in snapshot!!.documentChanges){
                homeViewModel.addContent(Content(user,doc))
            }

        }
        friendCollectionRef.addSnapshotListener{snapshot,error->
            homeViewModel.deleteEmailAll()
            friendCollectionRef.get().addOnSuccessListener {
                for(doc in it){
                    homeViewModel.addFriendEmail(doc["friendEmail"].toString(),doc["friendName"].toString())
                }
            }
        }




        return rootView
    }


}