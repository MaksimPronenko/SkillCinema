package edu.skillbox.skillcinema.presentation.allFilmsOfStaff

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
import edu.skillbox.skillcinema.databinding.FragmentAllFilmsOfStaffBinding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.presentation.adapters.FilmAdapter
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_STAFF_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "AllFilmsOfStaff.Fragment"

@AndroidEntryPoint
class AllFilmsOfStaffFragment : Fragment() {

    private var _binding: FragmentAllFilmsOfStaffBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter = FilmAdapter(limited = false,
        onClick = { filmItemData -> onItemClick(filmItemData) },
        showAll = {}
    )

    @Inject
    lateinit var allFilmsOfStaffViewModelFactory: AllFilmsOfStaffViewModelFactory
    private val viewModel: AllFilmsOfStaffViewModel by viewModels {
        allFilmsOfStaffViewModelFactory
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
        _binding = FragmentAllFilmsOfStaffBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPageRecycler.adapter = filmsAdapter

        Log.d(TAG, "Запускаем viewModel.loadFilmsExtended() из onViewCreated")
        viewModel.loadFilmsExtended()

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
                                binding.staffName.text = viewModel.name

                                viewModel.filmsFlow.onEach {
                                    filmsAdapter.setAdapterData(it)
                                    Log.d(TAG, "filmsAdapter.setData. Размер= ${it.size}")
                                    Log.d(TAG, "filmsAdapter.itemCount = ${filmsAdapter.itemCount}")
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                findNavController().navigate(R.id.action_AllFilmsOfStaffFragment_to_ErrorBottomFragment)
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
            R.id.action_AllFilmsOfStaffFragment_to_FilmFragment,
            bundle
        )
    }
}