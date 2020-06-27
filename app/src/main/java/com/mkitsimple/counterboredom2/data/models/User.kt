package com.mkitsimple.counterboredom2.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val token: String): Parcelable {
    constructor() : this("", "", "", "")
}