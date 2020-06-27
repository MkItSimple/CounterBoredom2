package com.mkitsimple.counterboredom2.ui.views

import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_friends_list.view.*

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.friendsListTextView.text = user.username
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.friendsListCircleImageView

        if (uri != "null") {
            Picasso.get().load(user.profileImageUrl).into(targetImageView)
        }
    }

    override fun getLayout(): Int {
        return R.layout.row_friends_list
    }
}