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
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.CollectionAdapter
import edu.skillbox.skillcinema.data.ViewedAdapter
import edu.skillbox.skillcinema.databinding.FragmentProfileBinding
import edu.skillbox.skillcinema.models.CollectionInfo
import edu.skillbox.skillcinema.models.FilmTable
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
        ViewedAdapter(limited = true) { filmTable -> onViewedItemClick(filmTable) }
//    private val collectionAdapter =
//        CollectionAdapter { collection -> onCollectionItemClick(collection) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewedRecycler.adapter = viewedAdapter
//        binding.collectionsRecycler.adapter = collectionAdapter

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

//    private fun onCollectionItemClick(
//        item: CollectionInfo
//    ) {
//        val bundle =
//            Bundle().apply {
//                putString(
//                    "collection",
//                    item.collectionName
//                )
//            }
//        findNavController().navigate(
//            R.id.action_ProfileFragment_to_CollectionFragment,
//            bundle
//        )
//    }
}