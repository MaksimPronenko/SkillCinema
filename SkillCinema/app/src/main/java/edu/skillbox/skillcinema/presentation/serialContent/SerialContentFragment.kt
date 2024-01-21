package edu.skillbox.skillcinema.presentation.serialContent

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
import edu.skillbox.skillcinema.presentation.adapters.SerialAdapter
import edu.skillbox.skillcinema.databinding.FragmentSerialContentBinding
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_NAME
import javax.inject.Inject

private const val TAG = "SerialContent.Fragment"

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
            Log.d(TAG, "Сработала setOnCheckedStateChangeListener{}: checkedIds = $checkedIds")
            Log.d(TAG, "setOnCheckedStateChangeListener{}: viewModel.firstSeason = ${viewModel.firstSeason}")
            viewModel.chosenSeason = checkedIds[0] ?: viewModel.firstSeason ?: 1
            refreshRecycler()

            Log.d(TAG, "setOnCheckedStateChangeListener{}: chosenSeason = ${viewModel.chosenSeason}")
        }

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
                                binding.serialName.text = viewModel.name
                                binding.chipSeasonTitle.isGone = true
                                binding.chipScrollView.isGone = true
                                binding.seasonNumberAndEpisodeQuantity.isGone = true

                            }
                            ViewModelState.Loaded -> {
                                Log.d(TAG, "ViewModelState.Loaded")
                                binding.progress.isGone = true
                                binding.serialName.text = viewModel.name
                                binding.chipSeasonTitle.isGone = false
                                binding.chipScrollView.isGone = false
                                binding.seasonNumberAndEpisodeQuantity.isGone = false

                                refreshRecycler()

                                viewModel.seasonsList.forEach { season ->
                                    binding.seasonsFilterGroup.addView(createTagChip(binding.seasonsFilterGroup, season.seasonTable.seasonNumber))
                                    Log.d(TAG, "Создаём чип под номером ${season.seasonTable.seasonNumber}")
                                }
                                binding.seasonsFilterGroup.check(viewModel.firstSeason ?: 1)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createTagChip(chipGroup: ChipGroup, chipNumber: Int): Chip {
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
        chip.id = chipNumber
        chip.text = chipNumber.toString()
        return chip
    }

    private fun refreshRecycler() {
        Log.d(TAG, "Вызвана refreshRecycler(): viewModel.seasonsList.size = ${viewModel.seasonsList.size}")
        if (viewModel.firstSeason != null) {
            val currentSeasonIndex = viewModel.chosenSeason - viewModel.firstSeason!!
            val seasonNumberAndEpisodeQuantityText = "${viewModel.chosenSeason} сезон, ${
                viewModel.episodeQuantityToText(viewModel.seasonsList[currentSeasonIndex].episodes.size)
            }"
            binding.seasonNumberAndEpisodeQuantity.text = seasonNumberAndEpisodeQuantityText

            serialAdapter.setData(viewModel.seasonsList[currentSeasonIndex].episodes)
        }
    }
}