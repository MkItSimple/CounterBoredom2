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
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom2.BaseApplication
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.auth.RegisterActivity
import com.mkitsimple.counterboredom2.utils.Coroutines
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
import javax.inject.Inject

class ProfileActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = ProfileFragment()
        var currentUser: User? = null
    }

    private lateinit var viewModel: ProfileViewModel
    private var pictureChanged = false

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

            pictureChanged = true
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(RegisterActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(RegisterActivity.TAG, "File Location: $it")
                        //Picasso.get().load(it).into(circleImageViewMain)
                        updateProfile(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(RegisterActivity.TAG, "Failed to upload image to storage: ${it.message}")
                }
    }

    private fun updateProfile(profileImageUrl: String) {

        Coroutines.main{
            viewModel.updateProfile(profileImageUrl, editTextProfile.text.toString())
            viewModel.isSuccessful?.observe(this, androidx.lifecycle.Observer {
                if(it == true){
                    Toast.makeText(this, "Profile successfully updated!", Toast.LENGTH_LONG).show()
                    circleImageViewProfile.setImageBitmap(bitmap)
                }
            })
        }

//        val uid = FirebaseAuth.getInstance().uid ?: ""
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//
//        val user = Profile(uid, editTextProfile.text.toString(), profileImageUrl)
//
//        ref.setValue(user)
//            .addOnSuccessListener {
//                //Log.d(RegisterActivity.TAG, "Profile successfully updated!")
//                Toast.makeText(this, "Profile successfully updated!", Toast.LENGTH_LONG).show()
//                //circleImageViewMain.setImageBitmap(bitmap)
//            }
//            .addOnFailureListener {
//                //Log.d(RegisterActivity.TAG, "Failed to update value to database: ${it.message}")
//                Toast.makeText(this, "Profile successfully updated!", Toast.LENGTH_LONG).show()
//            }
    }
}
