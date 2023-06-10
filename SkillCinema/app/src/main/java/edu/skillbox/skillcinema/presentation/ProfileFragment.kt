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
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.CollectionAdapter
import edu.skillbox.skillcinema.data.InterestedAdapter
import edu.skillbox.skillcinema.data.ViewedAdapter
import edu.skillbox.skillcinema.databinding.FragmentProfileBinding
import edu.skillbox.skillcinema.models.CollectionInfo
import edu.skillbox.skillcinema.models.FilmDbViewed
import edu.skillbox.skillcinema.models.PersonTable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Profile.Fragment"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory
    private val viewModel: ProfileViewModel by viewModels { profileViewModelFactory }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewedAdapter =
        ViewedAdapter(limited = true) { filmDbViewed -> onViewedItemClick(filmDbViewed) }
    private val collectionAdapter = CollectionAdapter(
        onOpenCollection = { collection -> onOpenCollection(collection) },
        onDeleteCollection = { collection -> onDeleteCollection(collection) }
    )
    private val interestedAdapter = InterestedAdapter(
        onFilmClick = { filmDbViewed -> onFilmClick(filmDbViewed) },
        onSerialClick = { filmDbViewed -> onSerialClick(filmDbViewed) },
        onPersonClick = { personTable -> onPersonClick(personTable) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewedRecycler.adapter = viewedAdapter
        binding.collectionsRecycler.adapter = collectionAdapter
        binding.interestedRecycler.adapter = interestedAdapter

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
        }

        val currentFragment = findNavController().getBackStackEntry(R.id.ProfileFragment)
        val dialogObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentFragment.savedStateHandle.contains("newCollectionName")) {
//                binding.viewedTitle.text = currentFragment.savedStateHandle.get("key")
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

        binding.buttonAllViewed.setOnClickListener {
            findNavController().navigate(
                R.id.action_ProfileFragment_to_CollectionFragment
            )
        }

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
                            else -> {
                                binding.progress.isGone = true
                                binding.scrollView.isGone = false

                                binding.viewedListSize.text = viewModel.viewedQuantity.toString()
                                viewModel.viewedFlow.onEach {
                                    viewedAdapter.setData(it)
                                    Log.d(TAG, "viewedAdapter.setData. Размер= ${it.size}")
                                    Log.d(
                                        TAG,
                                        "viewedAdapter.itemCount = ${viewedAdapter.itemCount}"
                                    )
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                                binding.interestedListSize.text =
                                    viewModel.interestedQuantity.toString()
                                viewModel.interestedFlow.onEach {
                                    interestedAdapter.setData(it)
                                    Log.d(TAG, "interestedAdapter.setData. Размер= ${it.size}")
                                    Log.d(
                                        TAG,
                                        "interestedAdapter.itemCount = ${interestedAdapter.itemCount}"
                                    )
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
//                            ViewModelState.Error -> {
//                                binding.scrollView.isGone = true
//                                binding.progress.isGone = true
//                                findNavController().navigate(R.id.action_FilmFragment_to_ErrorBottomFragment)
//                            }
                        }
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onViewedItemClick(
        item: FilmDbViewed
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmDb.filmTable.filmId
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
                    "collectionName",
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

    private fun onFilmClick(
        item: FilmDbViewed
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmDb.filmTable.filmId
                )
            }

        activity?.findNavController(R.id.ProfileFragment)?.navigate(
            R.id.action_ProfileFragment_to_FilmFragment,
            bundle
        )

//        findNavController().navigate(
//            R.id.action_ProfileFragment_to_FilmFragment,
//            bundle
//        )
    }

    private fun onSerialClick(
        item: FilmDbViewed
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmDb.filmTable.filmId
                )
            }
        findNavController().navigate(
            R.id.action_ProfileFragment_to_SerialFragment,
            bundle
        )
    }

    private fun onPersonClick(
        item: PersonTable
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "staffId",
                    item.personId
                )
            }
        findNavController().navigate(
            R.id.action_ProfileFragment_to_StaffFragment,
            bundle
        )
    }
}