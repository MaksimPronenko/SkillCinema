package edu.skillbox.skillcinema.presentation

import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.GalleryAdapter
import edu.skillbox.skillcinema.data.SimilarsAdapter
import edu.skillbox.skillcinema.data.StaffInfoAdapter
import edu.skillbox.skillcinema.databinding.FilmBottomDialogBinding
import edu.skillbox.skillcinema.databinding.FragmentFilmBinding
import edu.skillbox.skillcinema.models.ImageWithType
import edu.skillbox.skillcinema.models.SimilarFilm
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.roundToInt

private const val ARG_FILM_ID = "filmId"

@AndroidEntryPoint
class FilmFragment : Fragment() {

    @Inject
    lateinit var filmViewModelFactory: FilmViewModelFactory
    private val viewModel: FilmViewModel by viewModels { filmViewModelFactory }

    private var _binding: FragmentFilmBinding? = null
    private val binding get() = _binding!!

    private var _bottomDialogBinding: FilmBottomDialogBinding? = null
    private val bottomDialogBinding get() = _bottomDialogBinding!!

    private val actorsAdapter = StaffInfoAdapter(maxSize = 20)
    private val staffAdapter = StaffInfoAdapter(maxSize = 6)
    private val galleryAdapter = GalleryAdapter(limited = true) { image -> onImageClick(image) }
    private val similarsAdapter =
        SimilarsAdapter(limited = true) { similarFilm -> onSimilarsItemClick(similarFilm) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (arguments != null && viewModel.filmId == 0)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
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
//        _bottomDialogBinding = FilmBottomDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actorsRecycler.adapter = actorsAdapter
        binding.staffRecycler.adapter = staffAdapter
        binding.galleryRecycler.adapter = galleryAdapter
        binding.similarsRecycler.adapter = similarsAdapter

        //        posterUrl?.let {
//            Glide
//                .with(binding.poster.context)
//                .load(posterUrl)
//                .into(binding.poster)
//        }

        binding.collection.setOnClickListener {
            val dialog = context?.let { fragmentContext -> BottomSheetDialog(fragmentContext) }

            _bottomDialogBinding = FilmBottomDialogBinding.inflate(layoutInflater)

            bottomDialogBinding.closeButton.setOnClickListener {
                dialog?.dismiss()
            }

            dialog?.setCancelable(false)

            dialog?.setContentView(bottomDialogBinding.root)

            dialog?.show()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_FilmFragment_to_MainFragment)
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

                                binding.yearAndGenres.text =
                                    viewModel.year.toString() + ", " + viewModel.genres

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
                                        viewModel.shortDescription
                                else {
                                    binding.shortDescription.isGone = true
                                    binding.blankLineBetweenTextFields.isGone = true
                                }

                                if (viewModel.description != null)
                                    binding.description.text = viewModel.description
                                else {
                                    binding.description.isGone = true
                                    binding.blankLineBetweenTextFields.isGone = true
                                }

                                if (viewModel.actorsQuantity == 0) {
                                    binding.actorsRecyclerTitle.isGone = true
                                    binding.buttonAllActors.isGone = true
                                    binding.actorsRecycler.isGone = true
                                } else {
                                    viewModel.actors.onEach {
                                        actorsAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)

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
                                    viewModel.staff.onEach {
                                        staffAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)

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

                                if (viewModel.galleryQuantity == 0) {
                                    binding.galleryRecyclerTitle.isGone = true
                                    binding.buttonAllGallery.isGone = true
                                    binding.galleryRecycler.isGone = true
                                } else {
                                    viewModel.gallery.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)

                                    if (viewModel.galleryQuantity > 20) {
                                        binding.galleryListSize.text =
                                            viewModel.galleryQuantity.toString()
                                    } else {
                                        binding.buttonAllGallery.isGone = true
                                    }
                                }

                                if (viewModel.similarsQuantity == 0) {
                                    binding.similarsRecyclerTitle.isGone = true
                                    binding.buttonAllSimilars.isGone = true
                                    binding.similarsRecycler.isGone = true
                                } else {
                                    viewModel.similars.onEach {
                                        similarsAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)

                                    if (viewModel.similarsQuantity > 20) {
                                        binding.similarsListSize.text =
                                            viewModel.similarsQuantity.toString()
                                    } else {
                                        binding.buttonAllSimilars.isGone = true
                                    }
                                }

//                                viewModel.pagedFilmsTop100Popular.onEach {
//                                    filmTop100PopularAdapter.submitData(it)
//                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.scrollView.isGone = true
                                binding.progress.isGone = true
                            }
                        }
                    }
            }
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

    private fun onImageClick(
        item: ImageWithType
    ) {

    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment FilmFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            FilmFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}