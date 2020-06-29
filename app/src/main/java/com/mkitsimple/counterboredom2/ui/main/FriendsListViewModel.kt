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

    var users: LiveData<List<User>>? = null
    suspend fun fetchUsers() {
        users = repository.fetchUsers()
    }
}
