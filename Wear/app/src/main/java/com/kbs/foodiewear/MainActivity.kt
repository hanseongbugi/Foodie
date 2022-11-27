package com.kbs.foodiewear

import android.app.Activity
import android.os.Bundle
import com.kbs.foodiewear.databinding.ActivityMainBinding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var user:String

    val fragmentManager: FragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user=intent.getStringExtra("user")?:""
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction().add(R.id.fragmentView,homeFragment).commit()
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)


        /*override fun onBackPressed() {
        startActivity(
            Intent(this, WearMenu::class.java)
        )
        finish()
    }*/
    }
}