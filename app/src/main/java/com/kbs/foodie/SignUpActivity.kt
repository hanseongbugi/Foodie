package com.kbs.foodie

import android.content.ContentValues
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.databinding.ActivitySignupBinding
import java.util.*

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    lateinit var muserName:String
    lateinit var muserEmail:String
    lateinit var muserPassword:String
    lateinit var muserImage:String
    private val db: FirebaseFirestore = Firebase.firestore
    private val userItemRef = db.collection("user")
    var userPhoto : Uri? = null
    var fileName : String? = null
    val storage = Firebase.storage
    companion object {
        const val REQ_GALLERY = 2
        const val UPLOAD_FOLDER = "userImage/"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
            Toast.makeText(this, "Back Button-> No Auth", Toast.LENGTH_SHORT).show();
        }
        binding.userImage1.setOnClickListener {
            openGallery()
        }
        binding.saveButton.setOnClickListener {
            
            muserEmail = binding.emailSignUpText.text.toString()
            muserPassword = binding.passwdSignUpText.text.toString()
            muserName = binding.userNameEditText.text.toString()
            muserImage = fileName.toString()
            loginUserId(muserEmail, muserPassword, muserName, muserImage)


        }
    }
    private fun loginUserId(email:String, password: String, name: String, imagename: String){
        
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    Log.d(ContentValues.TAG, "사용자 이메일 등록 완료");
                    addItem()
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }else{
                    Log.d(ContentValues.TAG, "사용자 이메일 등록 실패");
                }
            }
    }
    private fun addItem(){
        uploadFile()
        
        val itemMap = hashMapOf(
            "username" to muserName,
            "imagename" to muserImage,
            "useremail" to muserEmail,
            "password" to muserPassword
        )
        userItemRef.document(muserEmail).set(itemMap)
            .addOnSuccessListener { //updateList()
                Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {  }
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
                    userPhoto =data?.data
                    binding.userImage1.setImageURI(userPhoto)
                }
            }
        }
    }
    private fun uploadFile() {
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        } else {
        }
        fileName = "IMAGE_$timestamp.png"
        val imageRef = storage.reference.child("${SignUpActivity.UPLOAD_FOLDER}${fileName}")

        imageRef.putFile(userPhoto!!).addOnCompleteListener {
            Toast.makeText(this, "Upload completed", Toast.LENGTH_SHORT).show()

        }

    }
}
