package com.mkitsimple.counterboredom2.ui.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom2.data.models.ChatMessage
import com.mkitsimple.counterboredom2.data.models.ImageMessage
import com.mkitsimple.counterboredom2.data.models.MessageType
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.utils.NODE_USERS
import kotlinx.coroutines.Job
import java.util.*
import javax.inject.Inject

class ChatLogViewModel : ViewModel() {

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _chatMessage = MutableLiveData<ChatMessage>()
    val chatMessage: LiveData<ChatMessage>
        get() = _chatMessage

    private val _imageMessage = MutableLiveData<ImageMessage>()
    val imageMessage: LiveData<ImageMessage>
        get() = _imageMessage

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean>
        get() = _isSuccessful


    fun fetchFilteredUsers(uid: String) {
        val dbUsers = dbUsers.child(uid)

        dbUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _user.value = snapshot.getValue(User::class.java)
                }
            }
        })
    }

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val mChatMessage = snapshot.getValue(ChatMessage::class.java)
            _chatMessage.value = mChatMessage
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }
    }

    fun getRealtimeUpdates() {
        dbUsers.addChildEventListener(childEventListener)
    }

    fun listenForMessages(uid: String?) {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(ChatLogActivity.TAG, "onCancelled")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d(ChatLogActivity.TAG, "onChildMoved")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d(ChatLogActivity.TAG, "onChildChanged")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val mChatMessage = p0.getValue(ChatMessage::class.java)

                if (mChatMessage!!.type == MessageType.TEXT) {
                    _chatMessage.value = mChatMessage
                } else {
                    val mImageMessage = p0.getValue(ImageMessage::class.java)
                    _imageMessage.value = mImageMessage
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(ChatLogActivity.TAG, "onChildRemoved")
            }

        })
    }

    fun performSendMessage(toId: String?, fromId: String?, text: String) {
        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
                //edittext_chat_log.text.clear()
                //recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                _isSuccessful.value = true

            }
            .addOnFailureListener{
                _isSuccessful.value = false
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

    fun performSendImageMessage(toId: String?, fromId: String?, fileLocation: String) {
        //val user = intent.getParcelableExtra<User>(FriendsListFragment.USER_KEY)
        if (fromId == null) return

        // chat copy for sender
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        // chat copy for reciever
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val imageMessage = ImageMessage(reference.key!!, fileLocation, fromId, toId!!, System.currentTimeMillis() / 1000)

        //Log.d("ImageMessage", "Image Path: " + imageMessage.imagePath + "Image Type:" + imageMessage.type)

        // setValue inerted the chat to database . . . then scroll recyclerview to the bottom
        reference.setValue(imageMessage)
            .addOnSuccessListener {
                Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
                _isSuccessful.value = true

            }
            .addOnFailureListener{
                _isSuccessful.value = false
            }

        toReference.setValue(imageMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(imageMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(imageMessage)
    }

    // Send Image
    private val _isUploadImageSuccessful = MutableLiveData<Boolean>()
    val isUploadImageSuccessful: LiveData<Boolean>
        get() = _isUploadImageSuccessful

    private val _uploadedImageUri = MutableLiveData<String>()
    val uploadedImageUri: LiveData<String>
        get() = _uploadedImageUri

    private val _uploadImageErrorMessage = MutableLiveData<String>()
    val uploadImageErrorMessage: LiveData<String>
        get() = _uploadImageErrorMessage

    fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/messages/$filename")

        ref.putFile(selectedPhotoUri)
            .addOnSuccessListener {
                _isUploadImageSuccessful.value = true

                ref.downloadUrl.addOnSuccessListener {
                    _uploadedImageUri.value = it.toString()
                }
            }
            .addOnFailureListener {
                _isUploadImageSuccessful.value = false
                _uploadImageErrorMessage.value = it.message
            }
    }

    private lateinit var jobSendNotification: Job
    private val _isNotificationSuccessful = MutableLiveData<Boolean>()
    val isNotificationSuccessful: LiveData<Boolean> get() = _isNotificationSuccessful

    fun sendNotification(token: String, username: String, text: String) {

    }

    override fun onCleared() {
        super.onCleared()
        if(::jobSendNotification.isInitialized) jobSendNotification.cancel()
    }

}