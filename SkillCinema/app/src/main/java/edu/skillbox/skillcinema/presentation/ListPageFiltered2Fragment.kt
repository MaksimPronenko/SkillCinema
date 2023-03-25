package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmsFilteredPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageFiltered2Binding
import edu.skillbox.skillcinema.models.FilmFiltered
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ListPageFiltered2Fragment : Fragment() {

    private var _binding: FragmentListPageFiltered2Binding? = null
    private val binding get() = _binding!!

    private val filmFiltered2Adapter =
        FilmsFilteredPagedListAdapter { filmFiltered -> onFilmFilteredItemClick(filmFiltered) }

//    private val viewModel: ListPageFiltered2ViewModel by viewModels {
//        ListPageFiltered2ViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

//    val app: App = requireActivity().application as App
//    private val viewModel: ListPageFiltered2ViewModel by viewModels {
//        app.appComponent.listPageFiltered2ViewModelFactory()
//    }

    @Inject
    lateinit var listPageFiltered2ViewModelFactory: ListPageFiltered2ViewModelFactory
    private val viewModel: ListPageFiltered2ViewModel by activityViewModels {
        listPageFiltered2ViewModelFactory
    }

//    private val viewModel: ListPageFiltered2ViewModel by viewModels{
//        DaggerAppComponent.create().listPageFiltered2ViewModelFactory()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPageFiltered2Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireContext().applicationContext as App
        binding.listName.text = application.filteredFilms2title
        
        binding.listPageRecycler.adapter = filmFiltered2Adapter

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
            findNavController().navigate(R.id.action_ListPageFiltered2Fragment_to_MainFragment)
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
                                viewModel.pagedFilmsFiltered2.onEach {
                                    filmFiltered2Adapter.submitData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                            }
                        }
                    }
            }
    }

    private fun onFilmFilteredItemClick(
        item: FilmFiltered
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.kinopoiskId
                )
            }
        findNavController().navigate(
            R.id.action_ListPageFiltered2Fragment_to_FilmFragment,
            bundle
        )
    }
}