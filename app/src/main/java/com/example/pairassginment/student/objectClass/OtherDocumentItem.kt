package com.example.pairassginment.student.objectClass

import android.os.Parcel
import android.os.Parcelable

data class OtherDocumentItem(
    val studentComment: String? = null,
    val dateSubmitted: String? = null,
    val dateFeedback: String? = null,
    val uploadedFileOrg: String? = null,
    val uploadedFile: String? = null,
    val submittedStatus: String? = null,
    val supervisorComment: String? = null,
    val documentID: String? = null,
    val documentType: String? = null,
    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
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
        parcel.writeString(studentComment)
        parcel.writeString(dateSubmitted)
        parcel.writeString(dateFeedback)
        parcel.writeString(uploadedFileOrg)
        parcel.writeString(uploadedFile)
        parcel.writeString(submittedStatus)
        parcel.writeString(supervisorComment)
        parcel.writeString(documentID)
        parcel.writeString(documentType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OtherDocumentItem> {
        override fun createFromParcel(parcel: Parcel): OtherDocumentItem {
            return OtherDocumentItem(parcel)
        }

        override fun newArray(size: Int): Array<OtherDocumentItem?> {
            return arrayOfNulls(size)
        }
    }
}

