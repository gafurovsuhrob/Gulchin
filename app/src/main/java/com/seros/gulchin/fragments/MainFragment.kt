package com.seros.gulchin.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.seros.gulchin.R
import com.seros.gulchin.VerseAdapter
import com.seros.gulchin.VerseClickListener
import com.seros.gulchin.databinding.FragmentMainBinding
import com.seros.gulchin.favorite.Favorite
import com.seros.gulchin.favorite.FavoriteObserver
import com.seros.gulchin.model.VerseItem
import org.json.JSONArray
import java.io.IOException

@Suppress("DEPRECATION")
class MainFragment : Fragment(), VerseClickListener, FavoriteObserver {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var btnScrollToTop: FloatingActionButton

    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: VerseAdapter
    lateinit var favoriteObserver: FavoriteObserver

    var verseList: MutableList<VerseItem> = mutableListOf()
    var verseListFiltered: List<VerseItem> = emptyList()

    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var viewSearch: ConstraintLayout
    lateinit var ibCloseSearch: ImageButton
    var searchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        Favorite.addObserver(this)
        recyclerView = binding.rvVerses
        searchView = binding.searchView
        viewSearch = binding.searchLayout
        ibCloseSearch = binding.ibCloseSearch
        btnScrollToTop = binding.button
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchRealization()

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

                    verseList.add(VerseItem(id, title, verseNumber, date, verseText))
                }
                verseList.sortBy { it.Verse_Namber.toInt() }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myAdapter = VerseAdapter(verseList, this)
        recyclerView.adapter = myAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0

                if (firstVisibleItemPosition > 7) {
                    btnScrollToTop.visibility = View.VISIBLE
                } else {
                    btnScrollToTop.visibility = View.INVISIBLE
                }
            }
        })

        btnScrollToTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        savingAndFetchSearch()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_favorites -> {
                findNavController().navigate(R.id.action_mainFragment_to_favoriteVersesFragment)
                true
            }
            R.id.item_search -> {
                searchViewFun()
                true
            }
            R.id.item_info -> {
                findNavController().navigate(R.id.action_mainFragment_to_infoFragment)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun clickVerse(verse: VerseItem) {
        val action = MainFragmentDirections.actionMainFragmentToDataVerseFragment(verse)
        findNavController().navigate(action)
    }

    override fun addToFavoritesClicked(verse: VerseItem) {
        toggleFavoriteState(verse)
    }

    private fun toggleFavoriteState(verse: VerseItem) {
        val verseSearch = Favorite.getVerseById(verse.Id)
        val isCurrentlyFavorite = verseSearch != null

        if (isCurrentlyFavorite) {
            Favorite.removeProduct(verse.Id, requireContext())
        } else {
            Favorite.addFavorite(verse, requireContext())
        }

        val updatedVerseList = verseList.map {
            if (it.Id == verse.Id) {
                it.copy(isFavorite = isCurrentlyFavorite)
            } else {
                it
            }
        }
        myAdapter.updateList(updatedVerseList)
        Favorite.saveCartToPrefs(requireContext())
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

    override fun onStart() {
        super.onStart()
        Favorite.notifyObservers()
        Favorite.loadCartFromPrefs(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        Favorite.loadCartFromPrefs(requireContext())
        Favorite.removeObserver(favoriteObserver)
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Favorite.saveCartToPrefs(requireContext())
        Favorite.loadCartFromPrefs(requireContext())
    }

    override fun onFavoriteChanged(verseItems: Set<VerseItem>) {
        verseList.forEachIndexed { index, verse ->
            val isFavorite = verseItems.any { it.Id == verse.Id }
            verseList[index] = verse.copy(isFavorite = isFavorite)
        }
        myAdapter.notifyDataSetChanged()
    }


    override fun onStop() {
        super.onStop()
        Favorite.saveCartToPrefs(requireContext())
    }
}
