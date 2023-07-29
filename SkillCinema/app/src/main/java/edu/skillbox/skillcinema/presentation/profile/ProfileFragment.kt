package edu.skillbox.skillcinema.presentation.profile

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.presentation.adapters.CollectionAdapter
import edu.skillbox.skillcinema.presentation.adapters.CollectionFilmsAdapter
import edu.skillbox.skillcinema.presentation.adapters.InterestedAdapter
import edu.skillbox.skillcinema.databinding.FragmentProfileBinding
import edu.skillbox.skillcinema.models.collection.CollectionInfo
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.person.PersonTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.ARG_COLLECTION_NAME
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_STAFF_ID
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

    private lateinit var viewedAdapter: CollectionFilmsAdapter

    private val collectionAdapter = CollectionAdapter(
        onOpenCollection = { collection -> onOpenCollection(collection) },
        onDeleteCollection = { collection -> onDeleteCollection(collection) }
    )
    private val interestedAdapter = InterestedAdapter(
        limited = true,
        onFilmClick = { filmItemData -> onFilmClick(filmItemData) },
        onSerialClick = { filmItemData -> onSerialClick(filmItemData) },
        onPersonClick = { personTable -> onPersonClick(personTable) },
        clear = { viewModel.clearAllInterested() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewedAdapter = CollectionFilmsAdapter(limited = true,
            viewedOrCollection = true,
            context = requireContext(),
            onClick = { filmItemData -> onViewedItemClick(filmItemData) },
            clear = { viewModel.removeAllViewedFilms() }
        )
    }

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

        if (viewModel.jobLoadProfileData?.isActive != true) {
            Log.d(TAG, "Вызов loadProfileData() по onViewCreated")
            viewModel.loadProfileData()
        } else {
            Log.d(TAG, "Предотвращён повторный вызов loadProfileData() по onViewCreated")
        }

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
                val newCollectionName =
                    currentFragment.savedStateHandle.get<String>("newCollectionName") ?: ""
                viewModel.createNewCollection(collectionName = newCollectionName)
                currentFragment.savedStateHandle.remove<String>("newCollectionName")
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

        binding.buttonAllInterested.setOnClickListener {
            findNavController().navigate(
                R.id.action_ProfileFragment_to_AllInterestedFragment
            )
        }

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
                                    viewedAdapter.setAdapterData(it)
                                    Log.d(TAG, "viewedAdapter.setData. Размер= ${it.size}")
                                    Log.d(
                                        TAG,
                                        "viewedAdapter.itemCount = ${viewedAdapter.itemCount}"
                                    )
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                                binding.interestedListSize.text =
                                    viewModel.interestedQuantity.toString()
                                viewModel.interestedFlow.onEach {
                                    interestedAdapter.setAdapterData(it)
                                    Log.d(TAG, "interestedAdapter.setData. Размер= ${it.size}")
                                    Log.d(
                                        TAG,
                                        "interestedAdapter.itemCount = ${interestedAdapter.itemCount}"
                                    )
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
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
        item: FilmItemData
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_FILM_ID,
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
                    ARG_COLLECTION_NAME,
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
        item: FilmItemData
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_FILM_ID,
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_ProfileFragment_to_FilmFragment,
            bundle
        )
    }

    private fun onSerialClick(
        item: FilmItemData
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_FILM_ID,
                    item.filmId
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
                    ARG_STAFF_ID,
                    item.personId
                )
            }
        findNavController().navigate(
            R.id.action_ProfileFragment_to_StaffFragment,
            bundle
        )
    }
}