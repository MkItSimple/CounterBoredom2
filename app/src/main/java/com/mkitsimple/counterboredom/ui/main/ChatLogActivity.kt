package com.mkitsimple.counterboredom.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.ui.views.ChatFromItem
import com.mkitsimple.counterboredom.ui.views.ChatToItem
import com.mkitsimple.counterboredom.ui.views.ImageFromItem
import com.mkitsimple.counterboredom.ui.views.ImageToItem
import com.mkitsimple.counterboredom.utils.longToast
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
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
        const val TAG = "ChatLog"
        const val USER_KEY = "USER_KEY"
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
            //performSendMessage(token!!)
            performSendMessage()
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
            selectedPhotoUri = data.data
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        CoroutineScope(Dispatchers.Main + job1).launch{
            viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
            viewModel.uploadImageResult?.observe(this@ChatLogActivity, Observer {
                if (it.first) {
                    performSendImageMessage(it.second, it.third)
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
                    val chatMessage = it.first.first
                    val cWhen = it.second.first
                    if (chatMessage.fromId == mAuth.uid) {
                        adapter.add(ChatFromItem(chatMessage, MainActivity.currentUser!!, cWhen))
                    } else {
                        adapter.add(ChatToItem(chatMessage, toUser!!, cWhen))
                    }
                } else {
                    val imageMessage = it.first.second
                    val mWhen = it.second.second
                    if (imageMessage.fromId == mAuth.uid) {
                        adapter.add(ImageToItem(imageMessage, MainActivity.currentUser!!, supportFragmentManager, mWhen))
                    } else {
                        adapter.add(ImageFromItem(imageMessage, toUser!!, supportFragmentManager, mWhen))
                    }
                }

                recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            })
        }
    }

    private fun performSendImageMessage(fileLocation: String, filename: String) {
        CoroutineScope(Dispatchers.Main + job3).launch{
            viewModel.performSendImageMessage(toId, fromId, fileLocation, filename)
            viewModel.isSuccessful.observe(this@ChatLogActivity, Observer { isSuccessful ->
                if(isSuccessful){
                    chatLogEditText.text.clear()
                    recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                }
            })
        }
    }

    //private fun performSendMessage(token: String) {
    private fun performSendMessage() {
        val text = chatLogEditText.text.toString()
        if(text != ""){
            CoroutineScope(Dispatchers.Main + job4).launch{
                viewModel.performSendMessage(toId!!, fromId, text)
                viewModel.isPerformSendMessageSuccessful?.observe(this@ChatLogActivity, Observer {
                    if(it){
                        chatLogEditText.text.clear()
                        recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                    }
                })

                // Send notification to receiver
                viewModel.sendNotification(token!!, MainActivity.currentUser!!.username, text)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }
}