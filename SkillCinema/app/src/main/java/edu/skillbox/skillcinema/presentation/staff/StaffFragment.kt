package edu.skillbox.skillcinema.presentation.staff

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.FragmentStaffBinding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.presentation.adapters.FilmAdapter
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_STAFF_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Staff.Fragment"

@AndroidEntryPoint
class StaffFragment : Fragment() {

    @Inject
    lateinit var staffViewModelFactory: StaffViewModelFactory
    private val viewModel: StaffViewModel by viewModels { staffViewModelFactory }

    private var _binding: FragmentStaffBinding? = null
    private val binding get() = _binding!!

    private val bestFilmsAdapter = FilmAdapter(limited = true,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = { showAllBestFilms() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val staffId = arguments?.getInt(ARG_STAFF_ID) ?: 0
        if (viewModel.staffId == 0 && staffId != 0) {
            viewModel.staffId = staffId
            viewModel.loadPersonData(staffId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bestFilmsRecycler.adapter = bestFilmsAdapter

        Log.d(TAG, "Запускаем viewModel.loadBestFilmsExtended() из onViewCreated")
        viewModel.loadBestFilmsExtended()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonAllFilms.setOnClickListener {
            showAllBestFilms()
        }

        binding.buttonToTheList.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        "staffId",
                        viewModel.staffId
                    )
                }
            findNavController().navigate(
                R.id.action_StaffFragment_to_ListPageFilmographyFragment,
                bundle
            )
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                                binding.staffName.isGone = true
                                binding.staffRole.isGone = true
                                binding.bestTitle.isGone = true
                                binding.buttonAllFilms.isGone = true
                                binding.bestFilmsRecycler.isGone = true
                                binding.filmographyTitle.isGone = true
                                binding.filmographySize.isGone = true
                                binding.buttonToTheList.isGone = true
                                binding.photo.isGone = true
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                binding.staffName.isGone = false
                                binding.staffRole.isGone = false
                                binding.bestTitle.isGone = false
                                binding.buttonAllFilms.isGone = false
                                binding.bestFilmsRecycler.isGone = false
                                binding.filmographyTitle.isGone = false
                                binding.filmographySize.isGone = false
                                binding.buttonToTheList.isGone = false
                                binding.photo.isGone = false

                                Glide
                                    .with(binding.photo.context)
                                    .load(viewModel.photo)
                                    .into(binding.photo)

                                binding.staffName.text = viewModel.name
                                binding.staffRole.text = viewModel.profession
                                binding.filmographySize.text =
                                    filmsQuantityToText(viewModel.filmsQuantity)

                                viewModel.bestFilmsFlow.onEach {
                                    Log.d(
                                        TAG,
                                        "Во Flow во фрагменте принят список размера: ${it.size}"
                                    )
                                    bestFilmsAdapter.setAdapterData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.staffName.isGone = true
                                binding.staffRole.isGone = true
                                binding.bestTitle.isGone = true
                                binding.buttonAllFilms.isGone = true
                                binding.bestFilmsRecycler.isGone = true
                                binding.filmographyTitle.isGone = true
                                binding.filmographySize.isGone = true
                                binding.buttonToTheList.isGone = true
                                binding.photo.isGone = true
                                findNavController().navigate(R.id.action_StaffFragment_to_ErrorBottomFragment)
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
            R.id.action_StaffFragment_to_FilmFragment,
            bundle
        )
    }

    private fun showAllBestFilms() {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_STAFF_ID,
                    viewModel.staffId
                )
            }
        findNavController().navigate(
            R.id.action_StaffFragment_to_AllFilmsOfStaffFragment,
            bundle
        )
    }

    private fun filmsQuantityToText(quantity: Int): String {
        val remOfDivBy10 = quantity % 10
        val remOfDivBy100 = quantity % 100
        return "$quantity фильм" + when (remOfDivBy10) {
            1 -> if (remOfDivBy100 == 11) "ов" else ""
            in 2..4 -> if (remOfDivBy100 == 12) "ов" else "а"
            else -> "ов"
        }
    }
}