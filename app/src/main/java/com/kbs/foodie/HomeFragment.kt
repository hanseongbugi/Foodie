package com.kbs.foodie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter: HomeAdapter? = null
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var contentCollectionRef:CollectionReference
    private var user:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

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
        user=arguments?.getString("user")
        contentCollectionRef=db.collection("user").document(user!!)
            .collection("content")

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(activity)
        adapter= HomeAdapter(homeViewModel)
        recyclerView.adapter=adapter
        homeViewModel.contentData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            adapter!!.notifyDataSetChanged() // 어댑터가 리사이클러뷰에게 알려 내용을 갱신함
        }
        contentCollectionRef.addSnapshotListener{snapshot,error->
            homeViewModel.deleteAll()
            contentCollectionRef.get().addOnSuccessListener {
                for(doc in it){
                    homeViewModel.addContent(Content(doc))
                }
            }
        }
        return rootView
    }


}