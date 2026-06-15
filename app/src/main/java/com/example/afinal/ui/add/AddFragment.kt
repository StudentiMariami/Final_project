package com.example.afinal.ui.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.afinal.databinding.FragmentAddBinding  // ViewBinding — NO findViewById!
import com.example.afinal.model.Note
import com.example.afinal.viewmodel.NoteViewModel

/**
 * ADD FRAGMENT — Screen for creating a new note
 *
 * Uses the same shared NoteViewModel as HomeFragment.
 * After saving, navigates back to the home list using the NavController.
 */
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    // activityViewModels() — same ViewModel instance as HomeFragment
    private val viewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Save button click
        binding.buttonSave.setOnClickListener {
            saveNote()
        }

        // Cancel button — go back without saving
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Reads input from the form, validates it, creates a Note, and saves it.
     * Uses ViewBinding to access EditText views — NO findViewById!
     */
    private fun saveNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        val imageUrl = binding.editTextImageUrl.text.toString().trim()

        // Validate that required fields are not empty
        if (title.isEmpty()) {
            binding.editTextTitle.error = "Title is required"
            return
        }
        if (content.isEmpty()) {
            binding.editTextContent.error = "Content is required"
            return
        }

        // Create a Note object (id=0 means Room will auto-generate the ID)
        val newNote = Note(
            title = title,
            content = content,
            imageUrl = imageUrl  // Optional image URL for Glide to load
        )

        // Tell the ViewModel to insert the note (ViewModel calls Repository → Room)
        viewModel.insert(newNote)

        Toast.makeText(requireContext(), "Note saved!", Toast.LENGTH_SHORT).show()

        // Navigate back to the home list
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Prevent memory leak
    }
}
