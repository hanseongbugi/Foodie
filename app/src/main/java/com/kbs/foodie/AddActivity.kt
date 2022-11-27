package com.kbs.foodie

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.EXTRA_EVENT_ID
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kbs.foodie.databinding.ActivityAddBinding
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.AddActivity.Companion.UPLOAD_FOLDER

import java.util.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat.WearableExtender

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
    val content=registerForActivityResult(ActivityResultContracts.GetContent()){
        foodPhoto =it
        binding.editFoodImage.setImageURI(foodPhoto)
    }
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

            binding.constraintLayout2.visibility=View.INVISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.addLocation,MapFragment())
                .addToBackStack(null)
                .commit()

        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0
            createNotificationChannel()
        }

        //갤러리 이미지 연동
        binding.editFoodImage.setOnClickListener{
            content.launch("image/*")
        }
        binding.saveAndBackButton.setOnClickListener{
            mName = binding.nameEditText.text.toString()
            mReview = binding.reviewEditText.text.toString()
            mScore = binding.scoreEditText.text.toString()
            mLocation = binding.locationEditText.text.toString()
            mReview = binding.reviewEditText.text.toString()
            if(mName==""||mReview==""||mScore==""||mLocation==""||mReview==""||foodPhoto==null){
                Toast.makeText(this, "모든 내용을 입력해주세요", Toast.LENGTH_SHORT).show()

            }
            else {
                addItem()
                finish()
                showNotification()
            }
        }
    }


    private val channelID = "default"
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelID, "default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "description text of this channel."
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun showNotification() {
        val notificationId = 1
//        val intent = Intent(this, LoginActivity::class.java)
        // The channel ID of the notification.
        val id = "my_channel_01"
        // Build intent for notification content
        val viewPendingIntent = Intent(this, LoginActivity::class.java).let { viewIntent ->
            viewIntent.putExtra(EXTRA_EVENT_ID, channelID)
            PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        // Notification channel ID is ignored for Android 7.1.1
        // (API level 25) and lower.
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_baseline_map_24)
            .setContentTitle("FOODIE NOTIFICATION")
            .setContentText("${mName.toString()}")
            .setContentText("${mScore.toString()}")
            .setContentIntent(viewPendingIntent)
        NotificationManagerCompat.from(this).apply {
            notify(notificationId, notificationBuilder.build())
        }

    }

    private fun addItem(){
        uploadFile()
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
                binding.constraintLayout2.visibility=View.VISIBLE

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
       // Log.w("2",location)
    }

    private fun uploadFile() {
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        } else {

        }
        foodFileName = if(timestamp==null) "food_${R.drawable.img}"
        else "food_$timestamp.png"
        val imageRef = storage.reference.child("${AddActivity.UPLOAD_FOLDER}${foodFileName}")

        imageRef.putFile(foodPhoto!!).addOnCompleteListener {
            Toast.makeText(this, "Upload completed", Toast.LENGTH_SHORT).show()

        }

    }

}


