package com.example.afinal.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels  // Shares ViewModel across fragments
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.afinal.R
import com.example.afinal.databinding.FragmentHomeBinding  // ViewBinding — NO findViewById!
import com.example.afinal.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * HOME FRAGMENT — The main screen showing the list of notes
 *
 * Fragment is a reusable "mini screen" that lives inside an Activity.
 * The Navigation Component swaps Fragments in and out automatically.
 *
 * activityViewModels() — gets a ViewModel shared with the Activity and other Fragments.
 * This means all fragments share the same data without passing bundles.
 */
class HomeFragment : Fragment() {

    // ViewBinding reference — generated from fragment_home.xml
    // _binding is nullable (null when view is destroyed to prevent memory leaks)
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!  // Non-null accessor (safe to use between onCreateView and onDestroyView)

    // Shared ViewModel — same instance used across HomeFragment, AddFragment, DetailFragment
    private val viewModel: NoteViewModel by activityViewModels()

    // RecyclerView adapter
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding (NOT setContentView — that's for Activities)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupFab()
        setupMenu()
    }

    /**
     * Setup the RecyclerView with its adapter and layout manager.
     * LinearLayoutManager arranges items in a vertical scrollable list.
     */
    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                // Navigate to DetailFragment, passing the note's ID as a Safe Arg
                val action = HomeFragmentDirections.actionHomeToDetail(note.id)
                findNavController().navigate(action)
            },
            onNoteDelete = { note ->
                // Show confirmation dialog before deleting
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete \"${note.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.delete(note)
                        Snackbar.make(binding.root, "Note deleted", Snackbar.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerViewNotes.apply {
            adapter = noteAdapter
            // LinearLayoutManager = one item per row, scrolls vertically
            layoutManager = LinearLayoutManager(requireContext())
            // Optimization: tells RecyclerView that item sizes don't change
            setHasFixedSize(true)
        }
    }

    /**
     * Observe LiveData from the ViewModel.
     * viewLifecycleOwner is the Fragment's view lifecycle — safer than the Fragment itself.
     * The lambda runs whenever the LiveData value changes.
     */
    private fun observeViewModel() {
        // Observe the list of notes — RecyclerView updates automatically
        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)  // submitList triggers DiffUtil comparison

            // Show/hide the empty state message
            binding.textViewEmpty.visibility =
                if (notes.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe loading state — show/hide progress bar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors — show a Snackbar message
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()  // Reset so we don't show it again on rotation
            }
        }
    }

    /**
     * FloatingActionButton opens the Add Note screen.
     */
    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_add)
        }
    }

    /**
     * Add a menu to the Fragment using MenuProvider (modern API).
     * This inflates menu/menu_home.xml into the Toolbar.
     */
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sync -> {
                        // Sync button: fetch notes from the remote API
                        viewModel.syncFromRemote()
                        true
                    }
                    R.id.action_about -> {
                        // About dialog
                        AlertDialog.Builder(requireContext())
                            .setTitle("About")
                            .setMessage("Notes App — Final Exam Project\nMVVM Architecture with Room + Retrofit + Glide")
                            .setPositiveButton("OK", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * IMPORTANT: Set _binding to null when the view is destroyed.
     * This prevents memory leaks because the binding holds references to views.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
