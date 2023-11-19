package com.seros.gulchin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class VerseItem(
    val Id: Int,
    val Title: String,
    val Verse_Namber: String,
    val Date: String,
    val Verse_Text: String,
    var isFavorite: Boolean = false
) : Parcelable, Serializable