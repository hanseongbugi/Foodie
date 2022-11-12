package com.kbs.foodie


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kbs.foodie.databinding.HomeFragmentBinding
import com.kbs.foodie.databinding.HomeItemBinding



class HomeAdapter(private val homeViewModel:HomeViewModel) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding:HomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos:Int) {
            with(homeViewModel.contents[pos]){
                binding.listTvName.text=name
                binding.listTvAddress.text=address
                binding.listTvNumber.text=score.toString()
            }
        }
    }
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
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