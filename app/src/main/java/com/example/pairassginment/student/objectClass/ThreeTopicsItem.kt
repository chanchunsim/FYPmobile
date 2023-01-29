package com.example.pairassginment.student.objectClass

import android.os.Parcel
import android.os.Parcelable

data class ThreeTopicsItem(
    val title: String? = null,
    val abstract: String? = null,
    val date_submitted: String? = null,
    val date_feedback: String? = null,
    val supervisor_comment: String? = null,
    val status: String? = null,
    val topic_id: String? = null,
    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(abstract)
        parcel.writeString(date_submitted)
        parcel.writeString(date_feedback)
        parcel.writeString(supervisor_comment)
        parcel.writeString(status)
        parcel.writeString(topic_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ThreeTopicsItem> {
        override fun createFromParcel(parcel: Parcel): ThreeTopicsItem {
            return ThreeTopicsItem(parcel)
        }

        override fun newArray(size: Int): Array<ThreeTopicsItem?> {
            return arrayOfNulls(size)
        }
    }
}
