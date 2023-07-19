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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmsPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageSerialsBinding
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ListPageSerialsFragment : Fragment() {

    private var _binding: FragmentListPageSerialsBinding? = null
    private val binding get() = _binding!!

    //    private val filmSeriesAdapter =
//        FilmsFilteredPagedListAdapter { filmFiltered -> onFilmFilteredItemClick(filmFiltered) }
    private val serialsAdapter =
        FilmsPagedListAdapter { filmItemData -> onItemClick(filmItemData) }

    @Inject
    lateinit var listPageSerialsViewModelFactory: ListPageSerialsViewModelFactory
    private val viewModel: ListPageSerialsViewModel by viewModels {
        listPageSerialsViewModelFactory
    }

//    private val viewModel: ListPageSeriesViewModel by viewModels {
//        ListPageSeriesViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentListPageSerialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadSerials()

        binding.listPageRecycler.adapter = serialsAdapter

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
                                viewModel.pagedSerials.onEach {
                                    serialsAdapter.submitData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.listPageRecycler.isGone = true
                                findNavController().navigate(R.id.action_ListPageSerialsFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
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
            R.id.action_ListPageSerialsFragment_to_SerialFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}