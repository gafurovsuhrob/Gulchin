package com.seros.gulchin.fragments

import android.annotation.SuppressLint
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

@Suppress("DEPRECATION")
class MainFragment : Fragment(), VerseClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding  get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: VerseAdapter
    private var verseList: List<Verse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        recyclerView = binding.rvVerses
        setHasOptionsMenu(true)
        return binding.root
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dbHelper = MyDataBaseHelper(requireContext())
        val db = dbHelper.readableDatabase

        // Выполните запрос к базе данных для выборки всех данных
        val selectQuery = "SELECT * FROM Verses"
        val cursor = db.rawQuery(selectQuery, null)

        val verses = ArrayList<Verse>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("Id"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val verseText = cursor.getString(cursor.getColumnIndex("Verse_Text"))
                val date = cursor.getString(cursor.getColumnIndex("Date"))
                val verseNumber = cursor.getString(cursor.getColumnIndex("Verse_Number"))

                val verse = Verse(id, title, verseText, date, verseNumber)
                verses.add(verse)
            } while (cursor.moveToNext())
        }
        Log.d("TAG", "onViewCreated: $verses")

        cursor.close()

        // Инициализируйте адаптер и установите его в RecyclerView
        myAdapter = VerseAdapter(verses, this)
        recyclerView.adapter = myAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun clickVerse(verse: Verse) {
        Toast.makeText(requireContext(), verse.title, Toast.LENGTH_SHORT).show()
    }
}