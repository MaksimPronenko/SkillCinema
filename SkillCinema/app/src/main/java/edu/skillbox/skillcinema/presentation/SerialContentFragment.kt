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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.SerialAdapter
import edu.skillbox.skillcinema.databinding.FragmentSerialContentBinding
import javax.inject.Inject

private const val TAG = "SerialContentFragment"

private const val ARG_FILM_ID = "filmId"
private const val ARG_NAME = "name"

@AndroidEntryPoint
class SerialContentFragment : Fragment() {

    private var _binding: FragmentSerialContentBinding? = null
    private val binding get() = _binding!!

    private val serialAdapter = SerialAdapter()

    @Inject
    lateinit var serialContentViewModelFactory: SerialContentViewModelFactory
    private val viewModel: SerialContentViewModel by viewModels { serialContentViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.name = arguments?.getString(ARG_NAME) ?: ""
            viewModel.loadSerialInfo(filmId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSerialContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (context != null)
            Log.d(TAG, "onViewCreated. Контекст != null")
        else
            Log.d(TAG, "onViewCreated. Контекст = null")

        binding.episodesRecycler.adapter = serialAdapter

        val dividerItemDecorationVertical = DividerItemDecoration(context, RecyclerView.VERTICAL)
        context?.let { ContextCompat.getDrawable(it, R.drawable.divider_serial) }
            ?.let {
                dividerItemDecorationVertical.setDrawable(it)
            }
        binding.episodesRecycler.addItemDecoration(dividerItemDecorationVertical)

        binding.seasonsFilterGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            viewModel.chosenSeason = checkedIds[0] ?: 1
            refreshRecycler()

            Log.d(TAG, "chosenSeason = ${viewModel.chosenSeason}")
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_SerialContentFragment_to_MainFragment)
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                binding.progress.isGone = false
                                binding.serialName.text = viewModel.name
                                binding.chipSeasonTitle.isGone = true
                                binding.chipScrollView.isGone = true
                                binding.seasonNumberAndEpisodeQuantity.isGone = true

                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                binding.serialName.text = viewModel.name
                                binding.chipSeasonTitle.isGone = false
                                binding.chipScrollView.isGone = false
                                binding.seasonNumberAndEpisodeQuantity.isGone = false

                                refreshRecycler()

                                viewModel.seasonsList.forEach { season ->
                                    binding.seasonsFilterGroup.addView(createTagChip(binding.seasonsFilterGroup, season.number))
                                    Log.d(TAG, "Создаём чип под номером ${season.number}")
                                }
                                binding.seasonsFilterGroup.check(viewModel.chosenSeason)
                                Log.d(TAG, "onViewCreated, Loaded. Размер списка чипов = ${viewModel.seasonsList.size}")
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.serialName.text = viewModel.name
                                binding.chipSeasonTitle.isGone = true
                                binding.chipScrollView.isGone = true
                                binding.seasonNumberAndEpisodeQuantity.isGone = true
                            }
                        }
                    }
            }
    }

    private fun createTagChip(chipGroup: ChipGroup, chipNumber: Int): Chip {
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
        chip.id = chipNumber
        chip.text = chipNumber.toString()
        return chip

//        return Chip(context).apply {
//            id = chipNumber
//            text = chipNumber.toString()
//            setTextAppearance(R.style.TextMainStyle_ChipStyle)
//            isCloseIconVisible = false
//            setChipBackgroundColorResource(R.color.chip_background)
//            setChipStrokeColorResource(R.color.chip_stroke)
//            chipStrokeWidth = 1F

//            if (id == 1) check(true)
//            setTextColor(ContextCompat.getColor(context, R.color.white))
//        }
    }

    private fun refreshRecycler() {
        binding.seasonNumberAndEpisodeQuantity.text =
            "${viewModel.chosenSeason} сезон, ${
                viewModel.episodeQuantityToText(viewModel.seasonsList[viewModel.chosenSeason - 1].episodes.size)
            }"
        serialAdapter.setData(viewModel.seasonsList[viewModel.chosenSeason - 1].episodes)
    }
}