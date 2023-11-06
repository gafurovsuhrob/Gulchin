package com.seros.gulchin.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.seros.gulchin.R
import com.seros.gulchin.databinding.FragmentDataVerseBinding
import com.seros.gulchin.model.VerseItem
import org.json.JSONArray
import java.io.IOException

@Suppress("DEPRECATION")
class DataVerseFragment : Fragment() {

    private var _binding: FragmentDataVerseBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar
    val args by navArgs<DataVerseFragmentArgs>()

    var verseList: MutableList<VerseItem> = mutableListOf()


    var currentVerseId = 1
    private val maxVerseId = 708

    private var currentIndex: Int = -1
    private var currentVerseNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataVerseBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        currentVerseId = args.verse.Id

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
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun setData(verse: VerseItem) {
        binding.apply {
            tvDateVerse.text = verse.Date
            tvNumberVerse.text = "№${verse.Verse_Namber}"
            hello.text = verse.Verse_Text
            toolbar.title = capitalizeFirstLetter(verse.Title)
        }

        currentIndex = verseList.indexOf(verse)
        currentVerseNumber = verse.Verse_Namber
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}