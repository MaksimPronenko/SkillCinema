package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.SearchPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentSearchBinding
import edu.skillbox.skillcinema.models.FilmFiltered
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Search.Fragment"

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter =
        SearchPagedListAdapter { filmFiltered -> onFilmItemClick(filmFiltered) }

    @Inject
    lateinit var searchViewModelFactory: SearchViewModelFactory
    private val viewModel: SearchViewModel by activityViewModels {
        searchViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPageRecycler.adapter = filmsAdapter

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

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_SearchFragment_to_SearchSettings1Fragment)
        }

        Log.d(TAG, "viewModel.searchText = ${viewModel.searchText}")
        binding.searchTextField.setText(viewModel.searchText)
        binding.searchTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                viewModel.search(s.toString())
            }
        })

        viewModel.search()

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            SearchViewModelState.Searching -> {
                                Log.d(TAG, "Перешли в состояние поиска")
                                binding.progress.isGone = false
                                binding.searchFailed.isVisible = false
                                binding.listPageRecycler.isVisible = false
                            }
                            SearchViewModelState.SearchSuccessfull -> {
                                Log.d(TAG, "Перешли в состояние удачного поиска")
                                binding.progress.isGone = true
                                binding.searchFailed.isVisible = false
                                binding.listPageRecycler.isVisible = true

                                viewModel.filmsFlow.onEach {
                                    filmsAdapter.submitData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            SearchViewModelState.SearchFailed -> {
                                Log.d(TAG, "Перешли в состояние неудачного поиска")
                                binding.progress.isGone = true
                                binding.searchFailed.isVisible = true
                                binding.listPageRecycler.isVisible = false
                            }
                            SearchViewModelState.SearchError -> {
                                Log.d(TAG, "Перешли в состояние ошибки")
                                binding.progress.isGone = true
                                binding.searchFailed.isVisible = false
                                binding.listPageRecycler.isVisible = false
                                findNavController().navigate(R.id.action_SearchFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }

    private fun onFilmItemClick(
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
            R.id.action_SearchFragment_to_FilmFragment,
            bundle
        )
    }
}