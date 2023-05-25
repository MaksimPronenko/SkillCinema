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
import edu.skillbox.skillcinema.models.FilmOfStaff
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Filmography.Fragment"

private const val ARG_STAFF_ID = "staffId"

@AndroidEntryPoint
class ListPageFilmographyFragment : Fragment() {

    private var _binding: FragmentListPageFilmographyBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val actorFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val actressFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val himselfFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val herselfFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val hronoTitrMaleFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val hronoTitrFemaleFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val directorFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val producerFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }
//    private val producerUSSRFilmsAdapter = StaffFilmsAdapter { film -> onFilmItemClick(film) }

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
            if (binding.chipActor.isChecked) {
                viewModel.chosenType = 0
//                binding.listPageRecycler.adapter = actorFilmsAdapter
//                viewModel.actorFilmsFlow.onEach {
//                    Log.d(TAG, "actorFilmsFlow. Принят список размера: ${it.size}")
//                    actorFilmsAdapter.setData(it)
//                    actorFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadActorFilms()
            }
            if (binding.chipActress.isChecked) {
                viewModel.chosenType = 1
//                binding.listPageRecycler.adapter = actressFilmsAdapter
//                viewModel.actressFilmsFlow.onEach {
//                    Log.d(TAG, "actressFilmsFlow. Принят список размера: ${it.size}")
//                    actressFilmsAdapter.setData(it)
//                    actressFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadActressFilms()
            }
            if (binding.chipHimself.isChecked) {
                viewModel.chosenType = 2
//                binding.listPageRecycler.adapter = himselfFilmsAdapter
//                viewModel.himselfFilmsFlow.onEach {
//                    Log.d(TAG, "himselfFilmsFlow. Принят список размера: ${it.size}")
//                    himselfFilmsAdapter.setData(it)
//                    himselfFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadHimselfFilms()
            }
            if (binding.chipHerself.isChecked) {
                viewModel.chosenType = 3
//                binding.listPageRecycler.adapter = herselfFilmsAdapter
//                viewModel.herselfFilmsFlow.onEach {
//                    Log.d(TAG, "herselfFilmsFlow. Принят список размера: ${it.size}")
//                    herselfFilmsAdapter.setData(it)
//                    herselfFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadHerselfFilms()
            }
            if (binding.chipHronoTitrMale.isChecked) {
                viewModel.chosenType = 4
//                binding.listPageRecycler.adapter = hronoTitrMaleFilmsAdapter
//                viewModel.hronoTitrMaleFilmsFlow.onEach {
//                    Log.d(TAG, "hronoTitrMaleFilmsFlow. Принят список размера: ${it.size}")
//                    hronoTitrMaleFilmsAdapter.setData(it)
//                    hronoTitrMaleFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadHronoTitrMaleFilms()
            }
            if (binding.chipHronoTitrFemale.isChecked) {
                viewModel.chosenType = 5
//                binding.listPageRecycler.adapter = hronoTitrFemaleFilmsAdapter
//                viewModel.hronoTitrFemaleFilmsFlow.onEach {
//                    Log.d(TAG, "hronoTitrFemaleFilmsFlow. Принят список размера: ${it.size}")
//                    hronoTitrFemaleFilmsAdapter.setData(it)
//                    hronoTitrFemaleFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadHronoTitrFemaleFilms()
            }
            if (binding.chipDirector.isChecked) {
                viewModel.chosenType = 6
//                binding.listPageRecycler.adapter = directorFilmsAdapter
//                viewModel.directorFilmsFlow.onEach {
//                    Log.d(TAG, "directorFilmsFlow. Принят список размера: ${it.size}")
//                    directorFilmsAdapter.setData(it)
//                    directorFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadDirectorFilms()
            }
            if (binding.chipProducer.isChecked) {
                viewModel.chosenType = 7
//                binding.listPageRecycler.adapter = producerFilmsAdapter
//                viewModel.producerFilmsFlow.onEach {
//                    Log.d(TAG, "producerFilmsFlow. Принят список размера: ${it.size}")
//                    producerFilmsAdapter.setData(it)
//                    producerFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadProducerFilms()
            }
            if (binding.chipProducerUssr.isChecked) {
                viewModel.chosenType = 8
//                binding.listPageRecycler.adapter = producerUSSRFilmsAdapter
//                viewModel.producerUSSRFilmsFlow.onEach {
//                    Log.d(TAG, "producerUSSRFilmsFlow. Принят список размера: ${it.size}")
//                    producerUSSRFilmsAdapter.setData(it)
//                    producerUSSRFilmsAdapter.notifyDataSetChanged()
//                }.launchIn(viewLifecycleOwner.lifecycleScope)
                viewModel.loadProducerUSSRFilms()
            }
            if (binding.chipVoiceDirector.isChecked) {
                viewModel.chosenType = 9
                viewModel.loadVoiceDirectorFilms()
            }
            if (binding.chipWriter.isChecked) {
                viewModel.chosenType = 10
                viewModel.loadWriterFilms()
            }
            if (binding.chipOperator.isChecked) {
                viewModel.chosenType = 11
                viewModel.loadOperatorFilms()
            }
            if (binding.chipEditor.isChecked) {
                viewModel.chosenType = 12
                viewModel.loadEditorFilms()
            }
            if (binding.chipComposer.isChecked) {
                viewModel.chosenType = 13
                viewModel.loadComposerFilms()
            }
            if (binding.chipDesign.isChecked) {
                viewModel.chosenType = 14
                viewModel.loadDesignFilms()
            }
            if (binding.chipTranslator.isChecked) {
                viewModel.chosenType = 15
                viewModel.loadTranslatorFilms()
            }
            if (binding.chipUnknown.isChecked) {
                viewModel.chosenType = 16
                viewModel.loadUnknownFilms()
            }
            Log.d(TAG, "Listener. chosenType = ${viewModel.chosenType}")
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListPageFilmographyFragment_to_MainFragment)
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

