package edu.skillbox.skillcinema.presentation.listPageTop250

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
import edu.skillbox.skillcinema.presentation.adapters.FilmsPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageTop250Binding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ListPageTop250Fragment : Fragment() {

    private var _binding: FragmentListPageTop250Binding? = null
    private val binding get() = _binding!!

    private val filmTop250Adapter = FilmsPagedListAdapter { filmItemData -> onItemClick(filmItemData) }

    @Inject
    lateinit var listPageTop250ViewModelFactory: ListPageTop250ViewModelFactory
    private val viewModel: ListPageTop250ViewModel by viewModels {
        listPageTop250ViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPageTop250Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTop250()

        binding.listPageRecycler.adapter = filmTop250Adapter

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
                                viewModel.pagedFilmsTop250Extended.onEach {
                                    filmTop250Adapter.submitData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.listPageRecycler.isGone = true
                                findNavController().navigate(R.id.action_ListPageTop250Fragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }

    private fun onItemClick(item: FilmItemData) {
        val bundle = Bundle().apply {
            putInt(
                ARG_FILM_ID,
                item.filmId
            )
        }
        findNavController().navigate(R.id.action_ListPageTop250Fragment_to_FilmFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}