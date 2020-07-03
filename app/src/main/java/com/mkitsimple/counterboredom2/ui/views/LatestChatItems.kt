package com.mkitsimple.counterboredom2.ui.views

import android.annotation.SuppressLint
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

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

                // Work with timestamp start
                val chatTimestamp = chatMessage.timestamp
                val mWhen = getWhen(chatTimestamp)
                // Work with timestamp end

                // if chat partner has profileImage
                if (chatPartnerUser?.profileImageUrl != "null") {
                    val targetImageView = viewHolder.itemView.circleImageViewLatestChats
                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
                }

                // message from who?
                if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                    who = chatPartnerUser?.username
                }

                var chatText = chatMessage.text
                if (chatText.length >= 30){
                    chatText  = chatText.substring(0, 30) + ".."
                }

                if (chatMessage.type == MessageType.TEXT) {
                    viewHolder.itemView.textViewMessageLatestMessage.text = "$who: " + chatText + "  " + mWhen
                } else {
                    viewHolder.itemView.textViewMessageLatestMessage.text = "$who sent a photo.  $mWhen"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    @SuppressLint("NewApi")
    fun getWhen(chatTimestamp: Long): String{
        val currentTimestamp = System.currentTimeMillis()

        // Chat Timestamp String
        val chatTimestampString = SimpleDateFormat("yyyy MMM ddEEE", Locale.getDefault()).format(chatTimestamp)
        val sChatYear = chatTimestampString.substring(0,11) // if more than 1 year
        val sChatMonth = chatTimestampString.substring(4,11) // if more than 1 month
        val sChatDay = chatTimestampString.substring(11,14) // if more than 1 day
        // Chat Timestamp Number
        val chatTimestampNumber = SimpleDateFormat("yyyyMMdd'T'h:mm a", Locale.getDefault()).format(chatTimestamp)
        val iChatYear = chatTimestampNumber.substring(0,4)
        val iChatMonth = chatTimestampNumber.substring(4,6)
        val iChatDay = chatTimestampNumber.substring(6,8)

        // chat timestamp time
        var iChatTime = ""
        val timeMPosition = chatTimestampNumber.indexOf("M", 1)
        if ( chatTimestampNumber.substring((timeMPosition - 7),(timeMPosition - 6)) != "T" ){
            iChatTime = chatTimestampNumber.substring((timeMPosition - 7),(timeMPosition+1))
        } else {
            iChatTime = chatTimestampNumber.substring((timeMPosition - 6),(timeMPosition+1))
        }

        // Current Timestamp Number
        val currentTimestampNumber = SimpleDateFormat("yyyyMMdd'T'h:mm a", Locale.getDefault()).format(currentTimestamp)
        val iCurrentYear = currentTimestampNumber.substring(0,4)
        val iCurrentMonth = currentTimestampNumber.substring(4,6)
        val iCurrentDay = currentTimestampNumber.substring(6,8)

        // Calculate number of Years Months Days
        val startLocalDate = LocalDate.of(iChatYear.toInt(), iChatMonth.toInt(), iChatDay.toInt()) // old timestamp
        val endLocalDate = LocalDate.of(iCurrentYear.toInt(), iCurrentMonth.toInt(), iCurrentDay.toInt()) // new timestamp
        //val endLocalDate = LocalDate.of(2020, 7, 2) // new timestamp
        val periodBetween: Period = Period.between(startLocalDate, endLocalDate)

        // Store to variables the count of years, months, and days
        val inputString = periodBetween.toString()
        val toFind = "D"
        val yPosition = inputString.indexOf("Y", 0)
        val mPosition = inputString.indexOf("M", 0)
        val dPosition = inputString.indexOf("D", 0)
        var yearsCount : String = "0"
        var monthsCount : String = "0"
        var daysCount : String = "0"

        if (yPosition > -1){
            if (inputString.substring((yPosition - 2), (yPosition - 1)) != "P") {
                yearsCount = inputString.substring((yPosition - 2), (yPosition))
            } else {
                yearsCount = inputString.substring((yPosition - 1), (yPosition))
            }
        }

        if (mPosition > -1){
            val beforeM = inputString.substring((mPosition - 2), (mPosition - 1))
            if (beforeM != "Y" && beforeM != "P") {
                monthsCount = inputString.substring((mPosition - 2), (mPosition))
            } else {
                monthsCount = inputString.substring((mPosition - 1), (mPosition))
            }
        }

        if (dPosition > -1){
            val beforeD = inputString.substring((dPosition - 2), (dPosition - 1))
            if (beforeD != "Y" && beforeD != "M" && beforeD != "P") {
                daysCount = inputString.substring((dPosition - 2), (dPosition))
            } else {
                daysCount = inputString.substring((dPosition - 1), (dPosition))
            }
        }

        var mWhen = iChatTime
        if (daysCount.toInt() > 0){ mWhen = sChatDay } // more than 1 day
        if (monthsCount.toInt() > 0){ mWhen = sChatMonth } // more than 1 month
        if (yearsCount.toInt() > 0){ mWhen = sChatYear } // more than 1 year

        return mWhen
    }

    override fun getLayout(): Int {
        return R.layout.row_latest_chats
    }
}