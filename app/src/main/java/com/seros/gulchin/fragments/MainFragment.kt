package com.seros.gulchin.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seros.gulchin.MyDataBaseHelper
import com.seros.gulchin.R
import com.seros.gulchin.VerseAdapter
import com.seros.gulchin.VerseClickListener
import com.seros.gulchin.databinding.FragmentMainBinding
import com.seros.gulchin.model.Verse
import com.seros.gulchin.model.VerseItem
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

@Suppress("DEPRECATION")
class MainFragment : Fragment(), VerseClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding  get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: VerseAdapter
    private var verseList: List<VerseItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        recyclerView = binding.rvVerses
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val json = loadJsonData(requireContext())
        if (json.isNotEmpty()) {
            val jsonArray = JSONArray(json)
            val verses = ArrayList<VerseItem>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("Id")
                val title = jsonObject.getString("Title")
                val verseText = jsonObject.getString("Verse_Text")
                val date = jsonObject.getString("Date")
                val verseNumber = jsonObject.getString("Verse_Namber")

                verses.add(VerseItem(id, title, verseNumber, date, verseText ))
            }

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            myAdapter = VerseAdapter(verses, this)
            recyclerView.adapter = myAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun clickVerse(verse: VerseItem) {
        Toast.makeText(requireContext(), verse.Title, Toast.LENGTH_SHORT).show()
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

}