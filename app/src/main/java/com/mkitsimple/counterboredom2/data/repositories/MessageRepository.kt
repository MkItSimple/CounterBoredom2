package com.mkitsimple.counterboredom2.data.repositories

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom2.data.models.ChatMessage
import com.mkitsimple.counterboredom2.data.models.ImageMessage
import com.mkitsimple.counterboredom2.data.models.MessageType
import com.mkitsimple.counterboredom2.data.network.Api
import com.mkitsimple.counterboredom2.ui.main.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class MessageRepository (val api: Api){

    suspend fun performSendMessage(toId: String, fromId: String, text: String): MutableLiveData<Boolean>? {
        val returnValue = MutableLiveData<Boolean>()

        // senders copy
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        // recievers copy
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis(), "")
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                returnValue.value = true

            }
            .addOnFailureListener{
                returnValue.value = false
            }

        toReference.setValue(chatMessage)

        // senders chat copy
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        // recievers chat copy
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

        return returnValue
    }

    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri): MutableLiveData<Triple<Boolean, String, String>>? {
        val returnValue = MutableLiveData<Triple<Boolean, String, String>>()

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/messages/$filename")

        ref.putFile(selectedPhotoUri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        val triple = Triple(true, it.toString(), filename)
                        returnValue.value = triple
                    }
                }
                .addOnFailureListener {
                    val triple = Triple(true, it.message!!, filename)
                    returnValue.value = triple
                }
        return returnValue
    }

    suspend fun listenForMessages(uid: String?): MutableLiveData<Triple<ChatMessage, ImageMessage, Int>>? {
        val returnValue = MutableLiveData<Triple<ChatMessage, ImageMessage, Int>>()

        val fromId = FirebaseAuth.getInstance().uid
        val toId = uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val mChatMessage = p0.getValue(ChatMessage::class.java)
                val mImageMessage = p0.getValue(ImageMessage::class.java)
                var mInt = 1
                if (mChatMessage!!.type == MessageType.IMAGE)
                    mInt = 2

                val pair = Triple(mChatMessage, mImageMessage!!, mInt)
                returnValue.value = pair
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

        })

        return returnValue
    }

    suspend fun performSendImageMessage(toId: String?, fromId: String, fileLocation: String, filename: String): MutableLiveData<Boolean>? {
        val returnValue = MutableLiveData<Boolean>()

        // chat copy for sender
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        // chat copy for reciever
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val imageMessage = ImageMessage(reference.key!!, fileLocation, fromId, toId!!, System.currentTimeMillis(), filename)


        // setValue inerted the chat to database . . . then scroll recyclerview to the bottom
        reference.setValue(imageMessage)
                .addOnSuccessListener {
                    //Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
                    returnValue.value = true

                }
                .addOnFailureListener{
                    returnValue.value = false
                }

        toReference.setValue(imageMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(imageMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(imageMessage)

        return returnValue
    }

//    suspend fun sendNotification(token: String, username: String, text: String): MutableLiveData<Boolean>? {
//        val returnValue = MutableLiveData<Boolean>()
//
////        val call = api.sendNotification(token, username, text)
////
////        call?.enqueue(object : Callback<ResponseBody?> {
////            override fun onResponse(
////                    call: Call<ResponseBody?>,
////                    response: Response<ResponseBody?>
////            ) {
////                try {
////                    returnValue.value = true
////                    //toast(response.body()!!.string())
////                } catch (e: IOException) {
////                    e.printStackTrace()
////                }
////            }
////
////            override fun onFailure(
////                call: Call<ResponseBody?>,
////                t: Throwable
////            ) {
////            }
////        })
//
//        return returnValue
//    }

    suspend fun listenForLatestMessages(): MutableLiveData<HashMap<String, ChatMessage>>? {
        val returnValue = MutableLiveData<HashMap<String, ChatMessage>>()

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
            //.orderByChild("key/username")

        val latestMessagesMap = HashMap<String, ChatMessage>()

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage // p0.key belong to the user that we're messaging
                returnValue.value = latestMessagesMap
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                returnValue.value = latestMessagesMap
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })


        return returnValue

    }
}