package com.mkitsimple.counterboredom2.ui.views

import com.mkitsimple.counterboredom2.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class FriendsListItems(): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.username_textview_new_message.text = "Your Friends Name"
        //Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
//        if(chatPartnerUser?.profileImageUrl == "null") {
//            Picasso.get().load(R.drawable.profile_black).into(targetImageView)
//        }

//        if(chatPartnerUser?.profileImageUrl == "null") {
//            Picasso.get().load(R.drawable.profile_black).into(targetImageView)
//        } else {
//            Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
//        }
    }

    override fun getLayout(): Int {
        return R.layout.row_friends_list
    }
}