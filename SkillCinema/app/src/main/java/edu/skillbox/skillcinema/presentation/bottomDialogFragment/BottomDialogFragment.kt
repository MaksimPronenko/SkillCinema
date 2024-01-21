package edu.skillbox.skillcinema.presentation.bottomDialogFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.presentation.adapters.CollectionFilmAdapter
import edu.skillbox.skillcinema.databinding.BottomDialogBinding
import edu.skillbox.skillcinema.models.collection.CollectionFilm
import edu.skillbox.skillcinema.utils.*
import javax.inject.Inject

private const val TAG = "BottomDialog.Fragment"

@AndroidEntryPoint
class BottomDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var bottomDialogViewModelFactory: BottomDialogViewModelFactory
    private val viewModel: BottomDialogViewModel by viewModels { bottomDialogViewModelFactory }

    private var _binding: BottomDialogBinding? = null
    private val binding get() = _binding!!

    private val collectionAdapter = CollectionFilmAdapter(
        onCollectionItemClick =
        { collection -> onCollectionItemClick(collection) },
        onAddCollectionItemClick =
        { findNavController().navigate(R.id.action_BottomDialogFragment_to_CollectionNameDialogFragment) }
    )

    override fun getTheme() = R.style.CollectionBottomDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        viewModel.posterSmall = arguments?.getString(ARG_POSTER_SMALL) ?: ""
        viewModel.name = arguments?.getString(ARG_NAME) ?: ""
        viewModel.year = arguments?.getString(ARG_YEAR) ?: ""
        viewModel.genres = arguments?.getString(ARG_GENRES) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomDialogBinding.bind(inflater.inflate(R.layout.bottom_dialog, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.filmName.text = viewModel.name
        val filmInfoText = "${viewModel.year}, ${viewModel.genres}"
        binding.filmInfo.text = filmInfoText
        binding.collectionRecycler.adapter = collectionAdapter

        Glide
            .with(binding.poster.context)
            .load(viewModel.posterSmall)
            .into(binding.poster)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.collectionChannel.collect { collectionList ->
                    collectionAdapter.setAdapterData(collectionList)
                    Log.d(TAG, "Новый список коллекций: $collectionList")
                    Log.d(TAG, "Recycler: ${binding.collectionRecycler.layoutManager?.childCount}")
                }
            }

        binding.filmInfo.setOnClickListener {
            viewModel.loadCollectionData()
        }

        val currentFragment = findNavController().getBackStackEntry(R.id.BottomDialogFragment)
        val dialogObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentFragment.savedStateHandle.contains("newCollectionName")) {
                val newCollectionName =
                    currentFragment.savedStateHandle.get<String>("newCollectionName") ?: ""
                viewModel.createNewCollection(collectionName = newCollectionName)
            }
        }
        val dialogLifecycle = currentFragment.lifecycle
        dialogLifecycle.addObserver(dialogObserver)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                dialogLifecycle.removeObserver(dialogObserver)
            }
        })

        binding.closeButton.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                COLLECTION_FAVORITE,
                viewModel.favorite
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                COLLECTION_WANTED_TO_WATCH,
                viewModel.wantedToWatch
            )
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun onCollectionItemClick(
        item: CollectionFilm
    ) {
        viewModel.onCollectionItemClick(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}