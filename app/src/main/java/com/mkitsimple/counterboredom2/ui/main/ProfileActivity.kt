package com.mkitsimple.counterboredom2.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.mkitsimple.counterboredom2.BaseApplication
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.auth.RegisterActivity
import com.mkitsimple.counterboredom2.utils.longToast
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = ProfileFragment()
        var currentUser: User? = null
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var job1: Job
    private lateinit var job2: Job
    private lateinit var job3: Job

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ( this.applicationContext as BaseApplication).appComponent
                .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(ProfileViewModel::class.java)

        //buttonSaveChanges.isEnabled = false
        if (MainActivity.currentUser?.profileImageUrl != "null") {
            Picasso.get().load(MainActivity.currentUser?.profileImageUrl)
                    .into(circleImageViewProfile)
        }

        editTextProfile.setText(MainActivity.currentUser!!.username)

        circleImageViewProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        buttonSaveChanges.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        profileBackArrow.setOnClickListener {
            finish()
        }
        initJobs()
    }

    private fun initJobs() {
        job1 = Job()
        job2 = Job()
        job3 = Job()
    }

    var selectedPhotoUri: Uri? = null
    var bitmap: Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(RegisterActivity.TAG, "Photo was selected")

            selectedPhotoUri = data.data
            bitmap = MediaStore.Images.Media
                    .getBitmap(this.contentResolver, selectedPhotoUri)

            circleImageViewProfile.setImageBitmap(bitmap)
            //buttonSelectPhotoProfile.alpha = 0f
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val username = editTextProfile.text.toString()

        if(username.isEmpty()){
            longToast("Please fill out username")
            return
        }

        if(username.length < 2){
            longToast("Username must be more than 2 characters")
            return
        }

        if (username.length > 20) {
            longToast("Username should not be more than 20 characters")
            return
        }

        if (username == MainActivity.currentUser!!.username && selectedPhotoUri == null) {
            longToast("There is no changes has been made")
            return
        }

        CoroutineScope(Dispatchers.Main + job1).launch{
            if (selectedPhotoUri == null) {
                updateProfile()
            } else {
                viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
                viewModel.isUploadSuccessful?.observe(this@ProfileActivity, androidx.lifecycle.Observer {
                    if (it.first) {
                        updateProfileWithImage(it.second)
                    }
                })
            }
        }
    }

    private fun updateProfile() {

        CoroutineScope(Dispatchers.Main + job2).launch{
            viewModel.updateProfile(editTextProfile.text.toString(), MainActivity.currentUser)
            viewModel.isSuccessful?.observe(this@ProfileActivity, androidx.lifecycle.Observer {
                if (it == true) {
                    Toast.makeText(applicationContext, "Profile successfully updated!", Toast.LENGTH_LONG).show()
                    updateCircleImageViewProfile()

                }
            })
        }
    }

    private fun updateProfileWithImage(profileImageUrl: String) {

        CoroutineScope(Dispatchers.Main + job3).launch{
            viewModel.updateProfileWithImage(editTextProfile.text.toString(), profileImageUrl, MainActivity.currentUser!!.token)
            viewModel.isSuccessful2?.observe(this@ProfileActivity, androidx.lifecycle.Observer {
                if(it == true){
                    Toast.makeText(applicationContext, "Profile successfully updated!", Toast.LENGTH_LONG).show()
                    updateCircleImageViewProfile()
                }
            })
        }
    }

    private fun updateCircleImageViewProfile(){
        if (selectedPhotoUri != null){
            circleImageViewProfile.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
        if(::job2.isInitialized) job2.cancel()
        if(::job3.isInitialized) job3.cancel()
    }
}
