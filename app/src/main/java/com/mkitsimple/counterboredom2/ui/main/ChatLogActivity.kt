package com.mkitsimple.counterboredom2.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.views.ChatFromItem
import com.mkitsimple.counterboredom2.ui.views.ChatToItem
import com.mkitsimple.counterboredom2.ui.views.ImageFromItem
import com.mkitsimple.counterboredom2.ui.views.ImageToItem
import com.mkitsimple.counterboredom2.utils.longToast
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.custom_toolbar_chatlog.*


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
        val USER_KEY = "USER_KEY"
        //var currentUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var toUser: User? = null
    var fromId: String? = null
    var toId: String? = null
    var token: String? = null


    private lateinit var viewModel: ChatLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        viewModel = ViewModelProviders.of(this)[ChatLogViewModel::class.java]

        recylerViewChatLog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(USER_KEY)
        token = toUser?.token.toString()

        Picasso.get().load(toUser?.profileImageUrl).into(customToolbarChatLogCircleImageView)
        customToolbarChatLogTextView.text = toUser?.username

        fromId = FirebaseAuth.getInstance().uid
        toId = toUser?.uid
        //val uid = mAuth.uid

        backArrow.setOnClickListener {
            finish()
        }

        //setDummyData()
        //getCurrentUser(uid!!)
        listenForMessages()

        chatLogSendbuutton.setOnClickListener {
            //Log.d(TAG, "Attempt to send message....")
            performSendMessage(token!!)
        }

        chatLogSendImage.setOnClickListener {
            //Log.d(TAG, "Attempt to send image message....")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null // we put this outide the function . . so that we can use it later on

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data // is the uri . . basically where that image stored in the device
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)  // before we can use selectedImagePath we need to  make it as bitmap

            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
        viewModel.isUploadImageSuccessful.observe(this, Observer {
            if (it) {
                viewModel.uploadedImageUri.observe(this, Observer {
                    performSendImageMessage(it.toString())
                })
            } else {
                viewModel.uploadImageErrorMessage.observe(this, Observer { uploadImageErrorMessage ->
                    longToast("Failed to upload image to storage: $uploadImageErrorMessage")
                })
            }
        })
    }

    private fun listenForMessages() {
        viewModel.listenForMessages(toUser?.uid)
        viewModel.chatMessage.observe(this, Observer { chatMessage ->

            if (chatMessage.fromId == mAuth.uid) {
                //adapter.add(ChatToItem(chatMessage.text, currentUser))
                adapter.add(ChatFromItem(chatMessage.text, MainActivity.currentUser!!))
            } else {
                adapter.add(ChatToItem(chatMessage.text, toUser!!))
            }

            recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
        })

        viewModel.imageMessage.observe(this, Observer {imageMessage ->
            if (imageMessage.fromId == mAuth.uid) {
                adapter.add(ImageToItem(imageMessage!!.imagePath, MainActivity.currentUser!!))
            } else {
                adapter.add(ImageFromItem(imageMessage!!.imagePath, toUser!!))
            }
        })
    }

    private fun performSendImageMessage(fileLocation: String) {
        viewModel.performSendImageMessage(toId, fromId, fileLocation)

        viewModel.isSuccessful.observe(this, Observer { isSuccessful ->
            if(isSuccessful){
                chatLogEditText.text.clear()
                recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }
        })
    }

    private fun performSendMessage(token: String) {
        val text = chatLogEditText.text.toString()
        //val fromUsername = currentUser!!.username

        viewModel.performSendMessage(toId, fromId, text)

        viewModel.isSuccessful.observe(this, Observer { isSuccessful ->
            if(isSuccessful){
                chatLogEditText.text.clear()
                recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }
        })

        // send notification
        //viewModel.sendNotification(token, MainActivity.currentUser!!.username, text)

//        // send notification
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api =
//            retrofit.create(
//                Api::class.java
//            )
//
//        val call = api.sendNotification(token, MainActivity.currentUser!!.username, text)
//
//        call?.enqueue(object : Callback<ResponseBody?> {
//            override fun onResponse(
//                call: Call<ResponseBody?>,
//                response: Response<ResponseBody?>
//            ) {
//                try {
//                    toast(response.body()!!.string())
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onFailure(
//                call: Call<ResponseBody?>,
//                t: Throwable
//            ) {
//            }
//        })
//        // end  notification
    }
}
