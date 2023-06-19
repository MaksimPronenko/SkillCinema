package edu.skillbox.skillcinema.presentation

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
import edu.skillbox.skillcinema.data.BestFilmsAdapter
import edu.skillbox.skillcinema.databinding.FragmentStaffBinding
import edu.skillbox.skillcinema.models.FilmOfStaff
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Staff.Fragment"

private const val ARG_STAFF_ID = "staffId"

@AndroidEntryPoint
class StaffFragment : Fragment() {

    @Inject
    lateinit var staffViewModelFactory: StaffViewModelFactory
    private val viewModel: StaffViewModel by viewModels { staffViewModelFactory }

    private var _binding: FragmentStaffBinding? = null
    private val binding get() = _binding!!

    private val bestFilmsAdapter =
        BestFilmsAdapter(limited = true) { bestFilm -> onBestFilmItemClick(bestFilm) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val staffId = arguments?.getInt(ARG_STAFF_ID) ?: 0
        Log.d(
            "StaffVM",
            "onCreate Film Fragment. VM.filmId = ${viewModel.staffId}, arguments.filmId = $staffId"
        )
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

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.mainButton.setOnClickListener {
//            findNavController().navigate(R.id.action_StaffFragment_to_MainFragment)
//        }

        binding.buttonAllFilms.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt(
                        "staffId",
                        viewModel.staffId
                    )
                }
            findNavController().navigate(
                R.id.action_StaffFragment_to_AllFilmsOfStaffFragment,
                bundle
            )
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

//                                bestFilmsAdapter.setData(viewModel.detailedFilms)
//                                Log.d(TAG, "Перед addItem() detailedFilms.size: ${viewModel.detailedFilms.size}")
//                                viewModel.personFilmFlow.onEach { film ->
//                                    if (film != null) {
//                                        Log.d(TAG, "Во Flow во фрагменте принят фильм: ${film.filmId}")
//                                        bestFilmsAdapter.addItem(
//                                            item = film
//                                        )
//                                    }
//                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                                viewModel.personFilmsFlow.onEach {
                                    Log.d(TAG, "Во Flow во фрагменте принят список размера: ${it.size}")
                                    bestFilmsAdapter.setData(it)
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

    private fun onBestFilmItemClick(
        item: FilmOfStaff
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.filmId
                )
            }
        findNavController().navigate(
            R.id.action_StaffFragment_to_FilmFragment,
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