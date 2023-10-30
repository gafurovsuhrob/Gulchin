package com.seros.gulchin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seros.gulchin.databinding.RowItemVerseBinding
import com.seros.gulchin.model.Verse

class VerseAdapter(private var verseList: List<Verse>, private val listener: VerseClickListener):
    RecyclerView.Adapter<VerseAdapter.ViewHolder>(){
    inner class ViewHolder(binding: RowItemVerseBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.verseTitle
        val number = binding.verseNumber
        val date = binding.verseDate

        fun bind(verse: Verse){
            title.text = verse.title
            number.text = verse.verseNumber
            date.text = verse.date
        }

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