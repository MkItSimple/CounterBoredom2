package com.mkitsimple.counterboredom2.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.mkitsimple.counterboredom2.BaseApplication
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.ui.main.MainActivity
import com.mkitsimple.counterboredom2.utils.Coroutines
import com.mkitsimple.counterboredom2.utils.longToast
import com.mkitsimple.counterboredom2.utils.toast
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var token: String? = null

    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ( this.applicationContext as BaseApplication).appComponent
            .newAuthComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory)[AuthViewModel::class.java]

        // generate registration token for this device
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result!!.token
                    //saveToken(token)
                    Log.d(TAG, token!!)
                }
            }

        textViewAlreadyHaveAccount.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonSelectPhoto.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        buttonRegister.setOnClickListener {
            performRegister()
        }
    }

    var selectedPhotoUri: Uri? = null
    var downloadedPhotoUri: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imageViewSelectPhoto.setImageBitmap(bitmap)
            buttonSelectPhoto.alpha = 0f
        }
    }

    private fun performRegister() {
        val username = editTextUsername.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (username.isEmpty()) {
            toast("Please fill out username")
            return
        }

        if (email.isEmpty()) {
            toast("Please fill out email")
            return
        }

        if (password.isEmpty()) {
            toast("Please fill out password")
            return
        }

//        viewModel.performRegister(email, password)
//        viewModel.isRegisterSuccessful.observe(this, Observer {
//            if (it){
//                uploadImageToFirebaseStorage()
//            }
//        })

        Coroutines.main {
            viewModel.performRegister(email, password)
            viewModel.registerResult?.observe(this, Observer {
                if (it == true) {
                    uploadImageToFirebaseStorage()
                } else {
                    longToast("Failed to create user: ${it}")
                }
            })
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase(token)
        } else {
            Coroutines.main {
                viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
                viewModel.uploadResult?.observe(this, Observer {
                    saveUserToFirebaseDatabaseWithProfileImage(it.toString(), token)
                })
            }
        }
    }

    // Save User
    private fun saveUserToFirebaseDatabase(token: String?) {
        Coroutines.main {
            viewModel.saveUserToFirebaseDatabase(editTextUsername.text.toString(), "null", token!!)
            viewModel.saveUserResult?.observe(this, Observer {
                if (it == true) {
                    loginUser()
                } else {
                    longToast("Failed to upload image to storage: ${it}")
                }
            })
        }
    }

    // Save User with Profile Image
    private fun saveUserToFirebaseDatabaseWithProfileImage(profileImageUrl: String?, token: String?) {
        Coroutines.main {
            viewModel.saveUserToFirebaseDatabase(editTextUsername.text.toString(), profileImageUrl!!, token!!)
            viewModel.saveUserResult?.observe(this, Observer {
                if (it == true) {
                    loginUser()
                } else {
                    longToast("Failed to upload image to storage: ${it}")
                }
            })
        }
    }

    private fun loginUser() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
