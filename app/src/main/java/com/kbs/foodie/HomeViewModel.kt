package com.kbs.foodie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Content(val id: String, val name: String, val score: Float?, val address: String, val review:String ,val image: String) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["name"].toString(), doc["score"].toString().toFloatOrNull(),
                doc["address"].toString(), doc["review"].toString(), doc["image"].toString())
    constructor(doc: DocumentChange):
            this(doc.document.id,doc.document.data["name"].toString(),
                doc.document.data["score"].toString().toFloatOrNull(),
            doc.document.data["address"].toString(),doc.document.data["review"].toString(),
                doc.document.data["image"].toString())
}
class HomeViewModel : ViewModel() {
    val contentData = MutableLiveData<ArrayList<Content>>()
    val contents = ArrayList<Content>()
    val emailData=MutableLiveData<ArrayList<String>>()
    val friendEmail=ArrayList<String>()

    fun addFriendEmail(email:String){
        if(friendEmail.contains(email))return
        friendEmail.add(email)
        emailData.value=friendEmail

    }

    fun addContent(content:Content) {
        if(contents.contains(content))return
        contents.add(content)
        contents.sortBy{it.name}
        contentData.value = contents
    }

    fun updateContent(pos: Int, content:Content) {
        contents[pos] = content
        contentData.value = contents // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun deleteContent(pos: Int) {
        contents.removeAt(pos)
        contentData.value = contents
    }
    fun deleteContentAll(){
        contents.clear()
    }
    fun deleteEmailAll(){
        friendEmail.clear()
    }
}
