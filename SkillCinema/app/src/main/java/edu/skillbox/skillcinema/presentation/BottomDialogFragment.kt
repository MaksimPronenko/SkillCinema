package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.CollectionFilmAdapter
import edu.skillbox.skillcinema.databinding.BottomDialogBinding
import edu.skillbox.skillcinema.models.CollectionFilm
import javax.inject.Inject

private const val TAG = "BottomDialog.Fragment"

private const val ARG_FILM_ID = "filmId"
private const val ARG_POSTER_SMALL = "posterSmall"
private const val ARG_NAME = "name"
private const val ARG_YEAR = "year"
private const val ARG_GENRES = "genres"
private const val ARG_NEW_COLLECTION_NAME = "newCollectionName"

@AndroidEntryPoint
class BottomDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var bottomDialogViewModelFactory: BottomDialogViewModelFactory
    private val viewModel: BottomDialogViewModel by viewModels { bottomDialogViewModelFactory }

    private var _binding: BottomDialogBinding? = null
    private val binding get() = _binding!!

    private val collectionAdapter =  CollectionFilmAdapter { collection -> onCollectionItemClick(collection) }

    override fun getTheme() = R.style.CollectionBottomDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        viewModel.posterSmall = arguments?.getString(ARG_POSTER_SMALL) ?: ""
        viewModel.name = arguments?.getString(ARG_NAME) ?: ""
        viewModel.year = arguments?.getString(ARG_YEAR) ?: ""
        viewModel.genres = arguments?.getString(ARG_GENRES) ?: ""
        viewModel.newCollectionName = arguments?.getString(ARG_NEW_COLLECTION_NAME) ?: ""
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
        binding.filmInfo.text = "${viewModel.year}, ${viewModel.genres}"
        binding.collectionRecycler.adapter = collectionAdapter

        Glide
            .with(binding.poster.context)
            .load(viewModel.posterSmall)
            .into(binding.poster)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.collectionChannel.collect { collectionList ->
                    collectionAdapter.setData(collectionList)
                    Log.d(TAG, "Новый список коллекций: $collectionList")
                    Log.d(TAG, "Recycler: ${binding.collectionRecycler.layoutManager?.childCount}")
                }
            }

//        viewLifecycleOwner.lifecycleScope
//            .launchWhenStarted {
//                viewModel.state
//                    .collect { state ->
//                        when (state) {
//                            ViewModelState.Loading -> {
//                                Log.d(TAG, "Состояние загрузки")
//                            }
//                            ViewModelState.Loaded -> {
//                                Log.d(TAG, "Состояние рабочее")
//                            }
//                            ViewModelState.Error -> {
//                                Log.d(TAG, "Состояние ошибки")
////                                findNavController().navigate(R.id.action_BottomDialogFragment_to_ErrorBottomFragment)
//                            }
//                        }
//                    }
//            }

        binding.filmInfo.setOnClickListener {
            viewModel.loadCollectionData()
        }

        binding.addToCollectionButton.setOnClickListener {
            findNavController().navigate(R.id.action_BottomDialogFragment_to_CollectionNameDialogFragment)
        }

        val currentFragment = findNavController().getBackStackEntry(R.id.BottomDialogFragment)
        val dialogObserver = LifecycleEventObserver{ _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentFragment.savedStateHandle.contains("key")) {
//                binding.addToCollection.text = currentFragment.savedStateHandle.get("key")
                val newCollectionName = currentFragment.savedStateHandle.get<String>("key") ?: ""
                viewModel.createNewCollection(collectionName = newCollectionName)
            }
        }
        val dialogLifecycle = currentFragment.lifecycle
        dialogLifecycle.addObserver(dialogObserver)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver{ _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                dialogLifecycle.removeObserver(dialogObserver)
            }
        })

        binding.closeButton.setOnClickListener {
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