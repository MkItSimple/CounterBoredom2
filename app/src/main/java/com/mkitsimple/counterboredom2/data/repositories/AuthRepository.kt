package com.mkitsimple.counterboredom2.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthRepository {

    val mAuth = FirebaseAuth.getInstance()

    //private val _signInReturnValue = MutableLiveData<Any>()

    suspend fun performTypeAny() : MutableLiveData<Task<AuthResult>> {
        val _signInReturnValue = MutableLiveData<Task<AuthResult>>()

        //_signInReturnValue.value = "Hala ka"
        mAuth.signInWithEmailAndPassword("c16@gmail.com", "111111")
            .addOnCompleteListener {
                // add listener here
                _signInReturnValue.value = it
            }
            .addOnFailureListener {
                //_signInReturnValue.value = it.message
            }

        return _signInReturnValue
    }

//    suspend fun performLogin(email: String, password: String) : MutableLiveData<FirebaseUser> {
//        val authenticatedUserMutableLiveData =
//            MutableLiveData<FirebaseUser>()
//
//        mAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { authTask ->
//                //if (!it.isSuccessful) return@addOnCompleteListener
//                if (authTask.isSuccessful()) {
//                    //val isNewUser = authTask.result!!.additionalUserInfo.isNewUser
//                    val firebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!
//
////                    val uid: String = firebaseUser.uid
////                    val name: String = firebaseUser.displayName
////                    val mEmail: String = firebaseUser.email!!
//                    //val user: CurrentUser = CurrentUser(firebaseUser)
//                    authenticatedUserMutableLiveData.setValue(firebaseUser);
//                }
//
//            }
//        return authenticatedUserMutableLiveData
//    }

//    suspend fun performLogin(email: String, password: String): String {
//        var loginResult : String? = ""
//
//        mAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
////                Log.d("Login", "Successfully logged in: ${it.result.user.uid}")
//                //Log.d("Login", "Successfully logged in: ")
//                loginResult = it.isSuccessful.toString()
//            }
//            .addOnFailureListener {
//                //Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
//                loginResult = "Failed to log in: ${it.message}"
//            }
//        return loginResult!!
////        if (loginResult) {
////            return loginResult.toString()
////        } else {
////            return "loginErrorMessage"
////        }
//    }

//    suspend fun performRegister(email: String, password: String): String {
//        var registerResult : String = "true"
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
//                registerResult = it.isSuccessful.toString()
//            }
//            .addOnFailureListener{
//                registerResult = "Failed to create user: ${it.message}"
//            }
//        return registerResult
////        if (registerResult) {
////            return registerResult.toString()
////        } else {
////            return "registerErrorMessage"
////        }
//    }

//    suspend fun saveUserToFirebaseDatabase(): String {
//        var saveUserResult = false
//        if (saveUserResult) {
//            return saveUserResult.toString()
//        } else {
//            return "saveErrorMessage"
//        }
//    }

//    suspend fun uploadImageToFirebaseStorage(): String {
//        var uploadResult = false
//        if (uploadResult) {
//            return uploadResult.toString()
//        } else {
//            return "uploadErrorMessage"
//        }
//    }
}