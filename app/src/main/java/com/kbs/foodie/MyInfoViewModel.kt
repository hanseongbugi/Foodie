package com.kbs.foodie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot

data class foodContent(val id: String, val name: String, val score: Float?, val address: String, val review:String ,val image: String) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["name"].toString(), doc["score"].toString().toFloatOrNull(),
                doc["address"].toString(), doc["review"].toString(), doc["image"].toString())
}
class MyInfoViewModel :ViewModel(){
    val myFoodData = MutableLiveData<ArrayList<foodContent>>()
    val myFoods = ArrayList<foodContent>()
    fun addUserInfo(userInfo:foodContent) {
        myFoods.add(userInfo)
        myFoodData.value = myFoods
    }
    fun getContent(pos: Int):foodContent?{
        return myFoodData.value?.get(pos)
    }
    fun updateContent(pos: Int, userInfo:foodContent) {
        myFoods[pos] = userInfo
        myFoodData.value = myFoods // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun deleteContent(pos: Int) {
        myFoods.removeAt(pos)
        myFoodData.value = myFoods
    }

    fun deleteAll(){
        myFoods.clear()
        myFoodData.value = myFoods // let the observer know the livedata changed
    }
}