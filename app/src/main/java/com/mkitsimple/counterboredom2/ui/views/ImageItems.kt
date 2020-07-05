package com.mkitsimple.counterboredom2.ui.views

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.ImageMessage
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.main.ChatLogActivity
import com.mkitsimple.counterboredom2.ui.main.MyDialogBottomSheet
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_from_row.view.*
import kotlinx.android.synthetic.main.image_to_row.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*


class ImageFromItem(
    val image: ImageMessage,
    val user: User,
    val supportFragmentManager: FragmentManager
) : Item<ViewHolder>() {

    private var myContext: ChatLogActivity? = null
    val filename: String = image.filename

    override fun bind(viewHolder: ViewHolder, position: Int) {

        //val imageUri = image
        val chatTimestamp = image.timestamp
        val mWhen = getWhen(chatTimestamp)
        viewHolder.itemView.fromRowWhen.text = mWhen

        val imageviewFromRow = viewHolder.itemView.imageview_from_row
        Picasso.get().load(image.imagePath).into(imageviewFromRow)

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)

        imageviewFromRow.setOnClickListener {
            Toast.makeText(it.context, ""+image.filename, Toast.LENGTH_LONG).show()
        }

        imageviewFromRow.setOnLongClickListener {
            //download(it, uri)
            MyDialogBottomSheet(filename).show(
                supportFragmentManager,
                ""
            )
            return@setOnLongClickListener true
        }
    }

    private fun download(it: View, uri: String) {
        Toast.makeText(it.context, "ImageFromItem Long clicked $uri", Toast.LENGTH_LONG).show()
    }

    private fun showBottomSheet(it: View) {

    }

    override fun getLayout(): Int {
        return R.layout.image_from_row
    }

//    override fun bind(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
//        super.bind(holder, position, payloads)
//        holder.itemView.imageViewFromRow.setOnClickListener {
//
//        }
//    }

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
}

class ImageToItem(val image: ImageMessage, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val chatTimestamp = image.timestamp
        val mWhen = getWhen(chatTimestamp)
        viewHolder.itemView.toRowWhen.text = mWhen

        //viewHolder.itemView.textview_to_row.text = "IMAGE " + image
        //val imageUri = image
        val imageviewToRow = viewHolder.itemView.imageview_to_row
        Picasso.get().load(image.imagePath).into(imageviewToRow)

        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)

        imageviewToRow.setOnLongClickListener {
            showToast(it)
            return@setOnLongClickListener true
        }
    }

    private fun showToast(it: View) {
        Toast.makeText(it.context, "Long click", Toast.LENGTH_LONG).show()
    }

    override fun getLayout(): Int {
        return R.layout.image_to_row
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
}