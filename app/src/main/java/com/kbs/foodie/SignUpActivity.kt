package com.kbs.foodie

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kbs.foodie.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
            Toast.makeText(this, "Back Button-No Auth", Toast.LENGTH_SHORT).show();
        }
        binding.saveButton.setOnClickListener {
            val userEmail = binding.emailSignUpText.text.toString()
            val password = binding.passwdSignUpText.text.toString()
            loginUserId(userEmail, password)
        }
    }
    private fun loginUserId(email:String, password: String){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    Log.d(ContentValues.TAG, "사용자 이메일 등록 완료");
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }else{
                    Log.d(ContentValues.TAG, "사용자 이메일 등록 실패");
                }
            }
    }
}
