package edu.skillbox.skillcinema.presentation.allStaff

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
import edu.skillbox.skillcinema.presentation.adapters.AllStaffAdapter
import edu.skillbox.skillcinema.databinding.FragmentAllStaffBinding
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import edu.skillbox.skillcinema.utils.ARG_FILM_ID
import edu.skillbox.skillcinema.utils.ARG_STAFF_ID
import javax.inject.Inject

private const val ARG_FILM_NAME = "filmName"
private const val ARG_STAFF_TYPE = "staffType" // false - актёры; true - остальной персонал

@AndroidEntryPoint
class AllStaffFragment : Fragment() {

    private var _binding: FragmentAllStaffBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter = AllStaffAdapter { staff -> onStaffItemClick(staff) }

    @Inject
    lateinit var allStaffViewModelFactory: AllStaffViewModelFactory
    private val viewModel: AllStaffViewModel by viewModels {
        allStaffViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmId = arguments?.getInt(ARG_FILM_ID) ?: 0
        val filmName = arguments?.getString(ARG_FILM_NAME) ?: ""
        val staffType = arguments?.getBoolean(ARG_STAFF_TYPE) ?: false
        if (viewModel.filmId == 0 && filmId != 0) {
            viewModel.filmId = filmId
            viewModel.filmName = filmName
            viewModel.staffType = staffType
            viewModel.loadStaff()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllStaffBinding.inflate(inflater, container, false)

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
                                binding.filmName.text = viewModel.filmName

                                if (!viewModel.staffType) {
                                    binding.listName.text = getString(R.string.actors_recycler_title)
                                    filmsAdapter.setData(viewModel.actorsList)
                                } else {
                                    binding.listName.text = getString(R.string.staff_recycler_title)
                                    filmsAdapter.setData(viewModel.staffList)
                                }
                            }
                            ViewModelState.Error -> {
                                binding.progress.isGone = true
                                findNavController().navigate(R.id.action_AllStaffFragment_to_ErrorBottomFragment)
                            }
                        }
                    }
            }
    }

    private fun onStaffItemClick(
        item: StaffTable
    ) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_STAFF_ID,
                    item.staffId
                )
            }
        findNavController().navigate(
            R.id.action_AllStaffFragment_to_StaffFragment,
            bundle
        )
    }
}