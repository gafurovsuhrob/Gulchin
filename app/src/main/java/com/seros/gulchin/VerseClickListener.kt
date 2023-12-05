package com.seros.gulchin

import com.seros.gulchin.model.Verse
import com.seros.gulchin.model.VerseItem

interface VerseClickListener {
    fun clickVerse(verse: VerseItem)
    fun addToFavoritesClicked(verse: VerseItem)

}
