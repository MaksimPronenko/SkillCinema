package edu.skillbox.skillcinema.presentation.serial

import android.animation.LayoutTransition
import android.content.Intent
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
import edu.skillbox.skillcinema.databinding.FragmentSerialBinding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.presentation.adapters.FilmAdapter
import edu.skillbox.skillcinema.presentation.adapters.GalleryAdapter
import edu.skillbox.skillcinema.presentation.adapters.StaffAdapter
import edu.skillbox.skillcinema.utils.ARG_CURRENT_IMAGE
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_STAFF_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "Serial.Fragment"

@AndroidEntryPoint
class SerialFragment : Fragment() {

    @Inject
    lateinit var serialViewModelFactory: SerialViewModelFactory
    private val viewModel: SerialViewModel by viewModels { serialViewModelFactory }

    private var _binding: FragmentSerialBinding? = null
    private val binding get() = _binding!!

    private val actorsAdapter =
        StaffAdapter(maxSize = 20) { staffTable -> onStaffItemClick(staffTable) }
    private val staffAdapter =
        StaffAdapter(maxSize = 6) { staffTable -> onStaffItemClick(staffTable) }
    private val galleryAdapter = GalleryAdapter { currentImage -> onImageClick(currentImage) }
    private val similarsAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_FilmFragment_to_ListPageSimilarsFragment) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.loadSerialData(filmId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentSerialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.share.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    if(viewModel.imdbId != null) {
                        putExtra(Intent.EXTRA_TEXT, "https://www.imdb.com/title/${viewModel.imdbId}/")
                    } else {
                        // Если отсутствует идентификатор imdbId, то отправляем хоть какие-то
                        // сведения о фильме: название и год, или только название, если года
                        // тоже нет в данных.
                        if (viewModel.year != null) {
                            putExtra(Intent.EXTRA_TEXT, "${viewModel.name}, ${viewModel.year}")
                        } else {
                            putExtra(Intent.EXTRA_TEXT, viewModel.name)
                        }
                    }
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
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
            findNavController().navigate(R.id.action_SerialFragment_to_BottomFragment, bundle)
        }

        // При отключении диалога BottomDialogFragment инициирую проверку данных о включении
        // фильма в "Любимое" и "Хочу посмотреть"
        val currentFragment = findNavController().getBackStackEntry(R.id.SerialFragment)
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

        binding.buttonAllSeasonsAndEpisodes.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        "filmId",
                        viewModel.filmId
                    )
                    putString(
                        "name",
                        viewModel.name
                    )
                }
            findNavController().navigate(
                R.id.action_SerialFragment_to_SerialContentFragment,
                bundle
            )
        }

        binding.buttonAllActors.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt("filmId", viewModel.filmId)
                    putString("filmName", viewModel.name)
                    putBoolean("staffType", false)
                }
            findNavController().navigate(
                R.id.action_SerialFragment_to_AllStaffFragment,
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
                R.id.action_SerialFragment_to_AllStaffFragment,
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
                R.id.action_SerialFragment_to_ListPageGalleryFragment,
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
                R.id.action_SerialFragment_to_ListPageSimilarsFragment,
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

                                binding.yearAndGenresAndSeasonsQuantity.text =
                                    viewModel.year.toString() + ", " + viewModel.genres +
                                            ", " + viewModel.seasonsInformation

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

                                binding.seasonsAndEpisodesInfo.text = viewModel.serialInformation
                                binding.buttonAllSeasonsAndEpisodes.isGone =
                                    viewModel.quantityOfEpisodes == 0

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
                                findNavController().navigate(R.id.action_SerialFragment_to_ErrorBottomFragment)
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
                    ARG_STAFF_ID,
                    item.staffId
                )
            }
        findNavController().navigate(
            R.id.action_SerialFragment_to_StaffFragment,
            bundle
        )
    }

    private fun onItemClick(
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
            R.id.action_SerialFragment_to_SerialFragment,
            bundle
        )
    }

    private fun onImageClick(currentImage: String) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_FILM_ID,
                    viewModel.filmId
                )
                putString(
                    ARG_CURRENT_IMAGE,
                    currentImage
                )
            }
        findNavController().navigate(
            R.id.action_SerialFragment_to_ImagePagerFragment,
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