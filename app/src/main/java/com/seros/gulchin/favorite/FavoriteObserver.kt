package com.seros.gulchin.favorite

import com.seros.gulchin.model.VerseItem

interface FavoriteObserver {
    fun onFavoriteChanged(verseItems: Set<VerseItem>)
}
