package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
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
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmsPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageFiltered1Binding
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ListPageFiltered1.Fragment"

private const val ARG_GENRE_1_KEY = "genre1Key"
private const val ARG_COUNTRY_1_KEY = "country1Key"

@AndroidEntryPoint
class ListPageFiltered1Fragment : Fragment() {

    private var _binding: FragmentListPageFiltered1Binding? = null
    private val binding get() = _binding!!

    var genre1Key: Int? = null
    var country1Key: Int? = null

    private val filmFiltered1Adapter =
        FilmsPagedListAdapter { filmItemData -> onItemClick(filmItemData) }

//    private val viewModel: ListPageFiltered1ViewModel by viewModels {
//        ListPageFiltered1ViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

//    val app: App = requireActivity().application as App
//    private val viewModel: ListPageFiltered1ViewModel by viewModels {
//        app.appComponent.listPageFiltered1ViewModelFactory()
//    }

    @Inject
    lateinit var listPageFiltered1ViewModelFactory: ListPageFiltered1ViewModelFactory
    private val viewModel: ListPageFiltered1ViewModel by viewModels {
        listPageFiltered1ViewModelFactory
    }

//    @Inject
//    lateinit var listPageFiltered1ViewModelFactory: ListPageFiltered1ViewModelFactory
//    private val viewModel: ListPageFiltered1ViewModel by activityViewModels {
//        listPageFiltered1ViewModelFactory
//    }

//    private val viewModel: ListPageFiltered1ViewModel by viewModels{
//        DaggerAppComponent.create().listPageFiltered1ViewModelFactory()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre1Key = arguments?.getInt(ARG_GENRE_1_KEY)
        country1Key = arguments?.getInt(ARG_COUNTRY_1_KEY)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPageFiltered1Binding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        if (genre1Key != null && country1Key != null) {
            viewModel.loadFilmsFiltered1(genre1Key!!, country1Key!!)
            Log.d(TAG, "loadFilmsFiltered1($genre1Key, $country1Key)")
        }

        val application = requireContext().applicationContext as App
        binding.listName.text = application.filteredFilms1title

        binding.listPageRecycler.adapter = filmFiltered1Adapter

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

//        binding.mainButton.setOnClickListener {
//            findNavController().navigate(R.id.action_ListPageFiltered1Fragment_to_MainFragment)
//        }

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
                                viewModel.pagedFilmsFiltered1.onEach {
                                    filmFiltered1Adapter.submitData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
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
            R.id.action_ListPageFiltered1Fragment_to_FilmFragment,
            bundle
        )
    }
}