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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.GalleryAdapter
import edu.skillbox.skillcinema.data.SimilarsAdapter
import edu.skillbox.skillcinema.data.StaffInfoAdapter
import edu.skillbox.skillcinema.databinding.FragmentFilmBinding
import edu.skillbox.skillcinema.models.SimilarFilm
import edu.skillbox.skillcinema.models.StaffInfo
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "FilmFragment"

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
        StaffInfoAdapter(maxSize = 20) { staffInfo -> onStaffItemClick(staffInfo) }
    private val staffAdapter =
        StaffInfoAdapter(maxSize = 6) { staffInfo -> onStaffItemClick(staffInfo) }
    private val galleryAdapter = GalleryAdapter { currentImage -> onImageClick(currentImage) }
    private val similarsAdapter =
        SimilarsAdapter(limited = true) { similarFilm -> onSimilarsItemClick(similarFilm) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (arguments != null && viewModel.filmId == 0)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        Log.d(
            "FilmVM",
            "onCreate Film Fragment. VM.filmId = ${viewModel.filmId}, arguments.filmId = $filmId"
        )
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.loadFilmInfo(filmId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actorsRecycler.adapter = actorsAdapter
        binding.staffRecycler.adapter = staffAdapter
        binding.galleryRecycler.adapter = galleryAdapter
        binding.similarsRecycler.adapter = similarsAdapter

        binding.favorite.setOnClickListener {
            Log.d("BD", "Нажата кнопка favorite для ${viewModel.filmId}")
            viewModel.onCollectionButtonClick("Любимое")
        }

        binding.wantedToWatch.setOnClickListener {
            Log.d("BD", "Нажата кнопка wantedToWatch для ${viewModel.filmId}")
            viewModel.onCollectionButtonClick("Хочу посмотреть")
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

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_FilmFragment_to_MainFragment)
        }

        binding.searchButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_FilmFragment_to_SearchFragment
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
//                        binding.favorite.background.setTint(resources.getColor(R.color.blue, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} в коллекции \"Любимое\"")
                    } else {
                        binding.favorite.setColorFilter(resources.getColor(R.color.grey_4, null))
//                        binding.favorite.background.setTint(
//                            resources.getColor(
//                                R.color.grey_4,
//                                null
//                            )
//                        )
                        Log.d(TAG, "Фильм ${viewModel.filmId} отсутствует в коллекции \"Любимое\"")
                    }
                }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.wantedToWatchChannel.collect { isWantedToWatch ->
                    if (isWantedToWatch) {
//                        binding.wantedToWatch.background.setTint(resources.getColor(R.color.blue, null))
                        binding.wantedToWatch.setColorFilter(resources.getColor(R.color.blue, null))
                        Log.d(TAG, "Фильм ${viewModel.filmId} в коллекции \"Хочу посмотреть\"")

                    } else {
                        binding.wantedToWatch.setColorFilter(resources.getColor(R.color.grey_4, null))
//                        binding.wantedToWatch.background.setTint(
//                            resources.getColor(
//                                R.color.grey_4,
//                                null
//                            )
//                        )
                        Log.d(TAG, "Фильм ${viewModel.filmId} отсутствует в коллекции \"Хочу посмотреть\"")
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
                                    galleryAdapter.setData(viewModel.imageWithTypeList)

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
                                    similarsAdapter.setData(viewModel.similarFilmList)

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
//        activity?.viewModelStore?.clear()
//        this.viewModelStore.clear()
//        viewModelStore.clear()
//        val application = requireContext().applicationContext as App
//        activity
//        requireActivity().viewModelStore.clear()
//
//        Log.d("FilmVM", "Film Fragment. onDestroyView.")
    }

//    override fun onDetach() {
//        super.onDetach()
//        requireActivity().viewModelStore.clear()
//        Log.d("FilmVM", "Film Fragment. onDetach.")
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (activity != null) {
//            Log.d("FilmVM", "FilmFragment. Activity != null. ${activity.toString()}")
//            val vmStore = activity!!.viewModelStore
//            Log.d("FilmVM", "FilmFragment. vmStore = $vmStore")
//            activity!!.viewModelStore.clear()
//            val vmStoreCleared = activity!!.viewModelStore
//            Log.d("FilmVM", "FilmFragment. vmStoreCleared = $vmStoreCleared")
//        }
//        Log.d("FilmVM", "Film Fragment. onDestroy.")
//    }

    private fun onStaffItemClick(
        item: StaffInfo
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

    private fun onSimilarsItemClick(
        item: SimilarFilm
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