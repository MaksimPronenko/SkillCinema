package edu.skillbox.skillcinema.presentation.allInterested

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
import edu.skillbox.skillcinema.presentation.adapters.InterestedAdapter
import edu.skillbox.skillcinema.databinding.FragmentAllInterestedBinding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData
import edu.skillbox.skillcinema.models.person.PersonTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_STAFF_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "AllInterested.Fragment"

@AndroidEntryPoint
class AllInterestedFragment : Fragment() {

    private var _binding: FragmentAllInterestedBinding? = null
    private val binding get() = _binding!!

    private val interestedAdapter = InterestedAdapter(
        limited = false,
        onFilmClick = { filmItemData -> onFilmClick(filmItemData) },
        onSerialClick = { filmItemData -> onSerialClick(filmItemData) },
        onPersonClick = { personTable -> onPersonClick(personTable) },
        clear = { viewModel.clearAllInterested() }
    )

    @Inject
    lateinit var allInterestedViewModelFactory: AllInterestedViewModelFactory
    private val viewModel: AllInterestedViewModel by viewModels {
        allInterestedViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllInterestedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPageRecycler.adapter = interestedAdapter

        Log.d(TAG, "Запускаем viewModel.createAndSendToAdapterInterestedList() из onViewCreated")
        viewModel.createAndSendToAdapterInterestedList()

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
                            else -> {
                                binding.progress.isGone = true
                                viewModel.interestedFlow.onEach {
                                    interestedAdapter.setAdapterData(it)
                                    Log.d(TAG, "interestedAdapter.setData. Размер= ${it.size}")
                                    Log.d(
                                        TAG,
                                        "interestedAdapter.itemCount = ${interestedAdapter.itemCount}"
                                    )
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                        }
                    }
            }
    }

    private fun onFilmClick(
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
            R.id.action_ProfileFragment_to_FilmFragment,
            bundle
        )
    }

    private fun onSerialClick(
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
            R.id.action_ProfileFragment_to_SerialFragment,
            bundle
        )
    }

    private fun onPersonClick(
        item: PersonTable
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_STAFF_ID,
                    item.personId
                )
            }
        findNavController().navigate(
            R.id.action_AllInterestedFragment_to_StaffFragment,
            bundle
        )
    }
}