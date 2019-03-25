package com.oliynick.max.toothpickadapter.misc

import android.os.Parcel
import android.os.Parcelable

/**
 *
 *
 * Describes injection target identifier
 *
 * Created by Максим on 2/20/2017.
 */
data class Key(val id: String) : Parcelable {

    private constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(id)

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Key> {
        override fun createFromParcel(parcel: Parcel): Key = Key(parcel)
        override fun newArray(size: Int): Array<Key?> = arrayOfNulls(size)
    }

}
