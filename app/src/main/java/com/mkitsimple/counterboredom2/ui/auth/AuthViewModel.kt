package com.mkitsimple.counterboredom2.ui.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom2.data.repositories.AuthRepository
import javax.inject.Inject


class AuthViewModel @Inject constructor() : ViewModel(){

    @Inject
    lateinit var repository: AuthRepository

    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean> get() = _isLoginSuccessful

    private val _isRegisterSuccessful = MutableLiveData<Boolean>()
    val isRegisterSuccessful: LiveData<Boolean> get() = _isRegisterSuccessful

    private val _isSaveUserSuccessful = MutableLiveData<Boolean>()
    val isSaveUserSuccessful: LiveData<Boolean> get() = _isSaveUserSuccessful

    private val _isUploadImageSuccessful = MutableLiveData<Boolean>()
    val isUploadImageSuccessful: LiveData<Boolean>
        get() = _isUploadImageSuccessful

    private val _mSelectedPhotoUri = MutableLiveData<String>()
    val mSelectedPhotoUri: LiveData<String>
        get() = _mSelectedPhotoUri

    private val _errorMessage= MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    //private lateinit var job: Job

//    fun performLogin(email: String, password: String) =
//        Coroutines.main { repositoryPerformLogin(email, password) }

//    private val _loggedinUser= MutableLiveData<FirebaseUser>()
//    val loggedinUser: LiveData<FirebaseUser>
//        get() = _loggedinUser
    var loginResult: LiveData<Any>? = null
    suspend fun performLogin(email: String, password: String) {
        loginResult = repository.performLogin(email, password)
    }

    var registerResult: LiveData<Any>? = null
    suspend fun performRegister(email: String, password: String) {
        registerResult = repository.performRegister(email, password)
    }

    var uploadResult: LiveData<Any>? = null
    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        uploadResult = repository.uploadImageToFirebaseStorage(selectedPhotoUri)
    }

    var saveUserResult: LiveData<Any>? = null
    suspend fun saveUserToFirebaseDatabase(username: String, profileImage: String, token: String) {
        saveUserResult = repository.saveUserToFirebaseDatabase(username, profileImage, token)
    }

//    fun setAny(performTypeAny: MutableLiveData<Any>) {
//        //_nAny.value = "Hello cho"
//        Coroutines.main{
//            loginResult = performTypeAny
//        }
//    }

//    fun performRegister(email: String, password: String) =
//        Coroutines.main { repositoryPerformRegister(email, password) }
//
//    suspend fun repositoryPerformRegister(email: String, password: String) {
//        val mRegisterResult = repository.performRegister(email, password)
//        if (mRegisterResult == "true") {
//            _isRegisterSuccessful.value = true
//        } else {
//            _errorMessage.value = "Login Failed"
//        }
//    }
//
//    fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
//
//    }
//
//    fun saveUserToFirebaseDatabase(toString: String, s: String, token: String) {
//
//    }

//    private val _nAny = MutableLiveData<Any>()
//    val nAny: LiveData<Any> get() = _nAny
//
//    fun performTypeAny() : Any {
//        _nAny.value = "chochho"
//        return nAny
//    }


//    override fun onCleared() {
//        super.onCleared()
//        //if(::job.isInitialized) job.cancel()
//        //compositeDisposable.clear()
//    }


}