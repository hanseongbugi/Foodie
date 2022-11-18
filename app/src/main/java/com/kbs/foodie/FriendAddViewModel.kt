package com.kbs.foodie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot

data class UserInfo(val id: String, val username: String, val email: String, val userImage: String) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["username"].toString(), doc["useremail"].toString(), doc["userimage"].toString())

}
class FriendAddViewModel :ViewModel(){

    val userInfoData = MutableLiveData<ArrayList<UserInfo>>()
    val userInfos = ArrayList<UserInfo>()
    fun addUserInfo(userInfo:UserInfo) {
        userInfos.add(userInfo)
    }
    fun updateUserInfo(){
        userInfoData.value = userInfos // let the observer know the livedata changed
    }
    fun updateContent(pos: Int, userInfo:UserInfo) {
        userInfos[pos] = userInfo
        userInfoData.value = userInfos // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun deleteContent(pos: Int) {
        userInfos.removeAt(pos)
        userInfoData.value = userInfos
    }
    fun deleteAll(){
        for (d in userInfos)
            println(d)
        userInfos.clear()
        userInfoData.value = userInfos // let the observer know the livedata changed
    }
}