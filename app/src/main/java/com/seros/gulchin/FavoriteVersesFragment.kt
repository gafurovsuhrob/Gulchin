package com.seros.gulchin

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.seros.gulchin.databinding.FragmentFavoriteVersesBinding
import com.seros.gulchin.favorite.Favorite
import com.seros.gulchin.favorite.FavoriteObserver
import com.seros.gulchin.fragments.MainFragment
import com.seros.gulchin.fragments.MainFragmentDirections
import com.seros.gulchin.fragments.filterSearchResults
import com.seros.gulchin.fragments.searchBarChecked
import com.seros.gulchin.model.VerseItem

class FavoriteVersesFragment : Fragment(), VerseClickListener, FavoriteObserver  {

    private var _binding: FragmentFavoriteVersesBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: VerseAdapter
    private var favoriteList: MutableList<VerseItem> = mutableListOf()
    var verseListFiltered: List<VerseItem> = emptyList()


    lateinit var btnScrollToTop: FloatingActionButton

    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var viewSearch: ConstraintLayout
    lateinit var ibCloseSearch: ImageButton
    var searchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteVersesBinding.inflate(inflater, container, false)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        favoriteList = Favorite.getAllFavoriteItems()
        searchView = binding.searchView
        viewSearch = binding.searchLayout
        ibCloseSearch = binding.ibCloseSearch
        btnScrollToTop = binding.button
        setHasOptionsMenu(true)
        Favorite.loadCartFromPrefs(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchRealization()

        recyclerView = binding.rvVerses
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myAdapter = VerseAdapter(favoriteList, this)
        recyclerView.adapter = myAdapter

        val emptyList = favoriteList.isEmpty()

        if (emptyList){
            recyclerView.visibility = GONE
            binding.textView2.visibility = VISIBLE
        } else {
            recyclerView.visibility = VISIBLE
            binding.textView2.visibility = GONE
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0

                if (firstVisibleItemPosition > 7) {
                    btnScrollToTop.visibility = VISIBLE
                } else {
                    btnScrollToTop.visibility = INVISIBLE
                }
            }
        })

        btnScrollToTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu_2, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val emptyList = favoriteList.isEmpty()

        return when (item.itemId) {
            R.id.item_search -> {
                if (emptyList){
                    Toast.makeText(requireContext(), "Ҷустуҷӯ бо рӯйхати холӣ гузаронида намешавад.", Toast.LENGTH_SHORT).show()
                } else {
                    searchViewFun()
                }
                true
            }
            R.id.clear_favorite -> {
                if (emptyList){
                    Toast.makeText(requireContext(), "Тозо кардан бо рӯйхати холӣ гузаронида намешавад.", Toast.LENGTH_SHORT).show()
                } else {
                    callWarningDialog()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun searchViewFun() {
        filterSearchResults()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = VISIBLE
        } else {
            viewSearch.visibility = GONE
        }
    }

    fun callWarningDialog() {
        val builder = MaterialAlertDialogBuilder(requireActivity())
            .setMessage("Оё шумо мехоҳед дӯстдоштаҳои худро тоза кунед?")
            .setPositiveButton("Хуб") { dialog: DialogInterface, _: Int ->
                Favorite.clearAllItems(requireContext())
                Favorite.notifyObservers()
                myAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Не", null)

        val dialog = builder.create()

        dialog.setOnShowListener { dialogInterface ->
            val okButton = (dialogInterface as Dialog).findViewById(android.R.id.button1) as? Button
            okButton?.setTextColor(Color.BLACK)
        }
        dialog.show()
    }


    override fun onStart() {
        super.onStart()
        Favorite.notifyObservers()
        Favorite.loadCartFromPrefs(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Favorite.loadCartFromPrefs(requireContext())
    }

    override fun clickVerse(verse: VerseItem) {
        val action = FavoriteVersesFragmentDirections.actionFavoriteVersesFragmentToDataVerseFragment(verse)
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

        val updatedVerseList = favoriteList.map {
            if (it.Id == verse.Id) {
                it.copy(isFavorite = isCurrentlyFavorite)
            } else {
                it
            }
        }

        myAdapter.updateList(updatedVerseList)
        Favorite.saveCartToPrefs(requireContext())
    }

    override fun onFavoriteChanged(verseItems: Set<VerseItem>) {
        val filteredList = favoriteList.filter { it in verseItems }
        myAdapter.updateList(filteredList)
    }

    fun savingAndFetchSearch() {
        try {
            if (searchText.isNotEmpty()) {
                viewSearch.visibility = VISIBLE

                ibCloseSearch.setOnClickListener {
                    if (searchText != "") {
                        searchView.setQuery("", false)
                    }
                    viewSearch.visibility = GONE
                }
                filterSearchResults()
            }
        } catch (e: Exception) {
            Log.d("TAG", "savingAndFetchSearch: ${e.message}")
        }
    }

    fun searchRealization() {
        ibCloseSearch.setOnClickListener {
            if (searchText != "") {
                searchView.setQuery("", false)
            }
            viewSearch.visibility = GONE
        }

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText.toString()
                filterSearchResults()
                return true
            }
        })
    }

    fun filterSearchResults() {
        verseListFiltered = if (searchText.isNotEmpty()) {
            favoriteList.filter { verse ->
                verse.Verse_Namber.contains(searchText, ignoreCase = true) ||
                        verse.Verse_Text.contains(searchText, ignoreCase = true)
            }
        } else {
            favoriteList
        }
        myAdapter.updateList(verseListFiltered)
    }
}

