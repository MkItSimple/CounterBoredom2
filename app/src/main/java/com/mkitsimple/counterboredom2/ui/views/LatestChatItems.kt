package com.mkitsimple.counterboredom2.ui.views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.ChatMessage
import com.mkitsimple.counterboredom2.data.models.MessageType
import com.mkitsimple.counterboredom2.data.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_latest_chats.view.*

class LatestChatItems (val chatMessage: ChatMessage): Item<ViewHolder>() {
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        var who : String? = "You"
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textViewUsernameLatestChats.text = chatPartnerUser?.username

                if (chatPartnerUser?.profileImageUrl != "null") {
                    val targetImageView = viewHolder.itemView.circleImageViewLatestChats
                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
                }


                if (chatMessage.type == MessageType.TEXT) {
                    viewHolder.itemView.textViewMessageLatestMessage.text = chatMessage.text
                } else {
                    if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                        who = chatPartnerUser?.username
                    }
                    viewHolder.itemView.textViewMessageLatestMessage.text = "$who sent a photo."
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.row_latest_chats
    }
}