//    private fun switchToActorFilms() {
//        viewModel.actorFilmsFlow.onEach {
//            Log.d(TAG, "actorFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadActorFilms()
//    }
//    private fun switchToActressFilms() {
//        viewModel.actressFilmsFlow.onEach {
//            Log.d(TAG, "actressFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadActressFilms()
//    }

//    private fun switchToHimselfFilms() {
//        viewModel.himselfFilmsFlow.onEach {
//            Log.d(TAG, "himselfFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadHimselfFilms()
//    }

//    private fun switchToHerselfFilms() {
//        viewModel.herselfFilmsFlow.onEach {
//            Log.d(TAG, "herselfFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadHerselfFilms()
//    }

//    private fun switchToHronoTitrMaleFilms() {
//        viewModel.hronoTitrMaleFilmsFlow.onEach {
//            Log.d(TAG, "hronoTitrMaleFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadHronoTitrMaleFilms()
//    }

//    private fun switchToHronoTitrFemaleFilms() {
//        viewModel.hronoTitrFemaleFilmsFlow.onEach {
//            Log.d(TAG, "hronoTitrFemaleFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadHronoTitrFemaleFilms()
//    }

//    private fun switchToDirectorFilms() {
//        viewModel.directorFilmsFlow.onEach {
//            Log.d(TAG, "directorFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadDirectorFilms()
//    }

//    private fun switchToProducerFilms() {
//        viewModel.producerFilmsFlow.onEach {
//            Log.d(TAG, "producerFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadProducerFilms()
//    }

//    private fun switchToProducerUSSRFilms() {
//        viewModel.producerUSSRFilmsFlow.onEach {
//            Log.d(TAG, "producerUSSRFilmsFlow. Принят список размера: ${it.size}")
//            filmsAdapter.setData(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewModel.loadProducerUSSRFilms()
//    }

    private fun onFilmItemClick(
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
            R.id.action_ListPageFilmographyFragment_to_FilmFragment,
            bundle
        )
    }
}