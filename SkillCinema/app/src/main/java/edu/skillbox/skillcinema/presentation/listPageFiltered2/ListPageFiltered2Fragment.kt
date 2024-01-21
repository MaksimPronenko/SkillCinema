package edu.skillbox.skillcinema.presentation.listPageFiltered2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.presentation.adapters.FilmsPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageFiltered2Binding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.ARG_COUNTRY_2_KEY
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_GENRE_2_KEY
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ListPageFilter2Fragment"

@AndroidEntryPoint
class ListPageFiltered2Fragment : Fragment() {

    private var _binding: FragmentListPageFiltered2Binding? = null
    private val binding get() = _binding!!

    private var genre2Key: Int? = null
    private var country2Key: Int? = null

    private val filmFiltered2Adapter =
        FilmsPagedListAdapter { filmItemData -> onItemClick(filmItemData) }

    @Inject
    lateinit var listPageFiltered2ViewModelFactory: ListPageFiltered2ViewModelFactory
    private val viewModel: ListPageFiltered2ViewModel by activityViewModels {
        listPageFiltered2ViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre2Key = arguments?.getInt(ARG_GENRE_2_KEY)
        country2Key = arguments?.getInt(ARG_COUNTRY_2_KEY)
    }

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

        if (genre2Key != null && country2Key != null) {
            viewModel.loadFilmsFiltered2(genre2Key!!, country2Key!!)
            Log.d(TAG, "loadFilmsFiltered2($genre2Key, $country2Key)")
        }

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
                                binding.listPageRecycler.isGone = true
                                findNavController().navigate(R.id.action_ListPageFiltered2Fragment_to_ErrorBottomFragment)
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
                    ARG_FILM_ID,
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_ListPageFiltered2Fragment_to_FilmFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}