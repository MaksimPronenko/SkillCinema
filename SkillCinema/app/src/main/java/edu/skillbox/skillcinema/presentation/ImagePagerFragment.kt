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
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.ImageAdapter
import edu.skillbox.skillcinema.databinding.FragmentImagePagerBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val ARG_FILM_ID = "filmId"
private const val ARG_CURRENT_IMAGE = "currentImage"
private const val ARG_IMAGES_TYPE = "imagesType"

@AndroidEntryPoint
class ImagePagerFragment : Fragment() {

    @Inject
    lateinit var imagePagerViewModelFactory: ImagePagerViewModelFactory
    private val viewModel: ImagePagerViewModel by viewModels {
        imagePagerViewModelFactory
    }

    private var _binding: FragmentImagePagerBinding? = null
    private val binding get() = _binding!!

    private val imageAdapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        val currentImage = arguments?.getString(ARG_CURRENT_IMAGE) ?: ""
        Log.d("Pager", "Fragment. chosenImage = $currentImage")
        val imagesType = arguments?.getInt(ARG_IMAGES_TYPE) ?: 0
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.loadImages(filmId, currentImage, imagesType)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePagerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pager.adapter = imageAdapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                                binding.pager.isGone = true
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                binding.pager.isGone = false
                                viewModel.images.onEach {
                                    imageAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.pager.isGone = true
                                findNavController().navigate(R.id.action_ImagePagerFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }
}