package com.mkitsimple.counterboredom2.data.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom2.data.models.User
import java.util.*


class AuthRepository {

    val mAuth = FirebaseAuth.getInstance()

    //private val _signInReturnValue = MutableLiveData<Any>()

//    suspend fun performTypeAny() : MutableLiveData<Any> {
//        val _signInReturnValue = MutableLiveData<Any>()
//        //delay(10000)
//        _signInReturnValue.value = "Failed"
//        return _signInReturnValue
//    }

    suspend fun performLogin(email: String, password: String) : MutableLiveData<Any> {
        val loginReturnValue = MutableLiveData<Any>()

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    loginReturnValue.value = true
                }
                .addOnFailureListener {
                    loginReturnValue.value = it.message
                }

        return loginReturnValue
    }

    suspend fun performRegister(email: String, password: String): MutableLiveData<Any> {
        val registerReturnValue = MutableLiveData<Any>()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                registerReturnValue.value = it.isSuccessful
            }
            .addOnFailureListener{
                registerReturnValue.value = it.message
            }
        return registerReturnValue
    }

    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri): MutableLiveData<Any> {
        val uploadReturnValue = MutableLiveData<Any>()
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        uploadReturnValue.value = it
                    }
                }
                .addOnFailureListener {
                    uploadReturnValue.value = it.message
                }
        return  uploadReturnValue
    }

    suspend fun saveUserToFirebaseDatabase(username: String, profileImage: String, token: String): MutableLiveData<Any> {
        val uploadReturnValue = MutableLiveData<Any>()

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username, profileImage, token)

        ref.setValue(user)
                .addOnSuccessListener {
                    uploadReturnValue.value = true
                }
                .addOnFailureListener {
                    uploadReturnValue.value = it.message
                }
        return uploadReturnValue
    }

    suspend fun performPair(): MutableLiveData<Pair<Boolean, String>> {
        val pairValues = MutableLiveData<Pair<Boolean, String>>()
        pairValues.value = Pair(true, "choreyn")
        return pairValues
    }
}