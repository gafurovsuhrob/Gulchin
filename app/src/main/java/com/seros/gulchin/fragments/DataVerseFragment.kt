package com.seros.gulchin.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.seros.gulchin.R
import com.seros.gulchin.databinding.FragmentDataVerseBinding
import com.seros.gulchin.favorite.Favorite
import com.seros.gulchin.favorite.FavoriteObserver
import com.seros.gulchin.model.VerseItem
import org.json.JSONArray
import java.io.IOException

@Suppress("DEPRECATION")
class DataVerseFragment : Fragment(), FavoriteObserver {

    private var _binding: FragmentDataVerseBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar
    val args by navArgs<DataVerseFragmentArgs>()

    var verseList: MutableList<VerseItem> = mutableListOf()

    var currentVerseId = 1
    private val maxVerseId = 708

    lateinit var favoriteObserver: FavoriteObserver

    private var currentIndex: Int = -1
    private var currentVerseNumber: String = ""

    private lateinit var sharedPreferences: SharedPreferences
    private val FAVORITES_KEY = "favorites"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataVerseBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        Favorite.loadCartFromPrefs(requireContext())
        favoriteObserver = this
        Favorite.addObserver(favoriteObserver)

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        currentVerseId = args.verse.Id
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (verseList.isEmpty()) {
            val json = loadJsonData(requireContext())
            if (json.isNotEmpty()) {
                val jsonArray = JSONArray(json)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getInt("Id")
                    val title = jsonObject.getString("Title")
                    val verseText = jsonObject.getString("Verse_Text")
                    val date = jsonObject.getString("Date")
                    val verseNumber = jsonObject.getString("Verse_Namber")

                    val verseItem = VerseItem(id, title, verseNumber, date, verseText)
                    verseList.add(verseItem)
                }
                verseList.sortBy { it.Verse_Namber.toInt() }
            }
        }

        currentIndex = verseList.indexOfFirst { it.Verse_Namber == currentVerseNumber }
        currentVerseNumber = args.verse.Verse_Namber
        setData(args.verse)

        binding.btnNextVerse.setOnClickListener {
            if (currentIndex < verseList.size - 1) {
                currentIndex++
                setData(verseList[currentIndex])
            }
        }

        binding.btnPreviousVerse.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                setData(verseList[currentIndex])
            }
        }

        binding.btnFavorite.setOnClickListener {
            val currentVerse = verseList.getOrNull(currentIndex)
            if (currentVerse != null) {
                toggleFavoriteState(currentVerse)
            } else {
                Log.e("Favorites", "Current verse is null")
            }
        }
        return binding.root
    }



    @SuppressLint("SetTextI18n")
    private fun setData(verse: VerseItem) {
        binding.apply {
            tvDateVerse.text = verse.Date
            tvNumberVerse.text = "№${verse.Verse_Namber}"
            hello.text = verse.Verse_Text

            val isTablet = resources.getBoolean(R.bool.isTablet)
            Log.d("TAG", "setData: $isTablet")

            if (isTablet) {
                val largeTextSize = 80f
                binding.hello.setTextSize(TypedValue.COMPLEX_UNIT_PX, largeTextSize)
            }

            toolbar.title = capitalizeFirstLetter(verse.Title)

            val isFavorite = Favorite.getAllFavoriteItems().contains(verse)
            val favoriteIcon = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_empty
            btnFavorite.setImageResource(favoriteIcon)

            btnFavorite.setOnClickListener {
                toggleFavoriteState(verse)
            }
        }

        currentIndex = verseList.indexOf(verse)
        currentVerseNumber = verse.Verse_Namber
    }

    private fun toggleFavoriteState(verse: VerseItem) {
        if (Favorite.getAllFavoriteItems().contains(verse)) {
            Favorite.removeProduct(verse.Id, requireContext())
        } else {
            Favorite.addFavorite(verse, requireContext())
        }
        updateUIWithFavoriteItems(Favorite.getAllFavoriteItems().toSet())
        Favorite.saveCartToPrefs(requireContext())
        Log.d("Favorites", "Toggle state for verse ${verse.Id}")
    }

    private fun loadJsonData(context: Context): String {
        var json: String? = null
        try {
            val inputStream = context.assets.open("verses.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json ?: ""
    }

    override fun onFavoriteChanged(verseItems: Set<VerseItem>) {
        updateUIWithFavoriteItems(verseItems)
    }

    private fun updateUIWithFavoriteItems(verseItems: Set<VerseItem>) {
        val currentVerse = verseList.getOrNull(currentIndex)
        currentVerse?.let {
            val isFavorite = verseItems.contains(it)
            val favoriteIcon = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_empty
            binding.btnFavorite.setImageResource(favoriteIcon)
        }
    }

    override fun onStart() {
        super.onStart()
        Favorite.notifyObservers()
        Favorite.loadCartFromPrefs(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Favorite.removeObserver(favoriteObserver)
        Favorite.loadCartFromPrefs(requireContext())
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Favorite.loadCartFromPrefs(requireContext())
    }
}