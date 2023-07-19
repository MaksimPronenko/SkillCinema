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
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.StaffFilmsAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPageFilmographyBinding
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Filmography.Fragment"

private const val ARG_STAFF_ID = "staffId"

@AndroidEntryPoint
class ListPageFilmographyFragment : Fragment() {

    private var _binding: FragmentListPageFilmographyBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter = StaffFilmsAdapter{ filmItemData -> onItemClick(filmItemData) }

    @Inject
    lateinit var listPageFilmographyViewModelFactory: ListPageFilmographyViewModelFactory
    private val viewModel: ListPageFilmographyViewModel by viewModels {
        listPageFilmographyViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val staffId = arguments?.getInt(ARG_STAFF_ID) ?: 0
        if (viewModel.staffId == 0 && staffId != 0) {
            viewModel.staffId = staffId
            viewModel.loadPersonData(staffId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPageFilmographyBinding.inflate(inflater, container, false)

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

        binding.filmsFilterGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.chipActor.isChecked) viewModel.loadFilms(0)
            if (binding.chipActress.isChecked) viewModel.loadFilms(1)
            if (binding.chipHimself.isChecked) viewModel.loadFilms(2)
            if (binding.chipHerself.isChecked) viewModel.loadFilms(3)
            if (binding.chipHronoTitrMale.isChecked) viewModel.loadFilms(4)
            if (binding.chipHronoTitrFemale.isChecked) viewModel.loadFilms(5)
            if (binding.chipDirector.isChecked) viewModel.loadFilms(6)
            if (binding.chipProducer.isChecked) viewModel.loadFilms(7)
            if (binding.chipProducerUssr.isChecked) viewModel.loadFilms(8)
            if (binding.chipVoiceDirector.isChecked) viewModel.loadFilms(9)
            if (binding.chipWriter.isChecked) viewModel.loadFilms(10)
            if (binding.chipOperator.isChecked) viewModel.loadFilms(11)
            if (binding.chipEditor.isChecked) viewModel.loadFilms(12)
            if (binding.chipComposer.isChecked) viewModel.loadFilms(13)
            if (binding.chipDesign.isChecked) viewModel.loadFilms(14)
            if (binding.chipTranslator.isChecked) viewModel.loadFilms(15)
            if (binding.chipUnknown.isChecked) viewModel.loadFilms(16)
            Log.d(TAG, "Listener. chosenType = ${viewModel.chosenType}")
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
                                binding.staffName.isGone = true
                                binding.chipScrollView.isGone = true
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true
                                binding.staffName.isGone = false
                                binding.staffName.text = viewModel.name
                                binding.chipScrollView.isGone = false

                                if (viewModel.quantityActorFilms > 0) {
                                    binding.chipActor.isGone = false
                                    binding.chipActor.text =
                                        getString(R.string.chip_actor) + "  " + viewModel.quantityActorFilms
                                } else {
                                    binding.chipActor.isGone = true
                                }

                                if (viewModel.quantityActressFilms > 0) {
                                    binding.chipActress.isGone = false
                                    binding.chipActress.text =
                                        getString(R.string.chip_actress) + "  " + viewModel.quantityActressFilms
                                } else {
                                    binding.chipActress.isGone = true
                                }

                                if (viewModel.quantityHimselfFilms > 0) {
                                    binding.chipHimself.isGone = false
                                    binding.chipHimself.text =
                                        getString(R.string.chip_himself) + "  " + viewModel.quantityHimselfFilms
                                } else {
                                    binding.chipHimself.isGone = true
                                }

                                if (viewModel.quantityHerselfFilms > 0) {
                                    binding.chipHerself.isGone = false
                                    binding.chipHerself.text =
                                        getString(R.string.chip_herself) + "  " + viewModel.quantityHerselfFilms
                                } else {
                                    binding.chipHerself.isGone = true
                                }

                                if (viewModel.quantityHronoTitrMaleFilms > 0) {
                                    binding.chipHronoTitrMale.isGone = false
                                    binding.chipHronoTitrMale.text =
                                        getString(R.string.chip_hrono_titr_male) + "  " + viewModel.quantityHronoTitrMaleFilms
                                } else {
                                    binding.chipHronoTitrMale.isGone = true
                                }

                                if (viewModel.quantityHronoTitrFemaleFilms > 0) {
                                    binding.chipHronoTitrFemale.isGone = false
                                    binding.chipHronoTitrFemale.text =
                                        getString(R.string.chip_hrono_titr_female) + "  " + viewModel.quantityHronoTitrFemaleFilms
                                } else {
                                    binding.chipHronoTitrFemale.isGone = true
                                }

                                if (viewModel.quantityDirectorFilms > 0) {
                                    binding.chipDirector.isGone = false
                                    binding.chipDirector.text =
                                        getString(R.string.chip_director) + "  " + viewModel.quantityDirectorFilms
                                } else {
                                    binding.chipDirector.isGone = true
                                }

                                if (viewModel.quantityProducerFilms > 0) {
                                    binding.chipProducer.isGone = false
                                    binding.chipProducer.text =
                                        getString(R.string.chip_producer) + "  " + viewModel.quantityProducerFilms
                                } else {
                                    binding.chipProducer.isGone = true
                                }

                                if (viewModel.quantityProducerUSSRFilms > 0) {
                                    binding.chipProducerUssr.isGone = false
                                    binding.chipProducerUssr.text =
                                        getString(R.string.chip_producer_ussr) + "  " + viewModel.quantityProducerUSSRFilms
                                } else {
                                    binding.chipProducerUssr.isGone = true
                                }

                                if (viewModel.quantityVoiceDirectorFilms > 0) {
                                    binding.chipVoiceDirector.isGone = false
                                    binding.chipVoiceDirector.text =
                                        getString(R.string.chip_voice_director) + "  " + viewModel.quantityVoiceDirectorFilms
                                } else {
                                    binding.chipVoiceDirector.isGone = true
                                }

                                if (viewModel.quantityWriterFilms > 0) {
                                    binding.chipWriter.isGone = false
                                    binding.chipWriter.text =
                                        getString(R.string.chip_writer) + "  " + viewModel.quantityWriterFilms
                                } else {
                                    binding.chipWriter.isGone = true
                                }

                                if (viewModel.quantityOperatorFilms > 0) {
                                    binding.chipOperator.isGone = false
                                    binding.chipOperator.text =
                                        getString(R.string.chip_operator) + "  " + viewModel.quantityOperatorFilms
                                } else {
                                    binding.chipOperator.isGone = true
                                }

                                if (viewModel.quantityEditorFilms > 0) {
                                    binding.chipEditor.isGone = false
                                    binding.chipEditor.text =
                                        getString(R.string.chip_editor) + "  " + viewModel.quantityEditorFilms
                                } else {
                                    binding.chipEditor.isGone = true
                                }

                                if (viewModel.quantityComposerFilms > 0) {
                                    binding.chipComposer.isGone = false
                                    binding.chipComposer.text =
                                        getString(R.string.chip_composer) + "  " + viewModel.quantityComposerFilms
                                } else {
                                    binding.chipComposer.isGone = true
                                }

                                if (viewModel.quantityDesignFilms > 0) {
                                    binding.chipDesign.isGone = false
                                    binding.chipDesign.text =
                                        getString(R.string.chip_design) + "  " + viewModel.quantityDesignFilms
                                } else {
                                    binding.chipDesign.isGone = true
                                }

                                if (viewModel.quantityTranslatorFilms > 0) {
                                    binding.chipTranslator.isGone = false
                                    binding.chipTranslator.text =
                                        getString(R.string.chip_translator) + "  " + viewModel.quantityTranslatorFilms
                                } else {
                                    binding.chipTranslator.isGone = true
                                }

                                if (viewModel.quantityUnknownFilms > 0) {
                                    binding.chipUnknown.isGone = false
                                    binding.chipUnknown.text =
                                        getString(R.string.chip_unknown) + "  " + viewModel.quantityUnknownFilms
                                } else {
                                    binding.chipUnknown.isGone = true
                                }

                                viewModel.filmsFlow.onEach {
                                    filmsAdapter.setData(it)
                                    Log.d(TAG, "filmsAdapter.setData. Размер= ${it.size}")
                                    Log.d(TAG, "filmsAdapter.itemCount = ${filmsAdapter.itemCount}")
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                                when (viewModel.chosenType) {
                                    0 -> binding.chipActor.isChecked = true
                                    1 -> binding.chipActress.isChecked = true
                                    2 -> binding.chipHimself.isChecked = true
                                    3 -> binding.chipHerself.isChecked = true
                                    4 -> binding.chipHronoTitrMale.isChecked = true
                                    5 -> binding.chipHronoTitrFemale.isChecked = true
                                    6 -> binding.chipDirector.isChecked = true
                                    7 -> binding.chipProducer.isChecked = true
                                    8 -> binding.chipProducerUssr.isChecked = true
                                    9 -> binding.chipVoiceDirector.isChecked = true
                                    10 -> binding.chipWriter.isChecked = true
                                    11 -> binding.chipOperator.isChecked = true
                                    12 -> binding.chipEditor.isChecked = true
                                    13 -> binding.chipComposer.isChecked = true
                                    14 -> binding.chipDesign.isChecked = true
                                    15 -> binding.chipTranslator.isChecked = true
                                    16 -> binding.chipUnknown.isChecked = true
                                }
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                binding.staffName.isGone = true
                                binding.chipScrollView.isGone = true
                                findNavController().navigate(R.id.action_ListPageFilmographyFragment_to_ErrorBottomFragment)
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
            R.id.action_ListPageFilmographyFragment_to_FilmFragment,
            bundle
        )
    }
}