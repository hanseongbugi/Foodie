package com.kbs.foodie

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kbs.foodie.databinding.ActivityAddBinding


class AddActivity: AppCompatActivity(), OnLocationSetListener {
    lateinit var binding:ActivityAddBinding
    lateinit var mLocation:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addLocation.visibility= View.INVISIBLE
        initToolBar()
//        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
//        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        binding.button.setOnClickListener{
            binding.addLocation.visibility=View.VISIBLE

            binding.button.visibility=View.INVISIBLE
            binding.editTextTextPersonName.visibility=View.INVISIBLE
            //binding.editTextTextPersonName2.visibility=View.INVISIBLE
            binding.editTextTextPersonName3.visibility=View.INVISIBLE
            binding.editTextTextPersonName4.visibility=View.INVISIBLE
            binding.imageView6.visibility=View.INVISIBLE
            //binding.editTextTextPersonName.visibility=View.INVISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentView,MapFragment())
                .addToBackStack(null)
                .commit()

        }

    }

    private fun initToolBar(){
        supportActionBar?.hide()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {         // Respond to the action bar's Up/Home button
                onBackPressed()
                binding.button.visibility=View.VISIBLE
                binding.editTextTextPersonName.visibility=View.VISIBLE
                //binding.editTextTextPersonName2.visibility=View.INVISIBLE
                binding.editTextTextPersonName3.visibility=View.VISIBLE
                binding.editTextTextPersonName4.visibility=View.VISIBLE
                binding.imageView6.visibility=View.VISIBLE
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLocationSet(location: String) {
        mLocation=location
        Log.w("2",location)
    }


}


