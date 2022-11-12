package com.kbs.foodie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment

class FriendFragment : Fragment(R.layout.friend_fragment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.friend_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_menu -> {
              //  val intent= Intent(this,AddActivity::class.java)
               // startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}