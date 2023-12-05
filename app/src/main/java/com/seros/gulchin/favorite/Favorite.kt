package com.seros.gulchin.favorite

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.seros.gulchin.model.VerseItem

object Favorite {
    private val PREFS_NAME = "my_favorite_prefs"
    private const val CART_KEY = "favorite"


    private val verses: MutableSet<VerseItem> = mutableSetOf()
    private val observers: MutableSet<FavoriteObserver> = mutableSetOf()
    private val clearObservers: MutableSet<FavoriteObserver> = mutableSetOf()


    fun addObserver(observer: FavoriteObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: FavoriteObserver) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        for (observer in observers) {
            observer.onFavoriteChanged(verses)
        }
    }

    fun getAllFavoriteItems(): MutableList<VerseItem> {
        return verses.toMutableList()
    }

    fun getVerseById(productId: Int): VerseItem? {
        return verses.find { it.Id == productId }
    }

    fun addFavorite(verse: VerseItem, context: Context) {
        try {
            val existingProduct = getVerseById(verse.Id)

            if (existingProduct == null) {
                verses.add(verse)
                Log.d("Favorites", "Verse added to favorites: ${verse.Id}")
            } else {
                saveCartToPrefs(context)
                Log.d("Favorites", "Verse already exists in favorites: ${verse.Id}")
            }
            notifyObservers()
        } catch (e: Exception){
            Log.d("TAG", "addProduct: ${e.message}")
        }
    }



    fun removeProduct(productId: Int, context: Context) {
        val productToRemove = getVerseById(productId)
        if (productToRemove != null) {
            verses.remove(productToRemove)
            saveCartToPrefs(context)
            notifyObservers()
        }
    }
    fun clearAllItems(context: Context) {
        verses.clear()
        saveCartToPrefs(context)
        notifyObservers()
    }

    fun loadCartFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cartJson = prefs.getStringSet(CART_KEY, null)
        if (cartJson != null) {
            verses.clear()
            verses.addAll(cartJson.map { Gson().fromJson(it, VerseItem::class.java) })
            Log.d("Favorites", "Loaded favorites from prefs: $verses")
        }
    }

    fun saveCartToPrefs(context: Context) {
        val cartJson = verses.map { Gson().toJson(it) }.toSet()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(CART_KEY, cartJson).apply()
        Log.d("Favorites", "Saved favorites to prefs: $verses")
    }

    fun getUniqueProductTypesCount(): Int {
        return verses.size
    }

}
