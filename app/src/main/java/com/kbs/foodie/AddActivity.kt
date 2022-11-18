package com.kbs.foodie

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kbs.foodie.databinding.ActivityAddBinding
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.AddActivity.Companion.UPLOAD_FOLDER

import java.util.*


class AddActivity: AppCompatActivity(), OnLocationSetListener {
    lateinit var binding:ActivityAddBinding
    //리뷰작성 항목은 이름, 장소, 점수, 리뷰내용
    //private var adapter: MyAdapter? = null

    lateinit var mLocation:String
    lateinit var mName:String
    lateinit var mScore : String
    lateinit var mReview:String
    lateinit var markedY:String
    lateinit var markedX:String
    lateinit var mFoodImage:String

    val db: FirebaseFirestore = Firebase.firestore
    private lateinit var contentCollectionRef:CollectionReference
    lateinit var user:String
    var foodPhoto : Uri? = null
    var foodFileName : String? = null
    val storage = Firebase.storage
    companion object {
        const val REQ_GALLERY = 1
        const val UPLOAD_FOLDER = "contentImage/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addLocation.visibility= View.INVISIBLE
        initToolBar()
        user=intent.getStringExtra("user")?:""
        if(user!="") {
            Log.w("login id in AddActivity :", user)
            contentCollectionRef = db.collection("user").document(user).collection("content");
        }
//        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
//        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        binding.locationSearchButton.setOnClickListener{
            binding.addLocation.visibility=View.VISIBLE

            binding.locationSearchButton.visibility=View.INVISIBLE
            binding.nameEditText.visibility=View.INVISIBLE
            //binding.editTextTextPersonName2.visibility=View.INVISIBLE
            binding.locationEditText.visibility=View.INVISIBLE
            binding.reviewEditText.visibility=View.INVISIBLE
            binding.imageView6.visibility=View.INVISIBLE
            binding.scoreEditText.visibility=View.INVISIBLE
            binding.saveAndBackButton.visibility=View.INVISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.addLocation,MapFragment())
                .addToBackStack(null)
                .commit()
        }




        //갤러리 이미지 연동
        binding.imageView6.setOnClickListener{
            openGallery()
        }
        //addItem()
        binding.saveAndBackButton.setOnClickListener{
            addItem()
            finish()
        }
    }
    private fun addItem(){
        uploadFile()
        mName = if(binding.nameEditText.text.toString()==null){
            ""
        }else {
            binding.nameEditText.text.toString()
        }
        mReview = binding.reviewEditText.text.toString()
        mScore = binding.scoreEditText.text.toString()
        mLocation = binding.locationEditText.text.toString()
        mReview = binding.reviewEditText.text.toString()
        mFoodImage = foodFileName.toString()

        val itemMap = hashMapOf(
            "name" to mName,
            "address" to mLocation,
            "score" to mScore,
            "review" to mReview,
            "image" to "contentImage/$mFoodImage",
            "y" to markedY,
            "x" to markedX,

        )
        contentCollectionRef.add(itemMap)
            .addOnSuccessListener { //updateList()
                Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
             }.addOnFailureListener {  }
    }
    /*
    private fun updateList() {
        contentCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<ClipData.Item>()
            for (doc in it) {
                items.add(ClipData.Item(doc))
            }
            //adapter?.updateList(items)
        }
    }

    private fun addItem() {
        val name = binding.editItemName.text.toString()
        if (name.isEmpty()) {
            Snackbar.make(binding.root, "Input name!", Snackbar.LENGTH_SHORT).show()
            return
        }
        val price = binding.editPrice.text.toString().toInt()
        val autoID = binding.checkAutoID.isChecked
        val itemID = binding.editID.text.toString()
        if (!autoID and itemID.isEmpty()) {
            Snackbar.make(binding.root, "Input ID or check Auto-generate ID!", Snackbar.LENGTH_SHORT).show()
            return
        }
        val itemMap = hashMapOf(
            "name" to name,
            "price" to price
        )
        if (autoID) {
            itemsCollectionRef.add(itemMap)
                .addOnSuccessListener { updateList() }.addOnFailureListener {  }
        } else {
            itemsCollectionRef.document(itemID).set(itemMap)
                .addOnSuccessListener { updateList() }.addOnFailureListener {  }
        }
    }*/
    private fun initToolBar(){
        supportActionBar?.hide()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {         // Respond to the action bar's Up/Home button
                onBackPressed()
                binding.addLocation.visibility=View.INVISIBLE

                binding.locationSearchButton.visibility=View.VISIBLE
                binding.nameEditText.visibility=View.VISIBLE
                //binding.editTextTextPersonName2.visibility=View.INVISIBLE
                binding.locationEditText.visibility=View.VISIBLE
                binding.reviewEditText.visibility=View.VISIBLE
                binding.imageView6.visibility=View.VISIBLE
                binding.scoreEditText.visibility=View.VISIBLE
                binding.saveAndBackButton.visibility=View.VISIBLE
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLocationSet(location: String, name: String, y: Double, x: Double) {
        mLocation=location
        mName=name
        markedY= y.toString()
        markedX= x.toString()
        binding.locationEditText.setText(mLocation)
        binding.nameEditText.setText(mName)
        Log.w("2",location)
    }
    fun openGallery(){
        val intent= Intent(Intent.ACTION_PICK)
        intent.type= MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQ_GALLERY -> {
                    foodPhoto =data?.data
                    binding.imageView6.setImageURI(foodPhoto)
                }
            }
        }
    }
    private fun uploadFile() {
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        } else {
        }
        foodFileName = "food_$timestamp.png"
        val imageRef = storage.reference.child("${AddActivity.UPLOAD_FOLDER}${foodFileName}")

        imageRef.putFile(foodPhoto!!).addOnCompleteListener {
            Toast.makeText(this, "Upload completed", Toast.LENGTH_SHORT).show()

        }

    }

}


