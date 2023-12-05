package com.seros.gulchin.fragments

import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.constraintlayout.widget.ConstraintLayout

fun MainFragment.filterSearchResults() {
    verseListFiltered = if (searchText.isNotEmpty()) {
        verseList.filter { verse ->
            verse.Verse_Namber.contains(searchText, ignoreCase = true) ||
            verse.Verse_Text.contains(searchText, ignoreCase = true)
        }
    } else {
        verseList
    }
    myAdapter.updateList(verseListFiltered)
}

fun MainFragment.savingAndFetchSearch() {
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

fun MainFragment.searchRealization() {
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

fun searchBarChecked(view: ConstraintLayout): Boolean {
    return view.visibility != VISIBLE
}

fun capitalizeFirstLetter(input: String): String {
    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase()
}

fun MainFragment.searchViewFun() {
    filterSearchResults()
    if (searchBarChecked(viewSearch)) {
        viewSearch.visibility = VISIBLE
    } else {
        viewSearch.visibility = GONE
    }
}