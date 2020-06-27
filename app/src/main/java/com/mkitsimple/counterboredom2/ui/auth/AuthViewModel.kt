package com.mkitsimple.counterboredom2.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.mkitsimple.counterboredom2.data.repositories.AuthRepository
import com.mkitsimple.counterboredom2.utils.Coroutines
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

    var authenticatedUserLiveData : LiveData<FirebaseUser>? = null

    //private val _nAny = MutableLiveData<Task<AuthResult>>()
    //val nAny: LiveData<Task<AuthResult>> get() = _nAny
    var nAny: LiveData<Task<AuthResult>>? = null
    //var nAny: LiveData<String>? = null

    fun repositoryPerformLogin() {
        Coroutines.main {
            nAny = repository.performTypeAny()
            //_nAny.value = repository.performTypeAny()
           // nAny = "Bakit"
            //setAny()
        }
    }

    fun setAny(){
        //_nAny.value = "Hello cho"
    }

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