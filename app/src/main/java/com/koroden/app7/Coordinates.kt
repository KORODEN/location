package com.koroden.app7

import android.os.Parcel
import android.os.Parcelable

class Coordinates() : Parcelable {
    var placeName: String? = ""
    var latitude: String? = ""
    var longitude: String? = ""
    var radius = 0
    var statusNumber = 2
    var id = 0

    constructor(parcel: Parcel) : this() {
        placeName = parcel.readString() ?: ""
        latitude = parcel.readString() ?: ""
        longitude = parcel.readString() ?: ""
        statusNumber = parcel.readInt()
        radius = parcel.readInt()
        id = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest !== null){
            dest.writeString(placeName)
            dest.writeString(latitude)
            dest.writeString(longitude)
            dest.writeInt(statusNumber)
            dest.writeInt(radius)
            dest.writeInt(id)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coordinates> {
        override fun createFromParcel(parcel: Parcel): Coordinates {
            return Coordinates(parcel)
        }

        override fun newArray(size: Int): Array<Coordinates?> {
            return arrayOfNulls(size)
        }
    }
}