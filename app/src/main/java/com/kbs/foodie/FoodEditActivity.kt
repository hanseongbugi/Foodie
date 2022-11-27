package com.kbs.foodie

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.databinding.ActivityEditFoodBinding
import java.util.*

class FoodEditActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditFoodBinding
    //리뷰작성 항목은 이름, 장소, 점수, 리뷰내용
    //private var adapter: MyAdapter? = null

    lateinit var mScore : String
    lateinit var mReview:String
    lateinit var mFoodImage:String

    val db: FirebaseFirestore = Firebase.firestore

    private lateinit var foodcontentCollectionRef: CollectionReference
    lateinit var user:String
    lateinit var storeName:String
    lateinit var storeLocation:String
    var storeScore:Double?=0.0
    lateinit var storeReview:String
    lateinit var toImage:String
    lateinit var storeId:String
    lateinit var fromImage:Uri


    var foodPhoto : Uri? = null
    var foodFileName : String? = null
    val storage = Firebase.storage

    val foodStorageRef = storage.reference
    val content=registerForActivityResult(ActivityResultContracts.GetContent()){
        foodPhoto=it
        binding.editFoodImage.setImageURI(foodPhoto)
    }
    companion object {
        const val REQ_GALLERY = 1
        const val UPLOAD_FOLDER = "contentImage/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolBar()
        user = intent.getStringExtra("user").toString()
        storeId = intent.getStringExtra("storeId").toString()
        storeName = intent.getStringExtra("name").toString()
        storeLocation = intent.getStringExtra("location").toString()
        storeScore = intent.getStringExtra("score")?.toDouble()
        storeReview = intent.getStringExtra("review").toString()
        toImage = intent.getStringExtra("image").toString()
        fromImage = Uri.parse(toImage)

        foodcontentCollectionRef = db.collection("user").document(user).collection("content")

        //기존 아이템 보여주기
        showItem()
        //갤러리 이미지 연동
        binding.editFoodImage.setOnClickListener{
            content.launch("image/*")
        }
        //addItem()
        binding.saveAndBackButton.setOnClickListener{
            mScore = binding.scoreEditText.text.toString()
            mReview = binding.reviewEditText.text.toString()
            if(mScore==""||mReview==""||foodPhoto==null){
                Toast.makeText(this,"아직 입력하지 않은 칸이 있습니다",Toast.LENGTH_SHORT).show()

            }
            else {
                addItem()
                startActivity(Intent(this, MainActivity::class.java).putExtra("user", user))
                finish()
            }
        }
        binding.deleteBackButton.setOnClickListener {

           deleteItem()

        }
    }
    fun showItem(){
                val profileImageRef = foodStorageRef.child("/${toImage}")
                loadImage(profileImageRef, binding.editFoodImage)
                binding.nameEditText.text =
                    SpannableStringBuilder(storeName)
                binding.locationEditText.text =
                    SpannableStringBuilder(storeLocation)
                binding.scoreEditText.text =
                    SpannableStringBuilder(storeScore.toString())
                binding.reviewEditText.text =
                    SpannableStringBuilder(storeReview)
            }

    private fun addItem(){
        uploadFile()
        mFoodImage = "${UPLOAD_FOLDER}${foodFileName}"
        val map = hashMapOf<String, Any>()
        map["score"] = mScore
        map["review"] = mReview
        map["image"] = mFoodImage
        foodcontentCollectionRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                foodcontentCollectionRef.document(storeId).update(map)
                    .addOnCompleteListener {

                    }

            }
        }
    }
    fun deleteItem(){
        //food Content DELETE
            val builder= AlertDialog.Builder(this)
            builder.setTitle("삭제하시겠습니까?")
                .setMessage("삭제하면 되돌릴 수 없습니다.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        foodcontentCollectionRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                foodcontentCollectionRef.document(storeId).delete().addOnSuccessListener {

                                }
                                startActivity(Intent(this,MainActivity::class.java).putExtra("user",user))
                                finish()
                            }

                            //navigation 이동
                            //findNavController().navigate(R.id.action_foodEditFragment_to_foodShowFragment)
                        }.addOnFailureListener {}

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(this,"CANCEL", Toast.LENGTH_SHORT).show()

                    })
            // 다이얼로그를 띄워주기
            builder.show()


    }
    private fun initToolBar(){
        supportActionBar?.hide()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    private fun loadImage(imageRef: StorageReference, view: ImageView) {
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }.addOnFailureListener {
            view.setImageResource(R.drawable.img)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {         // Respond to the action bar's Up/Home button
                onBackPressed()

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun uploadFile() {
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        } else {

        }
        foodFileName = if(timestamp==null) "food_${R.drawable.img}"
        else "food_$timestamp.png"
        val imageRef = storage.reference.child("${UPLOAD_FOLDER}${foodFileName}")

        imageRef.putFile(foodPhoto!!).addOnCompleteListener {

        }

    }

}

