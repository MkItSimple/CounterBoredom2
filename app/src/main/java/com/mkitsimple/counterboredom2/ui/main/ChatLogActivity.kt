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
import com.mkitsimple.counterboredom2.BaseApplication
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.views.ChatFromItem
import com.mkitsimple.counterboredom2.ui.views.ChatToItem
import com.mkitsimple.counterboredom2.ui.views.ImageFromItem
import com.mkitsimple.counterboredom2.ui.views.ImageToItem
import com.mkitsimple.counterboredom2.utils.longToast
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.custom_toolbar_chatlog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
        val USER_KEY = "USER_KEY"
    }

    val adapter = GroupAdapter<ViewHolder>()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var toUser: User? = null
    var fromId: String? = null
    var toId: String? = null
    var token: String? = null


    private lateinit var viewModel: ChatLogViewModel
    private lateinit var job1: Job
    private lateinit var job2: Job
    private lateinit var job3: Job
    private lateinit var job4: Job

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        ( this.applicationContext as BaseApplication).appComponent
                .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory)[ChatLogViewModel::class.java]
        job1 = Job()
        job2 = Job()
        job3 = Job()
        job4 = Job()

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
        listenForMessages()

        // Attemt to send message
        chatLogSendbutton.setOnClickListener {
            performSendMessage(token!!)
        }

        // Attempt to send image message
        chatLogSendImage.setOnClickListener {
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

        CoroutineScope(Dispatchers.Main + job1).launch{
            viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
            viewModel.uploadImageResult?.observe(this@ChatLogActivity, Observer {
                if (it.first) {
                    performSendImageMessage(it.second)
                } else {
                    viewModel.uploadImageErrorMessage.observe(this@ChatLogActivity, Observer { uploadImageErrorMessage ->
                        longToast("Failed to upload image to storage: $uploadImageErrorMessage")
                    })
                }
            })
        }
    }

    private fun listenForMessages() {
        CoroutineScope(Dispatchers.Main + job2).launch{
            viewModel.listenForMessages(toUser?.uid)
            viewModel.listenForMessagesResultChatMessage?.observe(this@ChatLogActivity, Observer {
                // Check if 1 ChatMessage, if 2 ImageMessage
                if (it.third == 1){
                    val chatMessage = it.first
                    if (chatMessage.fromId == mAuth.uid) {
                        adapter.add(ChatFromItem(chatMessage.text, MainActivity.currentUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                } else {
                    val imageMessage = it.second
                    if (imageMessage.fromId == mAuth.uid) {
                        adapter.add(ImageToItem(imageMessage.imagePath, MainActivity.currentUser!!))
                    } else {
                        adapter.add(ImageFromItem(imageMessage.imagePath, toUser!!))
                    }
                }
            })
        }
    }

    private fun performSendImageMessage(fileLocation: String) {
        CoroutineScope(Dispatchers.Main + job3).launch{
            viewModel.performSendImageMessage(toId, fromId, fileLocation)
            viewModel.isSuccessful.observe(this@ChatLogActivity, Observer { isSuccessful ->
                if(isSuccessful){
                    chatLogEditText.text.clear()
                    recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                }
            })
        }
    }

    private fun performSendMessage(token: String) {
        val text = chatLogEditText.text.toString()
        CoroutineScope(Dispatchers.Main + job4).launch{
            viewModel.performSendMessage(toId!!, fromId, text)
            viewModel.isPerformSendMessageSuccessful?.observe(this@ChatLogActivity, Observer {
                if(it){
                    chatLogEditText.text.clear()
                    recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                }
            })
            // Send notification to receiver
            //viewModel.sendNotification(token, MainActivity.currentUser!!.username, text)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }
}
