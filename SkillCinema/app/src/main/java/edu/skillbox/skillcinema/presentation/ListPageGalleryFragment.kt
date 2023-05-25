package edu.skillbox.skillcinema.presentation

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
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.Gallery12SpansAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageGalleryBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val ARG_FILM_ID = "filmId"

@AndroidEntryPoint
class ListPageGalleryFragment : Fragment() {

    private var _binding: FragmentListPageGalleryBinding? = null
    private val binding get() = _binding!!

//    private val dpToPix: Float = context?.resources?.displayMetrics?.density ?: 0F
//    private val galleryAdapter = Gallery12SpansAdapter( dpToPix ){ image -> onImageClick(image) }

//    private val galleryLayoutManager: GalleryLayoutManager = GalleryLayoutManager(requireContext(), 2)

    @Inject
    lateinit var listPageGalleryViewModelFactory: ListPageGalleryViewModelFactory
    private val viewModel: ListPageGalleryViewModel by viewModels {
        listPageGalleryViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.loadGallery(filmId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPageGalleryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dpToPix: Float = context?.resources?.displayMetrics?.density ?: 0F
        val galleryAdapter = Gallery12SpansAdapter(dpToPix) { currentImage ->
            onImageClick(currentImage)
        }

        val galleryLayoutManager = GridLayoutManager(requireContext(), 2)
        galleryLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position % 3) {
                    0 -> 2
                    else -> 1
                }
            }
        }

        binding.listPageRecycler.layoutManager = galleryLayoutManager
        binding.listPageRecycler.adapter = galleryAdapter

//        val dividerItemDecorationVertical = DividerItemDecoration(context, RecyclerView.VERTICAL)
//        val dividerItemDecorationHorizontal =
//            DividerItemDecoration(context, RecyclerView.HORIZONTAL)
//        context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
//            ?.let {
//                dividerItemDecorationVertical.setDrawable(it)
//                dividerItemDecorationHorizontal.setDrawable(it)
//            }
//        binding.listPageRecycler.addItemDecoration(dividerItemDecorationVertical)
//        binding.listPageRecycler.addItemDecoration(dividerItemDecorationHorizontal)

        binding.imageFilterGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.filterAll.isChecked) {
                viewModel.chosenType = 0
                viewModel.gallery.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterStill.isChecked) {
                viewModel.chosenType = 1
                viewModel.imagesStill.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterShooting.isChecked) {
                viewModel.chosenType = 2
                viewModel.imagesShooting.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterPoster.isChecked) {
                viewModel.chosenType = 3
                viewModel.imagesPoster.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterFanArt.isChecked) {
                viewModel.chosenType = 4
                viewModel.imagesFanArt.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterPromo.isChecked) {
                viewModel.chosenType = 5
                viewModel.imagesPromo.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterConcept.isChecked) {
                viewModel.chosenType = 6
                viewModel.imagesConcept.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterWallpaper.isChecked) {
                viewModel.chosenType = 7
                viewModel.imagesWallpaper.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterCover.isChecked) {
                viewModel.chosenType = 8
                viewModel.imagesCover.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            if (binding.filterScreenshot.isChecked) {
                viewModel.chosenType = 9
                viewModel.imagesScreenshot.onEach {
                    galleryAdapter.setData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
            Log.d("Gallery", "chosenType = ${viewModel.chosenType}")
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListPageGalleryFragment_to_MainFragment)
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                                binding.chipScrollView.isGone = true
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                binding.chipScrollView.isGone = false

                                when (viewModel.chosenType) {
                                    0 -> viewModel.gallery.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    1 -> viewModel.imagesStill.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    2 -> viewModel.imagesShooting.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    3 -> viewModel.imagesPoster.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    4 -> viewModel.imagesFanArt.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    5 -> viewModel.imagesPromo.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    6 -> viewModel.imagesConcept.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    7 -> viewModel.imagesWallpaper.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    8 -> viewModel.imagesCover.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    9 -> viewModel.imagesScreenshot.onEach {
                                        galleryAdapter.setData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                }

                                if (viewModel.galleryQuantity > 0) {
                                    binding.filterAll.isGone = false
                                    binding.filterAll.text =
                                        getString(R.string.all) + "  " + viewModel.galleryQuantity
                                } else {
                                    binding.filterAll.isGone = true
                                }

                                if (viewModel.quantityStill > 0) {
                                    binding.filterStill.isGone = false
                                    binding.filterStill.text =
                                        getString(R.string.still) + "  " + viewModel.quantityStill
                                } else {
                                    binding.filterStill.isGone = true
                                }

                                if (viewModel.quantityShooting > 0) {
                                    binding.filterShooting.isGone = false
                                    binding.filterShooting.text =
                                        getString(R.string.shooting) + "  " + viewModel.quantityShooting
                                } else {
                                    binding.filterShooting.isGone = true
                                }

                                if (viewModel.quantityPoster > 0) {
                                    binding.filterPoster.isGone = false
                                    binding.filterPoster.text =
                                        getString(R.string.poster) + "  " + viewModel.quantityPoster
                                } else {
                                    binding.filterPoster.isGone = true
                                }

                                if (viewModel.quantityFanArt > 0) {
                                    binding.filterFanArt.isGone = false
                                    binding.filterFanArt.text =
                                        getString(R.string.fan_art) + "  " + viewModel.quantityFanArt
                                } else {
                                    binding.filterFanArt.isGone = true
                                }

                                if (viewModel.quantityPromo > 0) {
                                    binding.filterPromo.isGone = false
                                    binding.filterPromo.text =
                                        getString(R.string.promo) + "  " + viewModel.quantityPromo
                                } else {
                                    binding.filterPromo.isGone = true
                                }

                                if (viewModel.quantityConcept > 0) {
                                    binding.filterConcept.isGone = false
                                    binding.filterConcept.text =
                                        getString(R.string.concept) + "  " + viewModel.quantityConcept
                                } else {
                                    binding.filterConcept.isGone = true
                                }

                                if (viewModel.quantityWallpaper > 0) {
                                    binding.filterWallpaper.isGone = false
                                    binding.filterWallpaper.text =
                                        getString(R.string.wallpaper) + "  " + viewModel.quantityWallpaper
                                } else {
                                    binding.filterWallpaper.isGone = true
                                }

                                if (viewModel.quantityCover > 0) {
                                    binding.filterCover.isGone = false
                                    binding.filterCover.text =
                                        getString(R.string.cover) + "  " + viewModel.quantityCover
                                } else {
                                    binding.filterCover.isGone = true
                                }

                                if (viewModel.quantityScreenshot > 0) {
                                    binding.filterScreenshot.isGone = false
                                    binding.filterScreenshot.text =
                                        getString(R.string.screenshot) + "  " + viewModel.quantityScreenshot
                                } else {
                                    binding.filterScreenshot.isGone = true
                                }
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.chipScrollView.isGone = true
                            }
                        }
                    }
            }
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
                putInt(
                    "imagesType",
                    viewModel.chosenType
                )
            }
        findNavController().navigate(
            R.id.action_ListPageGalleryFragment_to_ImagePagerFragment,
            bundle
        )
    }
}