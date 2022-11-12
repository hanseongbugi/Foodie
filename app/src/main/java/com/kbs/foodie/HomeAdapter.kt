package com.kbs.foodie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kbs.foodie.databinding.HomeItemBinding

class HomeAdapter:RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun setContetns(){
            view.findViewById<ImageView>(R.id.image).setImageResource(R.drawable.ic_baseline_person_24)
            view.findViewById<TextView>(R.id.name).text="hello"
            view.findViewById<TextView>(R.id.address).text="seoul"
            view.findViewById<TextView>(R.id.star).text="별점 5"
            view.findViewById<TextView>(R.id.review).text="맛이 쓰고, 맛이 말고, 맛이 있다."
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.home_item,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContetns()
    }

    override fun getItemCount(): Int {
        return 0
    }
}