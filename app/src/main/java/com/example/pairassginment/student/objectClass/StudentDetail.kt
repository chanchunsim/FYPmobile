package com.example.pairassginment.student.objectClass

import android.os.Parcel
import android.os.Parcelable

data class StudentDetail(
    var role_id: String? = null,
    var student_name: String? = null,
    var student_id: String? = null,
    var batch_id: String? = null,
    var mark_id: String? = null,
    var submission_id: String? = null
        ) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(role_id)
        parcel.writeString(student_name)
        parcel.writeString(student_id)
        parcel.writeString(batch_id)
        parcel.writeString(mark_id)
        parcel.writeString(submission_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudentDetail> {
        override fun createFromParcel(parcel: Parcel): StudentDetail {
            return StudentDetail(parcel)
        }

        override fun newArray(size: Int): Array<StudentDetail?> {
            return arrayOfNulls(size)
        }
    }

}