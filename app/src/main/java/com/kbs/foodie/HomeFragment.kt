package com.kbs.foodie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
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
import net.daum.mf.map.api.MapPoint

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
                    Toast.makeText(activity, "내 게시물 입니다. 아래 버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
                }
            }
        })


        homeViewModel.contentData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            adapter.notifyDataSetChanged() // 어댑터가 리사이클러뷰에게 알려 내용을 갱신함
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