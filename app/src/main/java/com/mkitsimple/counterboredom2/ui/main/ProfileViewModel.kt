package com.mkitsimple.counterboredom2.ui.main

import android.net.Uri
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

    var isSuccessful2: LiveData<Any>? = null
    suspend fun updateProfileWithImage(username: String, profileImageUrl: String) {
        isSuccessful2 = repository.updateProfileWithImage(profileImageUrl, username)
    }

    var isUploadSuccessful: LiveData<Pair<Boolean, String>>? = null
    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        isUploadSuccessful = repository.uploadImageToFirebaseStorage(selectedPhotoUri)
    }
}
