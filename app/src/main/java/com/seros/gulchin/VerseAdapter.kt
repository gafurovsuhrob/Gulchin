package com.seros.gulchin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seros.gulchin.databinding.RowItemVerseBinding
import com.seros.gulchin.favorite.Favorite
import com.seros.gulchin.model.VerseItem

class VerseAdapter(private var verseList: List<VerseItem>, private val listener: VerseClickListener):
    RecyclerView.Adapter<VerseAdapter.ViewHolder>(){
    inner class ViewHolder(binding: RowItemVerseBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.verseTitle
        val number = binding.verseNumber
        val date = binding.verseDate
        val addToFavoriteButton = binding.imageButton

        fun bind(verse: VerseItem){
            title.text = verse.Title
            number.text = verse.Verse_Namber
            date.text = verse.Date

            val isFavorite = Favorite.getVerseById(verse.Id)
            val favoriteIcon = if (isFavorite != null) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_empty
            addToFavoriteButton.setImageResource(favoriteIcon)

            addToFavoriteButton.setOnClickListener {
                listener.addToFavoritesClicked(verse)
            }
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