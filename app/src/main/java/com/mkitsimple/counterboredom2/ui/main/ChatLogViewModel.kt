package com.mkitsimple.counterboredom2.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.mkitsimple.counterboredom2.data.models.ChatMessage
import com.mkitsimple.counterboredom2.data.models.ImageMessage
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.data.repositories.MessageRepository
import com.mkitsimple.counterboredom2.utils.NODE_USERS
import kotlinx.coroutines.Job
import javax.inject.Inject

class ChatLogViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: MessageRepository

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


    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}
        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {}
        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val mChatMessage = snapshot.getValue(ChatMessage::class.java)
            _chatMessage.value = mChatMessage
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {}
    }

    fun getRealtimeUpdates() {
        dbUsers.addChildEventListener(childEventListener)
    }

    var listenForMessagesResultChatMessage: LiveData<Triple<ChatMessage, ImageMessage, Int>>? = null
    suspend fun listenForMessages(uid: String?) {
        listenForMessagesResultChatMessage = repository.listenForMessages(uid)

//        val fromId = FirebaseAuth.getInstance().uid
//        val toId = uid
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d(ChatLogActivity.TAG, "onCancelled")
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                Log.d(ChatLogActivity.TAG, "onChildMoved")
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                Log.d(ChatLogActivity.TAG, "onChildChanged")
//            }
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val mChatMessage = p0.getValue(ChatMessage::class.java)
//
//                if (mChatMessage!!.type == MessageType.TEXT) {
//                    _chatMessage.value = mChatMessage
//                } else {
//                    val mImageMessage = p0.getValue(ImageMessage::class.java)
//                    _imageMessage.value = mImageMessage
//                }
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//                Log.d(ChatLogActivity.TAG, "onChildRemoved")
//            }
//
//        })
    }

//    var isSuccessful: LiveData<Any>? = null
//    suspend fun updateProfile(username: String, profileImageUrl: String) {
//        isSuccessful = repository.updateProfile(profileImageUrl, username)
//    }

    var isPerformSendMessageSuccessful: LiveData<Boolean>? = null
    suspend fun performSendMessage(toId: String, fromId: String?, text: String) {
        if (fromId == null) return
        isPerformSendMessageSuccessful = repository.performSendMessage(toId, fromId, text)
//        if (fromId == null) return
//
//        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
//
//        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
//
//        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
//        reference.setValue(chatMessage)
//            .addOnSuccessListener {
//                Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
//                //edittext_chat_log.text.clear()
//                //recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//                _isSuccessful.value = true
//
//            }
//            .addOnFailureListener{
//                _isSuccessful.value = false
//            }
//
//        toReference.setValue(chatMessage)
//
//        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
//        latestMessageRef.setValue(chatMessage)
//
//        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
//        latestMessageToRef.setValue(chatMessage)
    }

    var isPerformSendImageMessage: LiveData<Boolean>? = null
    suspend fun performSendImageMessage(toId: String?, fromId: String?, fileLocation: String) {
        if (fromId == null) return

        isPerformSendImageMessage = repository.performSendImageMessage(toId, fromId, fileLocation)

//        // chat copy for sender
//        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
//
//        // chat copy for reciever
//        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
//
//        val imageMessage = ImageMessage(reference.key!!, fileLocation, fromId, toId!!, System.currentTimeMillis() / 1000)
//
//        //Log.d("ImageMessage", "Image Path: " + imageMessage.imagePath + "Image Type:" + imageMessage.type)
//
//        // setValue inerted the chat to database . . . then scroll recyclerview to the bottom
//        reference.setValue(imageMessage)
//            .addOnSuccessListener {
//                Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
//                _isSuccessful.value = true
//
//            }
//            .addOnFailureListener{
//                _isSuccessful.value = false
//            }
//
//        toReference.setValue(imageMessage)
//
//        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
//        latestMessageRef.setValue(imageMessage)
//
//        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
//        latestMessageToRef.setValue(imageMessage)
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

    var uploadImageResult: LiveData<Pair<Boolean, String>>? = null
    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        uploadImageResult = repository.uploadImageToFirebaseStorage(selectedPhotoUri)

//        val filename = UUID.randomUUID().toString()
//        val ref = FirebaseStorage.getInstance().getReference("/messages/$filename")
//
//        ref.putFile(selectedPhotoUri)
//            .addOnSuccessListener {
//                _isUploadImageSuccessful.value = true
//
//                ref.downloadUrl.addOnSuccessListener {
//                    _uploadedImageUri.value = it.toString()
//                }
//            }
//            .addOnFailureListener {
//                _isUploadImageSuccessful.value = false
//                _uploadImageErrorMessage.value = it.message
//            }
    }

    private lateinit var jobSendNotification: Job
    private val _isNotificationSuccessful = MutableLiveData<Boolean>()
    val isNotificationSuccessful: LiveData<Boolean> get() = _isNotificationSuccessful

    var isSendNotificationSuccessful: LiveData<Boolean>? = null
    suspend fun sendNotification(token: String, username: String, text: String) {
        isSendNotificationSuccessful = repository.sendNotification(token, username, text)
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
//                    call: Call<ResponseBody?>,
//                    response: Response<ResponseBody?>
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
    }

    override fun onCleared() {
        super.onCleared()
        if(::jobSendNotification.isInitialized) jobSendNotification.cancel()
    }

}