package com.kbs.foodie

import android.content.ClipData
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*

class ProfileEditFragment: Fragment(R.layout.profile_edit_fragment) {
    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var profileContentCollectionRef: CollectionReference
    private lateinit var contentCollectionRef: CollectionReference
    val storage = Firebase.storage
    val editProfileStorageRef = storage.reference
    private val eidtProfileViewModel by viewModels<MyInfoViewModel>()
    lateinit var profileUser: String

    var userPhoto : Uri? = null
    var UserFileName : String? = null
    lateinit var meditName:String
    lateinit var meditImage : String
    companion object {
        const val REQ_GALLERY = 1
        const val UPLOAD_FOLDER = "userImage/"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main = activity as MainActivity
        main.hiddenMenu()
        val rootView = inflater.inflate(R.layout.profile_edit_fragment, container, false) as ViewGroup
        profileUser= main.user

        profileContentCollectionRef = db.collection("user")

        val editProfileNameText = rootView.findViewById<TextView>(R.id.editProfileNameEditText)
        val editProfileInfomationText = rootView.findViewById<TextView>(R.id.editProfileEmailEditText)
        val editProfileUpdateButton = rootView.findViewById<Button>(R.id.editProfileUpdateButton)
        val editProfileUpdateImage = rootView.findViewById<ImageView>(R.id.editProfileUserImage)

        profileContentCollectionRef.document(profileUser).get()
            .addOnSuccessListener {
                editProfileNameText.text = it["username"].toString()
                editProfileInfomationText.text = "이 페이지는 공사 중"
                val profileImageRef = editProfileStorageRef.child("/${it["userimage"].toString()}")
                loadImage(profileImageRef, editProfileUpdateImage)
            }.addOnFailureListener {}

        editProfileUpdateImage.setOnClickListener{
            //이미지 UPDATE
        }
        editProfileUpdateButton.setOnClickListener {
            //DB UPDATE

        }

        return rootView
    }
    private fun loadImage(imageRef: StorageReference, view: ImageView) {
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }.addOnFailureListener {
            view.setImageResource(R.drawable.img)
        }
    }

}

