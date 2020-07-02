package com.mkitsimple.counterboredom2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mkitsimple.counterboredom2.data.models.Profile
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.data.repositories.UserRepository
import kotlinx.coroutines.Job
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    companion object {
        var currentUser = User()
    }

    @Inject
    lateinit var repository: UserRepository

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _mProfileUpdates = MutableLiveData<HashMap<String, User>>()
    val mProfileUpdates: LiveData<HashMap<String, User>> get() = _mProfileUpdates

    private val _isProfileUpdated = MutableLiveData<Task<Void>>()
    val isProfileUpdated: LiveData<Task<Void>> get() = _isProfileUpdated

    private val _isUpdated = MutableLiveData<Task<Void>>()
    val isUpdated: LiveData<Task<Void>> get() = _isUpdated

    var fetchCurrentUserResult: LiveData<User>? = null
    suspend fun fetchCurrentUser() {
        fetchCurrentUserResult = repository.fetchCurrentUser()
    }

    fun updateUser(profileImageUrl: String, username: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = Profile(uid, username, profileImageUrl)
        _isProfileUpdated.value = ref.setValue(user)
    }

    fun uid() : String? {
        return FirebaseAuth.getInstance().uid
    }
}