package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.CollectionAdapter
import edu.skillbox.skillcinema.data.ViewedAdapter
import edu.skillbox.skillcinema.databinding.FragmentProfileBinding
import edu.skillbox.skillcinema.models.CollectionInfo
import edu.skillbox.skillcinema.models.FilmTable
import javax.inject.Inject

private const val TAG = "Profile.Fragment"

private const val ARG_NEW_COLLECTION_NAME = "newCollectionName"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory
    private val viewModel: ProfileViewModel by viewModels { profileViewModelFactory }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewedAdapter =
        ViewedAdapter(limited = true) { filmTable -> onViewedItemClick(filmTable) }
    private val collectionAdapter =
        CollectionAdapter(onOpenCollection = { collection -> onOpenCollection(collection) },
            onDeleteCollection = { collection -> onDeleteCollection(collection) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.newCollectionName = arguments?.getString(ARG_NEW_COLLECTION_NAME) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
//        _binding = FragmentProfileBinding.bind(inflater.inflate(R.layout.fragment_profile, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewedRecycler.adapter = viewedAdapter
        binding.collectionsRecycler.adapter = collectionAdapter

        val dividerItemDecorationVertical = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerItemDecorationHorizontal =
            DividerItemDecoration(context, RecyclerView.HORIZONTAL)
        context?.let { ContextCompat.getDrawable(it, R.drawable.collection_divider) }
            ?.let {
                dividerItemDecorationVertical.setDrawable(it)
                dividerItemDecorationHorizontal.setDrawable(it)
            }
        binding.collectionsRecycler.addItemDecoration(dividerItemDecorationVertical)
        binding.collectionsRecycler.addItemDecoration(dividerItemDecorationHorizontal)

        binding.buttonCreateCollection.setOnClickListener {
            findNavController().navigate(R.id.action_ProfileFragment_to_CollectionNameDialogFragment)
//            viewModel.createNewCollection("Коллекция #${(0..1000).random()}")
        }

        val currentFragment = findNavController().getBackStackEntry(R.id.ProfileFragment)
        val dialogObserver = LifecycleEventObserver{ _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentFragment.savedStateHandle.contains("key")) {
//                binding.viewedTitle.text = currentFragment.savedStateHandle.get("key")
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

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ProfileFragment_to_MainFragment)
        }

        binding.searchButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_ProfileFragment_to_SearchFragment
            )
        }

//        binding.buttonAllViewed.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_ProfileFragment_to_AllViewedFragment
//            )
//        }
//
//        binding.buttonAllInterested.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_ProfileFragment_to_AllInterestedFragment
//            )
//        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.collectionChannel.collect { collectionList ->
                    collectionAdapter.setData(collectionList)
                    Log.d(TAG, "Новый список коллекций: ${viewModel.collectionInfoList}")
                }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                                binding.scrollView.isGone = true
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                binding.scrollView.isGone = false
                            }
                            ViewModelState.Error -> {
                                binding.scrollView.isGone = true
                                binding.progress.isGone = true
                                findNavController().navigate(R.id.action_FilmFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onViewedItemClick(
        item: FilmTable
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_ProfileFragment_to_FilmFragment,
            bundle
        )
    }

    private fun onOpenCollection(
        item: CollectionInfo
    ) {
        val bundle =
            Bundle().apply {
                putString(
                    "collection",
                    item.collectionName
                )
            }
        findNavController().navigate(
            R.id.action_ProfileFragment_to_CollectionFragment,
            bundle
        )
    }

    private fun onDeleteCollection(
        collection: CollectionInfo
    ) {
        viewModel.deleteCollection(collection.collectionName)
    }
}