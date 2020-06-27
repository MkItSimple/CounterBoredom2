package com.mkitsimple.counterboredom2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom2.data.repositories.UserRepository
import javax.inject.Inject

class ProfileViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: UserRepository

    var isSuccessful: LiveData<Any>? = null
    suspend fun updateProfile(username: String, profileImageUrl: String) {
        isSuccessful = repository.updateProfile(profileImageUrl, username)
    }
}
