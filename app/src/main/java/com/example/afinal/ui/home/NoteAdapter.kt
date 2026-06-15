package com.example.afinal.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide                   // NEW FEATURE: image loading library
import com.example.afinal.R
import com.example.afinal.databinding.ItemNoteBinding  // ViewBinding — NO findViewById!
import com.example.afinal.model.Note
import java.text.SimpleDateFormat
import java.util.*

/**
 * NOTE ADAPTER — RecyclerView Adapter for the list of notes
 *
 * ListAdapter<Note, ViewHolder>(NoteDiffCallback())
 *   - ListAdapter is smarter than RecyclerView.Adapter: it uses DiffUtil to only
 *     redraw items that ACTUALLY changed (better performance, smooth animations).
 *   - NoteDiffCallback tells DiffUtil how to compare two Note objects.
 *
 * Constructor takes two lambdas (callback functions):
 *   - onNoteClick: called when the user taps a note (navigate to detail)
 *   - onNoteDelete: called when the user long-presses to delete
 */
class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteDelete: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    /**
     * ViewHolder holds references to the views for ONE list item.
     * Using ViewBinding: ItemNoteBinding is generated from item_note.xml
     * — NO NEED for findViewById anywhere!
     */
    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * bind() fills the item layout with data from a Note object.
         */
        fun bind(note: Note) {
            // Set the title and content text using ViewBinding references
            binding.textViewTitle.text = note.title
            binding.textViewContent.text = note.content

            // Format the timestamp (milliseconds → readable date string)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.textViewDate.text = dateFormat.format(Date(note.timestamp))

            /**
             * GLIDE — NEW FEATURE (not used in previous lectures)
             *
             * Glide.with(context).load(url).into(imageView)
             * Glide handles:
             *   - Downloading the image from URL on a background thread
             *   - Caching it to disk/memory
             *   - Displaying it in the ImageView
             *   - Showing a placeholder while loading
             */
            if (note.imageUrl.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(note.imageUrl)                    // URL to load
                    .placeholder(R.drawable.ic_image_placeholder) // shown while loading
                    .error(R.drawable.ic_image_error)       // shown if load fails
                    .centerCrop()                           // scale the image to fill the view
                    .into(binding.imageViewNote)            // target ImageView
            }

            // Click listener: navigate to detail screen
            binding.root.setOnClickListener { onNoteClick(note) }

            // Long press listener: delete the note
            binding.root.setOnLongClickListener {
                onNoteDelete(note)
                true // consume the event (don't trigger normal click)
            }
        }
    }

    /**
     * onCreateViewHolder() is called when RecyclerView needs a NEW ViewHolder.
     * We inflate the item layout using ViewBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), // Use the context of the parent (the list)
            parent,
            false  // Don't attach immediately — RecyclerView does that
        )
        return NoteViewHolder(binding)
    }

    /**
     * onBindViewHolder() is called to fill an existing ViewHolder with data.
     * getItem(position) is provided by ListAdapter.
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * DiffUtil.ItemCallback tells ListAdapter how to detect changes.
 *
 * areItemsTheSame: are two items the SAME item? (compare by unique ID)
 * areContentsTheSame: does the item's DATA look the same? (compare all fields)
 *
 * If only contents changed, RecyclerView animates the update instead of redrawing everything.
 */
class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id  // Same database row = same item
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem  // Kotlin data class == compares all fields
    }
}
