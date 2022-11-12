package com.kbs.foodie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange

data class Content(val id: String, val name: String, val score: Int, val address: String, val image: String) {
    constructor(doc: DocumentChange) :
            this(doc.document.id, doc.document.data["name"].toString(), doc.document.data["score"].toString().toIntOrNull() ?: 0,
                doc.document.data["address"].toString(), doc.document.data["image"].toString())
}
class HomeViewModel : ViewModel() {
    val contentData = MutableLiveData<ArrayList<Content>>()
    val contents = ArrayList<Content>()
    fun addContent(content:Content) {
        contents.add(content)
        contentData.value = contents // let the observer know the livedata changed
    }
    fun updateContent(pos: Int, content:Content) {
        contents[pos] = content
        contentData.value = contents // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun deleteContent(pos: Int) {
        contents.removeAt(pos)
        contentData.value = contents
    }
}
