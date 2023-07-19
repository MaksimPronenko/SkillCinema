package edu.skillbox.skillcinema.presentation

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmAdapter
import edu.skillbox.skillcinema.data.GalleryAdapter
import edu.skillbox.skillcinema.data.StaffAdapter
import edu.skillbox.skillcinema.databinding.FragmentFilmBinding
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.StaffTable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "Film.Fragment"

private const val ARG_FILM_ID = "filmId"

@AndroidEntryPoint
class FilmFragment : Fragment() {

    @Inject
    lateinit var filmViewModelFactory: FilmViewModelFactory
    private val viewModel: FilmViewModel by viewModels { filmViewModelFactory }

//    @Inject
//    lateinit var filmViewModelFactory: FilmViewModelFactory
//    private val viewModel: FilmViewModel by activityViewModels {
//        filmViewModelFactory
//    }

    private var _binding: FragmentFilmBinding? = null
    private val binding get() = _binding!!

    private val actorsAdapter =
        StaffAdapter(maxSize = 20) { staffTable -> onStaffItemClick(staffTable) }
    private val staffAdapter =
        StaffAdapter(maxSize = 6) { staffTable -> onStaffItemClick(staffTable) }
    private val galleryAdapter = GalleryAdapter { currentImage -> onImageClick(currentImage) }
    private val similarsAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { showListPageSimilars() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.loadFilmData(filmId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        binding.actorsRecycler.adapter = actorsAdapter
        binding.staffRecycler.adapter = staffAdapter
        binding.galleryRecycler.adapter = galleryAdapter
        binding.similarsRecycler.adapter = similarsAdapter

        Log.d(TAG, "Запускаем viewModel.loadSimilarFilmsData() из onViewCreated")
        viewModel.loadSimilarFilmsData()

        binding.favorite.setOnClickListener {
            viewModel.onCollectionButtonClick("Любимое")
        }

        binding.wantedToWatch.setOnClickListener {
            viewModel.onCollectionButtonClick("Хочу посмотреть")
        }

        binding.viewed.setOnClickListener {
            viewModel.onViewedButtonClick()
        }

        binding.shortDescription.setOnClickListener {
            applyLayoutTransition()
            viewModel.shortDescriptionCollapsed = !viewModel.shortDescriptionCollapsed
            binding.shortDescription.text =
                if (viewModel.shortDescriptionCollapsed)
                    cutText(viewModel.shortDescription!!)
                else viewModel.shortDescription
        }

        binding.description.setOnClickListener {
            applyLayoutTransition()
            viewModel.descriptionCollapsed = !viewModel.descriptionCollapsed
            binding.description.text =
                if (viewModel.descriptionCollapsed)
                    cutText(viewModel.description!!)
                else viewModel.description
        }

        binding.collection.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        "filmId",
                        viewModel.filmId
                    )
                    putString(
                        "posterSmall",
                        viewModel.posterSmall
                    )
                    putString(
                        "name",
                        viewModel.name
                    )
                    putString(
                        "year",
                        if (viewModel.year == null) "" else viewModel.year.toString()
                    )
                    putString(
                        "genres",
                        viewModel.genres
                    )
                }
            findNavController().navigate(R.id.action_FilmFragment_to_BottomFragment, bundle)
        }

        // При отключении диалога BottomDialogFragment инициирую проверку данных о включении
        // фильма в "Любимое" и "Хочу посмотреть"
        val currentFragment = findNavController().getBackStackEntry(R.id.FilmFragment)
        val dialogObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkFilmInCollections()
            }
        }
        val dialogLifecycle = currentFragment.lifecycle
        dialogLifecycle.addObserver(dialogObserver)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                dialogLifecycle.removeObserver(dialogObserver)
            }
        })

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonAllActors.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt("filmId", viewModel.filmId)
                    putString("filmName", viewModel.name)
                    putBoolean("staffType", false)
                }
            findNavController().navigate(
                R.id.action_FilmFragment_to_AllStaffFragment,
                bundle
            )
        }

        binding.buttonAllStaff.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt("filmId", viewModel.filmId)
                    putString("filmName", viewModel.name)
                    putBoolean("staffType", true)
                }
            findNavController().navigate(
                R.id.action_FilmFragment_to_AllStaffFragment,
                bundle
            )
        }

        binding.buttonAllGallery.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        "filmId",
                        viewModel.filmId
                    )
                }
            findNavController().navigate(
                R.id.action_FilmFragment_to_ListPageGalleryFragment,
                bundle
            )
        }

        binding.buttonAllSimilars.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        "filmId",
                        viewModel.filmId
                    )
                }
            findNavController().navigate(
                R.id.action_FilmFragment_to_ListPageSimilarsFragment,
                bundle
            )
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.favoriteChannel.collect { isFavorite ->
                    if (isFavorite) {
                        binding.favorite.setColorFilter(resources.getColor(R.color.blue, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} в коллекции \"Любимое\"")
                    } else {
                        binding.favorite.setColorFilter(resources.getColor(R.color.grey_4, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} отсутствует в коллекции \"Любимое\"")
                    }
                }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.wantedToWatchChannel.collect { isWantedToWatch ->
                    if (isWantedToWatch) {
                        binding.wantedToWatch.setColorFilter(resources.getColor(R.color.blue, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} в коллекции \"Хочу посмотреть\"")

                    } else {
                        binding.wantedToWatch.setColorFilter(
                            resources.getColor(
                                R.color.grey_4,
                                null
                            )
                        )
                        Log.d(
                            TAG,
                            "Фильм ${viewModel.filmId} отсутствует в коллекции \"Хочу посмотреть\""
                        )
                    }
                }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.viewedChannel.collect { viewed ->
                    if (viewed) {
                        binding.viewed.setImageResource(R.drawable.watched)
                        binding.viewed.setColorFilter(resources.getColor(R.color.blue, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} просмотрен")

                    } else {
                        binding.viewed.setImageResource(R.drawable.not_watched)
                        binding.viewed.setColorFilter(resources.getColor(R.color.grey_4, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} не просмотрен")
                    }
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

                                Glide
                                    .with(binding.poster.context)
                                    .load(viewModel.poster)
                                    .into(binding.poster)

                                if (viewModel.rating != null)
                                    binding.ratingAndName.text =
                                        viewModel.rating.toString() + ", " + viewModel.name
                                else
                                    binding.ratingAndName.text = viewModel.name

                                val yearPart =
                                    if (viewModel.year == null) "" else viewModel.year.toString() + ", "
                                binding.yearAndGenres.text = yearPart + viewModel.genres

                                binding.countriesAndLengthAndAgeLimit.text =
                                    viewModel.countries.toString() +
                                            if (viewModel.filmLength != null) {
                                                ", " + viewModel.filmLength.toString()
                                            } else {
                                                ""
                                            } +
                                            if (viewModel.ageLimit != null) {
                                                ", " + viewModel.ageLimit.toString()
                                            } else {
                                                ""
                                            }

                                if (viewModel.shortDescription != null)
                                    binding.shortDescription.text =
                                        if (viewModel.shortDescriptionCollapsed)
                                            cutText(viewModel.shortDescription!!)
                                        else viewModel.shortDescription
                                else {
                                    binding.shortDescription.isGone = true
                                    binding.blankLineBetweenTextFields.isGone = true
                                }

                                if (viewModel.description != null)
                                    binding.description.text =
                                        if (viewModel.descriptionCollapsed) cutText(viewModel.description!!)
                                        else viewModel.description
                                else {
                                    binding.description.isGone = true
                                    binding.blankLineBetweenTextFields.isGone = true
                                }

                                if (viewModel.actorsQuantity == 0) {
                                    binding.actorsRecyclerTitle.isGone = true
                                    binding.buttonAllActors.isGone = true
                                    binding.actorsRecycler.isGone = true
                                } else {
                                    actorsAdapter.setData(viewModel.actorsList)

                                    if (viewModel.actorsQuantity > 20) {
                                        binding.actorsListSize.text =
                                            viewModel.actorsQuantity.toString()
                                    } else {
                                        binding.buttonAllActors.isGone = true
                                    }

                                    if (viewModel.actorsQuantity in 1..6) {
                                        binding.actorsRecycler.layoutManager = GridLayoutManager(
                                            context,
                                            (viewModel.actorsQuantity / 2.0).roundToInt(),
                                            GridLayoutManager.HORIZONTAL,
                                            false
                                        )
                                    }
                                }

                                if (viewModel.staffQuantity == 0) {
                                    binding.staffRecyclerTitle.isGone = true
                                    binding.buttonAllStaff.isGone = true
                                    binding.staffRecycler.isGone = true
                                } else {
                                    staffAdapter.setData(viewModel.staffList)

                                    if (viewModel.staffQuantity > 6) {
                                        binding.staffListSize.text =
                                            viewModel.staffQuantity.toString()
                                    } else {
                                        binding.buttonAllStaff.isGone = true
                                    }

                                    if (viewModel.staffQuantity == 1) {
                                        binding.staffRecycler.layoutManager =
                                            LinearLayoutManager(context)
                                    }
                                }

                                if (viewModel.gallerySize == 0) {
                                    binding.galleryRecyclerTitle.isGone = true
                                    binding.buttonAllGallery.isGone = true
                                    binding.galleryRecycler.isGone = true
                                } else {
                                    galleryAdapter.setData(viewModel.imageTableList!!)

                                    if (viewModel.gallerySize > 20) {
                                        binding.galleryListSize.text =
                                            viewModel.gallerySize.toString()
                                    } else {
                                        binding.buttonAllGallery.isGone = true
                                    }
                                }

                                if (viewModel.similarsQuantity == 0) {
                                    binding.similarsRecyclerTitle.isGone = true
                                    binding.buttonAllSimilars.isGone = true
                                    binding.similarsRecycler.isGone = true
                                } else {
                                    viewModel.similarsFlow.onEach {
                                        similarsAdapter.setAdapterData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)

                                    if (viewModel.similarsQuantity > 20) {
                                        binding.similarsListSize.text =
                                            viewModel.similarsQuantity.toString()
                                    } else {
                                        binding.buttonAllSimilars.isGone = true
                                    }
                                }
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

    private fun onStaffItemClick(
        item: StaffTable
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "staffId",
                    item.staffId
                )
            }
        findNavController().navigate(
            R.id.action_FilmFragment_to_StaffFragment,
            bundle
        )
    }

    private fun onItemClick(
        item: FilmItemData
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_FilmFragment_to_FilmFragment,
            bundle
        )
    }

    private fun showListPageSimilars() {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    viewModel.filmId
                )
            }
        findNavController().navigate(
            R.id.action_FilmFragment_to_ListPageSimilarsFragment,
            bundle
        )
    }

    private fun onImageClick(currentImage: String) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    viewModel.filmId
                )
                putString(
                    "currentImage",
                    currentImage
                )
            }
        findNavController().navigate(
            R.id.action_FilmFragment_to_ImagePagerFragment,
            bundle
        )
    }

    private fun cutText(baseText: String) = if (baseText.length > 250) baseText.substring(
        startIndex = 0,
        endIndex = 249
    ) + "..." else baseText

    private fun applyLayoutTransition() {
        val transition = LayoutTransition()
        transition.setDuration(300)
        transition.enableTransitionType(LayoutTransition.CHANGING)
        binding.scrollContainer.layoutTransition = transition
    }
}