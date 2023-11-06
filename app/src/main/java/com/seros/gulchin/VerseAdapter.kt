package com.seros.gulchin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seros.gulchin.databinding.RowItemVerseBinding
import com.seros.gulchin.model.VerseItem

class VerseAdapter(private var verseList: List<VerseItem>, private val listener: VerseClickListener):
    RecyclerView.Adapter<VerseAdapter.ViewHolder>(){
    inner class ViewHolder(binding: RowItemVerseBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.verseTitle
        val number = binding.verseNumber
        val date = binding.verseDate

        fun bind(verse: VerseItem){
            title.text = verse.Title
            number.text = verse.Verse_Namber
            date.text = verse.Date
        }
    }

    fun updateList(newVerses: List<VerseItem>){
        verseList = newVerses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseAdapter.ViewHolder {
        val binding = RowItemVerseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerseAdapter.ViewHolder, position: Int) {
        val verse = verseList[position]
        holder.bind(verse)

        holder.itemView.setOnClickListener {
            listener.clickVerse(verse)
        }
    }

    override fun getItemCount(): Int {
        return verseList.size
    }
}