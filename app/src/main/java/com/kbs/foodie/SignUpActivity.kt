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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.databinding.ActivitySignupBinding
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    lateinit var muserName:String
    lateinit var muserEmail:String
    lateinit var muserPassword:String
    lateinit var muserImage:String
    private val db: FirebaseFirestore = Firebase.firestore
    private val userItemRef = db.collection("user")
    var userPhoto : Uri? = null
    val content=registerForActivityResult(ActivityResultContracts.GetContent()){
        userPhoto =it
        binding.userImage1.setImageURI(userPhoto)
    }
    private var userFileName : String?=null
    val storage = Firebase.storage
    companion object {
        const val REQ_GALLERY = 1
        const val UPLOAD_FOLDER = "userImage/"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val signUpEmailText = binding.emailSignUpText.text
        val signUpNameText = binding.userNameEditText.text
        val signUpPassWord = binding.passwdSignUpText.text
        binding.backButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
            Toast.makeText(this, "Back Button-> No Auth", Toast.LENGTH_SHORT).show();
        }
        binding.userImage1.setOnClickListener {
            content.launch("image/*")
        }
        binding.saveButton.setOnClickListener {
            Log.w("aaa","1")
            muserEmail = signUpEmailText.toString()
            muserPassword = signUpPassWord.toString()
            muserName = signUpNameText.toString()
            if(checkEmail(muserEmail) && muserPassword.length >=6) {
                uploadFile()
                muserImage = "${UPLOAD_FOLDER}${userFileName}"

                loginUserId(muserEmail, muserPassword)

            }else{
                if(!checkEmail(muserEmail)){
                Toast.makeText(this, "Email error: 이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(muserPassword.length <6){
                    Toast.makeText(this, "Password error: 6글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                }
            }

    }

    fun checkEmail(email: String): Boolean = email.contains("@")
    private fun loginUserId(email:String, password: String){
        
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    Log.d(ContentValues.TAG, "사용자 이메일 등록 완료")
                    addItem()
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }else{
                    Log.d(ContentValues.TAG, "이미 등록된 계정 입니다.")
                }
            }
    }
    private fun addItem(){
        
        val itemMap = hashMapOf(
            "username" to muserName,
            "userimage" to muserImage,
            "useremail" to muserEmail
        )
        userItemRef.document(muserEmail).set(itemMap)
            .addOnSuccessListener { //updateList()
                Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {  }
    }


    private fun uploadFile() {
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        } else {
            Log.d(ContentValues.TAG, "version's low")
        }

        if(userPhoto!=null) {
            userFileName = "User_$timestamp.png"
            val imageRef = storage.reference.child("${UPLOAD_FOLDER}${userFileName}")
            imageRef.putFile(userPhoto!!).addOnCompleteListener {
                Toast.makeText(this, "Upload completed", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            userFileName="userDefaultImage.png"
            Toast.makeText(this, "Upload completed", Toast.LENGTH_SHORT).show()
        }

    }

}

