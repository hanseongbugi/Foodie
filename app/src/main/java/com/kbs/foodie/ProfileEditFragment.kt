package com.kbs.foodie

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
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
    private val profileEditViewModel by viewModels<FriendAddViewModel>()
    val storage = Firebase.storage
    val editProfileStorageRef = storage.reference
    private val eidtProfileViewModel by viewModels<MyInfoViewModel>()
    lateinit var profileUser: String
    lateinit var profileUserImage: String
    lateinit var rootView:ViewGroup
    var userPhoto: Uri? = null
    val content=registerForActivityResult(ActivityResultContracts.GetContent()){
        userPhoto=it
        rootView.findViewById<ImageView>(R.id.editProfileUserImage).setImageURI(userPhoto)
    }
    var userFileName : String? = null
    var defaultImage:String?=null
    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
        const val UPLOAD_FOLDER = "userImage/"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main = activity as MainActivity
        main.hiddenMenu()
        rootView = inflater.inflate(R.layout.profile_edit_fragment, container, false) as ViewGroup
        profileUser= main.user

        profileContentCollectionRef = db.collection("user")

        val editProfileNameText = rootView.findViewById<TextView>(R.id.editProfileNameEditText)
        val editProfileUpdateButton = rootView.findViewById<Button>(R.id.editFoodUpdateButton)
        val editProfileDeleteButton = rootView.findViewById<Button>(R.id.editFoodDeleteButton)
        val editProfileUpdateImage = rootView.findViewById<ImageView>(R.id.editProfileUserImage)
        if(main.ImageTrueFalse) {
            profileContentCollectionRef.document(profileUser)
                .get().addOnSuccessListener{
                        val userImageRef = editProfileStorageRef.child("/${it["userimage"].toString()}")
                        defaultImage="/${it["userimage"].toString()}"
                        println(defaultImage)
                        loadImage(userImageRef, editProfileUpdateImage)

                }

        }
        main.ImageTrueFalse = false
        db.runTransaction {
            val docRef = db.collection("user").document(profileUser)
            val snapshot = it.get(docRef)
            val userName = snapshot.getString("username") ?: ""
            editProfileNameText.text = userName
        }
        editProfileUpdateImage.setOnClickListener{
            //이미지 UPDATE -> activity로 넘어가서 값 받아오기??

            content.launch("image/*")
        }
            //이미지 띄우기

            /*val intent= Intent(Intent.ACTION_PICK)
            intent.type= MediaStore.Images.Media.CONTENT_TYPE
            //main.startActivityForResult(intent, PICK_PROFILE_FROM_ALBUM)


            }*/

        editProfileUpdateButton.setOnClickListener {
            //DB UPDATE

            uploadFile()
            val mImage = "${UPLOAD_FOLDER}${userFileName.toString()}"
            val mName = editProfileNameText.text.toString()
            val map = hashMapOf<String, Any>()
            map["username"] = mName
            map["userimage"] = mImage

            profileContentCollectionRef.document(profileUser).update(map)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(requireContext(),"UPDATE USER COMPLETE", Toast.LENGTH_SHORT).show()
                    }
                }.addOnSuccessListener {
                    main.onSupportNavigateUp()
                }

        }
        editProfileDeleteButton.setOnClickListener{
            val builder= AlertDialog.Builder(requireActivity())
            builder.setTitle("${profileUser}를 삭제하시겠습니까?")
                .setMessage("삭제하면 끝!")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        //사진삭제
                        profileContentCollectionRef.document(profileUser).get().addOnSuccessListener{
                            profileUserImage = it["userimage"].toString()
                            editProfileStorageRef.child("${profileUserImage}").delete()
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(),"DELETE USER COMPLETE", Toast.LENGTH_SHORT).show()
                                }
                        }
                        //음식사진 삭제
                        //유저삭제
                        deleteId()
                        //DB삭제-하위 컬렉션
                        deleteUnderCollection("friend")
                        deleteUnderCollection("content")
                        //DB삭제 -상위 컬렉션
                        profileContentCollectionRef.document(profileUser).delete()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {

                                    activity?.let {
                                        val intent =
                                            Intent(it, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }.addOnFailureListener {}

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(requireContext(),"CANCEL", Toast.LENGTH_SHORT).show()

                    })
            // 다이얼로그를 띄워주기
            builder.show()

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

    fun deleteId(){
        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(requireContext(), "아이디 삭제", Toast.LENGTH_LONG).show()


            }else{
            }
        }
    }
    fun deleteUnderCollection(underCollection: String){

        profileContentCollectionRef.document(profileUser)
            .collection(underCollection).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (doc in task.result) {
                        //사진 삭제
                        editProfileStorageRef.child("${doc.get("image").toString()}").delete()
                            .addOnSuccessListener {
                                println("음식사진 삭제 완료 ${doc.get("image")}")}
                        profileContentCollectionRef.document(profileUser)
                            .collection(underCollection).document(doc.id).delete()
                            .addOnCompleteListener {
                            }.addOnFailureListener {}
                        println(doc.id)
                    }
                }
            }
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
            }
        }
        else{
            val imageUrl=defaultImage?.split("/")
            userFileName= imageUrl?.get(imageUrl.size-1)
        }

    }

}

