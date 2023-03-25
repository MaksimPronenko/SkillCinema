package edu.skillbox.skillcinema.presentation

import android.os.Bundle
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
import edu.skillbox.skillcinema.data.FilmPremieresAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPagePremieresBinding
import edu.skillbox.skillcinema.models.FilmPremiere
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ListPagePremieresFragment : Fragment() {

    private var _binding: FragmentListPagePremieresBinding? = null
    private val binding get() = _binding!!

    private val filmPremieresAdapter =
        FilmPremieresAdapter(limited = false) { filmPremiere -> onPremiereItemClick(filmPremiere) }

    @Inject
    lateinit var listPagePremieresViewModelFactory: ListPagePremieresViewModelFactory
    private val viewModel: ListPagePremieresViewModel by viewModels {
        listPagePremieresViewModelFactory
    }

//    private val viewModel: ListPagePremieresViewModel by viewModels {
//        ListPagePremieresViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPagePremieresBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPageRecycler.adapter = filmPremieresAdapter

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

        binding.mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListPagePremieresFragment_to_MainFragment)
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

                                viewModel.premieres.onEach {
                                    filmPremieresAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

//                                    1 -> {
//                                        viewModel.premieres.onEach {
//                                            filmPremieresAdapter.setData(it)
//                                        }.launchIn(viewLifecycleOwner.lifecycleScope)
//                                    }
//                                    2 -> {
//                                        viewModel.premieres.onEach {
//                                            filmPremieresAdapter.setData(it)
//                                        }.launchIn(viewLifecycleOwner.lifecycleScope)
//                                    }
//                                    3 -> {
//                                        viewModel.premieres.onEach {
//                                            filmPremieresAdapter.setData(it)
//                                        }.launchIn(viewLifecycleOwner.lifecycleScope)
//                                    }
//                                    4 -> {
//                                        viewModel.premieres.onEach {
//                                            filmPremieresAdapter.setData(it)
//                                        }.launchIn(viewLifecycleOwner.lifecycleScope)
//                                    }
//                                    else -> {
//                                        viewModel.premieres.onEach {
//                                            filmPremieresAdapter.setData(it)
//                                        }.launchIn(viewLifecycleOwner.lifecycleScope)
//                                    }
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                            }
                        }
                    }
            }
    }
    private fun onPremiereItemClick(
        item: FilmPremiere
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    "filmId",
                    item.kinopoiskId
                )
            }
        findNavController().navigate(
            R.id.action_ListPagePremieresFragment_to_FilmFragment,
            bundle
        )
    }
}