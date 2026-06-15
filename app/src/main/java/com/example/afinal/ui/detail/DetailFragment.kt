package com.example.afinal.ui.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs  // Safe Args — type-safe navigation arguments
import com.bumptech.glide.Glide             // NEW FEATURE: Glide image loading
import com.example.afinal.R
import com.example.afinal.databinding.FragmentDetailBinding
import com.example.afinal.viewmodel.NoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * DETAIL FRAGMENT — Shows the full content of a single note
 *
 * navArgs() — retrieves arguments passed via Safe Args (type-safe navigation).
 * The note ID is passed from HomeFragment when the user taps a list item.
 */
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by activityViewModels()

    // navArgs() reads the Safe Args defined in nav_graph.xml
    // This gives us the noteId that was passed from HomeFragment
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNote()
    }

    /**
     * Fetches the note from Room by ID and populates the UI.
     * lifecycleScope.launch(Dispatchers.IO) runs on a background thread.
     * withContext(Dispatchers.Main) switches back to the UI thread to update views.
     */
    private fun loadNote() {
        lifecycleScope.launch(Dispatchers.IO) {
            val note = viewModel.allNotes.value?.find { it.id == args.noteId }

            withContext(Dispatchers.Main) {
                note?.let {
                    // Populate the UI with the note's data using ViewBinding
                    binding.textViewDetailTitle.text = it.title
                    binding.textViewDetailContent.text = it.content

                    // Format the timestamp into a readable date+time string
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                    binding.textViewDetailDate.text = "Created: ${dateFormat.format(Date(it.timestamp))}"

                    /**
                     * GLIDE IMAGE LOADING — THE NEW FEATURE
                     *
                     * If the note has an imageUrl, use Glide to load it into the ImageView.
                     * Glide handles: downloading, caching, threading, resizing.
                     *
                     * .thumbnail(0.1f) — loads a low-res version first (smooth UX)
                     * .placeholder() — drawable shown while image loads
                     * .error() — drawable shown if loading fails
                     */
                    if (it.imageUrl.isNotEmpty()) {
                        binding.imageViewDetail.visibility = View.VISIBLE
                        Glide.with(this@DetailFragment)
                            .load(it.imageUrl)
                            .thumbnail(0.1f)  // Show 10% quality preview first
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_error)
                            .centerCrop()
                            .into(binding.imageViewDetail)
                    } else {
                        binding.imageViewDetail.visibility = View.GONE
                    }

                    // Delete button
                    binding.buttonDelete.setOnClickListener { _ ->
                        viewModel.delete(it)
                        findNavController().navigateUp()  // Go back to list
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
