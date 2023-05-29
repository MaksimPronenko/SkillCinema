package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmPremieresAdapter
import edu.skillbox.skillcinema.data.FilmsFilteredPagedListAdapter
import edu.skillbox.skillcinema.data.FilmsTopPagedListAdapter
import edu.skillbox.skillcinema.databinding.FragmentMainBinding
import edu.skillbox.skillcinema.models.FilmFiltered
import edu.skillbox.skillcinema.models.FilmPremiere
import edu.skillbox.skillcinema.models.FilmTop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val TAG = "Main"
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class MainFragment : Fragment() {

//    private val viewModel: MainViewModel by activityViewModels {
//        MainViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

//    val app: App = requireActivity().application as App
//    private val viewModel: MainViewModel by activityViewModels {
//        app.appComponent.mainViewModelFactory()
//    }

//    private val viewModel: MainViewModel by activityViewModels {
//        DaggerAppComponent.create().mainViewModelFactory()
//    }

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by activityViewModels {
        mainViewModelFactory
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val filmPremieresAdapter =
        FilmPremieresAdapter(limited = true) { filmPremiere -> onPremiereItemClick(filmPremiere) }

//    private val filmPremieresAdapter = FilmPremieresAdapter(limited = true)
//    private val filmTop100PopularAdapter = FilmsTopPagedListAdapter()

    private
    val filmTop100PopularAdapter =
        FilmsTopPagedListAdapter { filmTop -> onItemClick(filmTop) }

    private
    val filmsFiltered1Adapter = FilmsFilteredPagedListAdapter { filmFiltered -> onFilmFilteredItemClick(filmFiltered) }

    //    { filmTop -> onItemClick(filmTop) }
//    private val filmTop250Adapter = FilmsTopPagedListAdapter()
    private
    val filmTop250Adapter =
        FilmsTopPagedListAdapter { filmTop -> onItemClick(filmTop) }

    //    { filmTop -> onItemClick(filmTop) }
    private
    val filmsFiltered2Adapter =
        FilmsFilteredPagedListAdapter { filmFiltered -> onFilmFilteredItemClick(filmFiltered) }

    //    { filmTop -> onItemClick(filmTop) }
    private
    val seriesAdapter =
        FilmsFilteredPagedListAdapter { filmFiltered -> onSerialItemClick(filmFiltered) }

    // TODO: Rename and change types of parameters
    private
    var param1: String? = null

    private
    var param2: String? =
        null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        arguments?.let {
            param1 =
                it.getString(
                    ARG_PARAM1
                )
            param2 =
                it.getString(
                    ARG_PARAM2
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMainBinding.inflate(
                inflater,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        binding.premieresRecycler.adapter =
            filmPremieresAdapter
        binding.popularRecycler.adapter =
            filmTop100PopularAdapter
        binding.filmsFiltered1Recycler.adapter =
            filmsFiltered1Adapter
        binding.top250Recycler.adapter =
            filmTop250Adapter
        binding.filmsFiltered2Recycler.adapter =
            filmsFiltered2Adapter
        binding.serialsRecycler.adapter =
            seriesAdapter

        val application =
            requireContext().applicationContext as App
        binding.filmsFiltered1.text =
            application.filteredFilms1title
        binding.filmsFiltered2.text =
            application.filteredFilms2title

        binding.buttonAllPremieres.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPagePremieresFragment
            )
        }

        binding.buttonAllPopular.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPagePopularFragment
            )
        }

        binding.buttonAllFilmsFiltered1.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageFiltered1Fragment
            )
        }

        binding.buttonAllTop250.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageTop250Fragment
            )
        }

        binding.buttonAllFilmsFiltered2.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageFiltered2Fragment
            )
        }

        binding.buttonAllSeries.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageSeriesFragment
            )
        }

        binding.searchButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_SearchFragment
            )
        }

        binding.profileButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ProfileFragment
            )
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.scrollView.isGone =
                                    true
                                binding.appNameImageLoading.isGone =
                                    false
                                binding.progress.isGone =
                                    false
                                binding.welcome1Image.isGone =
                                    false
                            }
                            ViewModelState.Loaded -> {
                                binding.scrollView.isGone =
                                    false
                                binding.appNameImageLoading.isGone =
                                    true
                                binding.progress.isGone =
                                    true
                                binding.welcome1Image.isGone =
                                    true

                                binding.buttonAllPremieres.isVisible =
                                    viewModel.premieresQuantity > 20
                                viewModel.premieres.onEach {
                                    filmPremieresAdapter.setData(
                                        it
                                    )
                                }
                                    .launchIn(
                                        viewLifecycleOwner.lifecycleScope
                                    )

                                binding.buttonAllPopular.isVisible =
                                    viewModel.top100PopularPagesQuantity > 1
                                viewModel.pagedFilmsTop100Popular.onEach {
                                    filmTop100PopularAdapter.submitData(
                                        it
                                    )
                                }
                                    .launchIn(
                                        viewLifecycleOwner.lifecycleScope
                                    )

                                binding.buttonAllFilmsFiltered1.isVisible =
                                    viewModel.filmsFiltered1PagesQuantity > 1
                                viewModel.pagedFilmsFiltered1.onEach {
                                    filmsFiltered1Adapter.submitData(
                                        it
                                    )
                                }
                                    .launchIn(
                                        viewLifecycleOwner.lifecycleScope
                                    )

                                binding.buttonAllTop250.isVisible =
                                    viewModel.top250PagesQuantity > 1
                                viewModel.pagedFilmsTop250.onEach {
                                    filmTop250Adapter.submitData(
                                        it
                                    )
                                }
                                    .launchIn(
                                        viewLifecycleOwner.lifecycleScope
                                    )

                                binding.buttonAllFilmsFiltered2.isVisible =
                                    viewModel.filmsFiltered2PagesQuantity > 1
                                viewModel.pagedFilmsFiltered2.onEach {
                                    filmsFiltered2Adapter.submitData(
                                        it
                                    )
                                }
                                    .launchIn(
                                        viewLifecycleOwner.lifecycleScope
                                    )

                                binding.buttonAllSeries.isVisible =
                                    viewModel.seriesPagesQuantity > 1
                                viewModel.pagedSeries.onEach {
                                    seriesAdapter.submitData(
                                        it
                                    )
                                }
                                    .launchIn(
                                        viewLifecycleOwner.lifecycleScope
                                    )
                            }
                            ViewModelState.Error -> {
                                binding.scrollView.isGone = true
                                binding.appNameImageLoading.isGone = true
                                binding.progress.isGone = true
                                binding.welcome1Image.isGone = true
                                findNavController().navigate(R.id.action_MainFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }

    private fun onItemClick(
        item: FilmTop
    ) {
        Log.d("FilmVM", "MainFragment. FilmID called = ${item.filmId}")
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_MainFragment_to_FilmFragment,
            bundle
        )
    }

    private fun onPremiereItemClick(
        item: FilmPremiere
    ) {
        Log.d("FilmVM", "MainFragment. FilmID called = ${item.kinopoiskId}")
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.kinopoiskId
                )
            }
        findNavController().navigate(
            R.id.action_MainFragment_to_FilmFragment,
            bundle
        )
    }

    private fun onFilmFilteredItemClick(
        item: FilmFiltered
    ) {
        Log.d("FilmVM", "MainFragment. FilmID called = ${item.kinopoiskId}")
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.kinopoiskId
                )
            }
        findNavController().navigate(
            R.id.action_MainFragment_to_FilmFragment,
            bundle
        )
    }

    private fun onSerialItemClick(
        item: FilmFiltered
    ) {
        Log.d("FilmVM", "MainFragment. FilmID called = ${item.kinopoiskId}")
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.kinopoiskId
                )
            }
        findNavController().navigate(
            R.id.action_MainFragment_to_SerialFragment,
            bundle
        )
    }
}