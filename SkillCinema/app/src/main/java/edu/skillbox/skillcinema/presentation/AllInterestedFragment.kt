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
import edu.skillbox.skillcinema.data.AllStaffFilmsAdapter
import edu.skillbox.skillcinema.databinding.FragmentAllFilmsOfStaffBinding
import edu.skillbox.skillcinema.models.FilmOfStaff
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "AllInterested.Fragment"

@AndroidEntryPoint
class AllInterestedFragment : Fragment() {

    private var _binding: FragmentAllFilmsOfStaffBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter =
        AllStaffFilmsAdapter { film -> onFilmItemClick(film) }

    @Inject
    lateinit var allFilmsOfStaffViewModelFactory: AllFilmsOfStaffViewModelFactory
    private val viewModel: AllFilmsOfStaffViewModel by viewModels {
        allFilmsOfStaffViewModelFactory
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
//            findNavController().navigate(R.id.action_AllFilmsOfStaffFragment_to_MainFragment)
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
                                binding.staffName.text = viewModel.name

                                viewModel.filmsFlow.onEach {
                                    filmsAdapter.setData(it)
                                    Log.d(TAG, "filmsAdapter.setData. Размер= ${it.size}")
                                    Log.d(TAG, "filmsAdapter.itemCount = ${filmsAdapter.itemCount}")
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                                viewModel.loadAllFilmsDetailed()
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                findNavController().navigate(R.id.action_AllFilmsOfStaffFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }
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
            R.id.action_AllFilmsOfStaffFragment_to_FilmFragment,
            bundle
        )
    }
}