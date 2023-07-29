package edu.skillbox.skillcinema.presentation.main

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.FragmentMainBinding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.presentation.adapters.FilmAdapter
import edu.skillbox.skillcinema.utils.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Main.Fragment"

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by activityViewModels {
        mainViewModelFactory
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val filmPremieresAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_MainFragment_to_ListPagePremieresFragment) }
    )

    private val filmTop100PopularExtendedAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_MainFragment_to_ListPagePopularFragment) }
    )

    private val filmsFiltered1Adapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_MainFragment_to_ListPageFiltered1Fragment) }
    )

    private val filmTop250ExtendedAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_MainFragment_to_ListPageTop250Fragment) }
    )

    private val filmsFiltered2Adapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_MainFragment_to_ListPageFiltered2Fragment) }
    )

    private val serialsAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onSerialItemClick(filmItemData) },
        showAll = { findNavController().navigate(R.id.action_MainFragment_to_ListPageSerialsFragment) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false

        _binding = FragmentMainBinding.inflate(inflater, container, false)
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

        binding.premieresRecycler.adapter = filmPremieresAdapter
        binding.popularRecycler.adapter = filmTop100PopularExtendedAdapter
        binding.filmsFiltered1Recycler.adapter = filmsFiltered1Adapter
        binding.top250Recycler.adapter = filmTop250ExtendedAdapter
        binding.filmsFiltered2Recycler.adapter = filmsFiltered2Adapter
        binding.serialsRecycler.adapter = serialsAdapter

        val application = requireContext().applicationContext as App
        binding.filmsFiltered1.text = application.filteredFilms1title
        binding.filmsFiltered2.text = application.filteredFilms2title

        if (viewModel.top100Popular != null && viewModel.top250 != null &&
            viewModel.serials != null && viewModel.filmsFiltered1 != null &&
            viewModel.filmsFiltered2 != null
        ) {
            viewModel.loadExtendedFilmData()
        }

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
            val bundle =
                Bundle().apply {
                    putInt(
                        ARG_GENRE_1_KEY,
                        viewModel.genre1Key
                    )
                    putInt(
                        ARG_COUNTRY_1_KEY,
                        viewModel.country1Key
                    )
                }
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageFiltered1Fragment,
                bundle
            )
        }

        binding.buttonAllTop250.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageTop250Fragment
            )
        }

        binding.buttonAllFilmsFiltered2.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        ARG_GENRE_2_KEY,
                        viewModel.genre2Key
                    )
                    putInt(
                        ARG_COUNTRY_2_KEY,
                        viewModel.country2Key
                    )
                }
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageFiltered2Fragment,
                bundle
            )
        }

        binding.buttonAllSeries.setOnClickListener {
            findNavController().navigate(
                R.id.action_MainFragment_to_ListPageSerialsFragment
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state
                .collect { state ->
                    when (state) {
                        ViewModelState.Loading -> {
                            binding.scrollView.isGone = true
                            binding.appNameImageLoading.isGone = false
                            binding.progress.isGone = false
                            binding.welcome1Image.isGone = false
                        }
                        ViewModelState.Loaded -> {
                            binding.scrollView.isGone = false
                            binding.appNameImageLoading.isGone = true
                            binding.progress.isGone = true
                            binding.welcome1Image.isGone = true

                            binding.buttonAllPremieres.isVisible = viewModel.premieresQuantity > 20
                            viewModel.premieresExtendedFlow.onEach {
                                filmPremieresAdapter.setAdapterData(it)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)

                            binding.buttonAllPopular.isVisible =
                                viewModel.top100PopularPagesQuantity > 1
                            viewModel.top100PopularExtendedFlow.onEach {
                                filmTop100PopularExtendedAdapter.setAdapterData(it)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)

                            binding.buttonAllFilmsFiltered1.isVisible =
                                viewModel.filmsFiltered1PagesQuantity > 1
                            viewModel.filmsFiltered1ExtendedFlow.onEach {
                                filmsFiltered1Adapter.setAdapterData(it)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)

                            binding.buttonAllTop250.isVisible =
                                viewModel.top250PagesQuantity > 1
                            viewModel.top250ExtendedFlow.onEach {
                                filmTop250ExtendedAdapter.setAdapterData(it)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)

                            binding.buttonAllFilmsFiltered2.isVisible =
                                viewModel.filmsFiltered2PagesQuantity > 1
                            viewModel.filmsFiltered2ExtendedFlow.onEach {
                                filmsFiltered2Adapter.setAdapterData(it)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)

                            binding.buttonAllSeries.isVisible =
                                viewModel.serialsPagesQuantity > 1
                            viewModel.serialsExtendedFlow.onEach {
                                serialsAdapter.setAdapterData(it)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)
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
        item: FilmItemData
    ) {
        Log.d(TAG, "onItemClick(${item.filmId})")
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_FILM_ID,
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_MainFragment_to_FilmFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSerialItemClick(
        item: FilmItemData
    ) {
        Log.d(TAG, "onSerialItemClick(${item.filmId})")
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_FILM_ID,
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_MainFragment_to_SerialFragment,
            bundle
        )
    }
}