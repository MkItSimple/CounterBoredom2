package com.mkitsimple.counterboredom.ui.views

import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatFromItem(
    val chat: ChatMessage,
    val user: User,
    val cWhen: String
): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val mWhen = cWhen
        viewHolder.itemView.fromRowWhen.text = mWhen
        viewHolder.itemView.textViewFromRow.text = chat.text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageViewFromRow
        if (uri != "null"){
            Picasso.get().load(uri).into(targetImageView)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(
    val chat: ChatMessage,
    val user: User,
    val cWhen: String
): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val mWhen = cWhen
        viewHolder.itemView.toRowWhen.text = mWhen

        viewHolder.itemView.textViewToRow.text = chat.text

        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageViewToRow
        if (uri != "null"){
            Picasso.get().load(uri).into(targetImageView)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}