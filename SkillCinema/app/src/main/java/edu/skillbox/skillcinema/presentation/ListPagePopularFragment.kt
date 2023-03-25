package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmsTopPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPagePopularBinding
import edu.skillbox.skillcinema.models.FilmTop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ListPagePopularFragment : Fragment() {

    private var _binding: FragmentListPagePopularBinding? = null
    private val binding get() = _binding!!

    private val filmTop100PopularAdapter =
        FilmsTopPagedListAdapter { filmTop -> onItemClick(filmTop) }

    @Inject
    lateinit var listPagePopularViewModelFactory: ListPagePopularViewModelFactory
    private val viewModel: ListPagePopularViewModel by viewModels {
        listPagePopularViewModelFactory
    }

//    private val viewModel: ListPagePopularViewModel by viewModels {
//        ListPagePopularViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPagePopularBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPageRecycler.adapter = filmTop100PopularAdapter

        val dividerItemDecorationVertical = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerItemDecorationHorizontal =
            DividerItemDecoration(context, RecyclerView.HORIZONTAL)
        context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
            ?.let {
                dividerItemDecorationVertical.setDrawable(it)
                dividerItemDecorationHorizontal.setDrawable(it)
            }
        binding.listPageRecycler.addItemDecoration(dividerItemDecorationVertical)
        binding.listPageRecycler.addItemDecoration(dividerItemDecorationHorizontal)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListPagePopularFragment_to_MainFragment)
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                viewModel.pagedFilmsTop100Popular.onEach {
                                    filmTop100PopularAdapter.submitData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                            }
                        }
                    }
            }
    }

    private fun onItemClick(item: FilmTop) {
        val bundle = Bundle().apply {
            putInt("filmId", item.filmId)
        }
        findNavController().navigate(R.id.action_ListPagePopularFragment_to_FilmFragment, bundle)
    }
}