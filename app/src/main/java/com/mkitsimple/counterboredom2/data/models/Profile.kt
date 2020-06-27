package com.mkitsimple.counterboredom2.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Profile(val uid: String, val username: String, val profileImageUrl: String):
    Parcelable {
    constructor() : this("", "", "")
}