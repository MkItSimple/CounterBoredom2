package com.mkitsimple.counterboredom2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.data.repositories.UserRepository
import com.mkitsimple.counterboredom2.utils.NODE_USERS
import javax.inject.Inject

class FriendsListViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: UserRepository

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

//    private val _users = MutableLiveData<List<User>>()
//    val users: LiveData<List<User>>
//        get() = _users

//    fun fetchUsers() {
//
//        val ref = FirebaseDatabase.getInstance().getReference("/users")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                //Log.d(TAG, "DataSnapshot: " + snapshot.getValue())
////                 val adapter = GroupAdapter<ViewHolder>()
////                 val uid = FirebaseAuth.getInstance().uid
//                val musers = mutableListOf<User>()
//
//                snapshot.children.forEach {
//                    //Log.d("NewMessage \n ", it.toString())
//                    val user = it.getValue(User::class.java)
//                    if (user != null) {
//                        musers.add(user)
//                    }
//                }
//                _users.value = musers
//
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

    var users: LiveData<List<User>>? = null
    suspend fun fetchUsers() {
        users = repository.fetchUsers()
    }

//    var isSuccessful: LiveData<Any>? = null
//    suspend fun updateProfile(username: String, profileImageUrl: String) {
//        isSuccessful = repository.updateProfile(profileImageUrl, username)
//    }
}
