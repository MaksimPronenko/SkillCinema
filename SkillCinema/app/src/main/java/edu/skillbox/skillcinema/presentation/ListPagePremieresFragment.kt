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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.data.FilmAdapter
import edu.skillbox.skillcinema.databinding.FragmentListPagePremieresBinding
import edu.skillbox.skillcinema.models.FilmItemData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ListPagePremieres.Fragment"

@AndroidEntryPoint
class ListPagePremieresFragment : Fragment() {

    private var _binding: FragmentListPagePremieresBinding? = null
    private val binding get() = _binding!!

    private val filmPremieresAdapter =
        FilmAdapter(limited = false) { filmItemData -> onItemClick(filmItemData) }
//    private val filmPremieresAdapter =
//        FilmPremieresAdapter(limited = false) { filmPremiere -> onPremiereItemClick(filmPremiere) }

    @Inject
    lateinit var listPagePremieresViewModelFactory: ListPagePremieresViewModelFactory
    private val viewModel: ListPagePremieresViewModel by viewModels {
        listPagePremieresViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = false
        bottomNavigation?.menu?.getItem(0)?.isChecked = false
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

        if (viewModel.premieres.isNotEmpty()) {
            viewModel.loadExtendedFilmData()
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
                            }
                            ViewModelState.Loaded -> {
                                binding.progress.isGone = true

                                viewModel.premieresExtendedFlow.onEach {
                                    filmPremieresAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
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
            R.id.action_ListPagePremieresFragment_to_FilmFragment,
            bundle
        )
    }
}