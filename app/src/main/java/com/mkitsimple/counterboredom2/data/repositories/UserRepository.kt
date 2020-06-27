package com.mkitsimple.counterboredom2.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mkitsimple.counterboredom2.data.models.ChatMessage

class UserRepository {

    val uid = FirebaseAuth.getInstance().uid // fromid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$uid")

    val latestMessagesMap = HashMap<String, ChatMessage>()

    fun fetchCurrentUser() {
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) {
//                //_currentUser.value = p0.getValue(User::class.java)
//                //Log.d("LatestMessages", "Current user ${cUser.profileImageUrl}")
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//                returnError(p0.message)
//            }
//        })
    }

    fun returnError(message: String) : String {
        return message
    }

    fun uid() : String? {
        return uid
    }
}