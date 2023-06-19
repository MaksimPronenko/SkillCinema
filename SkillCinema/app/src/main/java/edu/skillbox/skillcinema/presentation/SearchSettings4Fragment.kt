package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.FragmentSearchSettings4Binding
import javax.inject.Inject

private const val TAG = "SearchSettings4.Fragment"

@AndroidEntryPoint
class SearchSettings4Fragment : Fragment() {

    private var _binding: FragmentSearchSettings4Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchSettings4ViewModelFactory: SearchSettings4ViewModelFactory
    private val viewModel: SearchSettings4ViewModel by activityViewModels {
        searchSettings4ViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = true

        _binding = FragmentSearchSettings4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textPeriod1.text = periodText(viewModel.listYearFrom)
        binding.yearFrom1.text = viewModel.listYearFrom[0].toString()
        binding.yearFrom1.isChecked = viewModel.listYearFrom[0] == viewModel.yearFrom
        binding.yearFrom2.text = viewModel.listYearFrom[1].toString()
        binding.yearFrom2.isChecked = viewModel.listYearFrom[1] == viewModel.yearFrom
        binding.yearFrom3.text = viewModel.listYearFrom[2].toString()
        binding.yearFrom3.isChecked = viewModel.listYearFrom[2] == viewModel.yearFrom
        binding.yearFrom4.text = viewModel.listYearFrom[3].toString()
        binding.yearFrom4.isChecked = viewModel.listYearFrom[3] == viewModel.yearFrom
        binding.yearFrom5.text = viewModel.listYearFrom[4].toString()
        binding.yearFrom5.isChecked = viewModel.listYearFrom[4] == viewModel.yearFrom
        binding.yearFrom6.text = viewModel.listYearFrom[5].toString()
        binding.yearFrom6.isChecked = viewModel.listYearFrom[5] == viewModel.yearFrom
        binding.yearFrom7.text = viewModel.listYearFrom[6].toString()
        binding.yearFrom7.isChecked = viewModel.listYearFrom[6] == viewModel.yearFrom
        binding.yearFrom8.text = viewModel.listYearFrom[7].toString()
        binding.yearFrom8.isChecked = viewModel.listYearFrom[7] == viewModel.yearFrom
        binding.yearFrom9.text = viewModel.listYearFrom[8].toString()
        binding.yearFrom9.isChecked = viewModel.listYearFrom[8] == viewModel.yearFrom
        binding.yearFrom10.text = viewModel.listYearFrom[9].toString()
        binding.yearFrom10.isChecked = viewModel.listYearFrom[9] == viewModel.yearFrom
        binding.yearFrom11.text = viewModel.listYearFrom[10].toString()
        binding.yearFrom11.isChecked = viewModel.listYearFrom[10] == viewModel.yearFrom
        binding.yearFrom12.text = viewModel.listYearFrom[11].toString()
        binding.yearFrom12.isChecked = viewModel.listYearFrom[11] == viewModel.yearFrom

        binding.textPeriod2.text = periodText(viewModel.listYearTo)
        binding.yearTo1.text = viewModel.listYearTo[0].toString()
        binding.yearTo1.isChecked = viewModel.listYearTo[0] == viewModel.yearTo
        binding.yearTo2.text = viewModel.listYearTo[1].toString()
        binding.yearTo2.isChecked = viewModel.listYearTo[1] == viewModel.yearTo
        binding.yearTo3.text = viewModel.listYearTo[2].toString()
        binding.yearTo3.isChecked = viewModel.listYearTo[2] == viewModel.yearTo
        binding.yearTo4.text = viewModel.listYearTo[3].toString()
        binding.yearTo4.isChecked = viewModel.listYearTo[3] == viewModel.yearTo
        binding.yearTo5.text = viewModel.listYearTo[4].toString()
        binding.yearTo5.isChecked = viewModel.listYearTo[4] == viewModel.yearTo
        binding.yearTo6.text = viewModel.listYearTo[5].toString()
        binding.yearTo6.isChecked = viewModel.listYearTo[5] == viewModel.yearTo
        binding.yearTo7.text = viewModel.listYearTo[6].toString()
        binding.yearTo7.isChecked = viewModel.listYearTo[6] == viewModel.yearTo
        binding.yearTo8.text = viewModel.listYearTo[7].toString()
        binding.yearTo8.isChecked = viewModel.listYearTo[7] == viewModel.yearTo
        binding.yearTo9.text = viewModel.listYearTo[8].toString()
        binding.yearTo9.isChecked = viewModel.listYearTo[8] == viewModel.yearTo
        binding.yearTo10.text = viewModel.listYearTo[9].toString()
        binding.yearTo10.isChecked = viewModel.listYearTo[9] == viewModel.yearTo
        binding.yearTo11.text = viewModel.listYearTo[10].toString()
        binding.yearTo11.isChecked = viewModel.listYearTo[10] == viewModel.yearTo
        binding.yearTo12.text = viewModel.listYearTo[11].toString()
        binding.yearTo12.isChecked = viewModel.listYearTo[11] == viewModel.yearTo

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.chooseButton.setOnClickListener {
            viewModel.saveChosenPeriod()
            findNavController().navigate(R.id.action_SearchSettings4Fragment_to_SearchSettings1Fragment)
        }

        binding.periodBackButton1.setOnClickListener {
            viewModel.changeListYearFrom(direction = true)
        }

        binding.periodForwardButton1.setOnClickListener {
            viewModel.changeListYearFrom(direction = false)
        }

        binding.periodBackButton2.setOnClickListener {
            viewModel.changeListYearTo(direction = true)
        }

        binding.periodForwardButton2.setOnClickListener {
            viewModel.changeListYearTo(direction = false)
        }

        binding.yearsGroup1.setOnCheckedStateChangeListener { _, _ ->
            if (binding.yearFrom1.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[0])
            else if (binding.yearFrom1.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[0])
            else if (binding.yearFrom2.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[1])
            else if (binding.yearFrom3.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[2])
            else if (binding.yearFrom4.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[3])
            else if (binding.yearFrom5.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[4])
            else if (binding.yearFrom6.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[5])
            else if (binding.yearFrom7.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[6])
            else if (binding.yearFrom8.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[7])
            else if (binding.yearFrom9.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[8])
            else if (binding.yearFrom10.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[9])
            else if (binding.yearFrom11.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[10])
            else if (binding.yearFrom12.isChecked) viewModel.setPeriodFrom(viewModel.listYearFrom[11])
            else viewModel.setPeriodFrom(null)
            binding.chooseButton.isEnabled = viewModel.checkChoiceLogic()
        }

        binding.yearsGroup2.setOnCheckedStateChangeListener { _, _ ->
            if (binding.yearTo1.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[0])
            else if (binding.yearTo2.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[1])
            else if (binding.yearTo3.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[2])
            else if (binding.yearTo4.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[3])
            else if (binding.yearTo5.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[4])
            else if (binding.yearTo6.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[5])
            else if (binding.yearTo7.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[6])
            else if (binding.yearTo8.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[7])
            else if (binding.yearTo9.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[8])
            else if (binding.yearTo10.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[9])
            else if (binding.yearTo11.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[10])
            else if (binding.yearTo12.isChecked) viewModel.setPeriodTo(viewModel.listYearTo[11])
            else viewModel.setPeriodTo(null)
            binding.chooseButton.isEnabled = viewModel.checkChoiceLogic()
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.listYearFromFlow.collect { newListYearFrom ->
                    Log.d(TAG, "newListYearFrom = $newListYearFrom")
                    binding.textPeriod1.text = periodText(newListYearFrom)
                    binding.yearFrom1.text = newListYearFrom[0].toString()
                    binding.yearFrom1.isChecked = newListYearFrom[0] == viewModel.yearFrom
                    binding.yearFrom2.text = newListYearFrom[1].toString()
                    binding.yearFrom2.isChecked = newListYearFrom[1] == viewModel.yearFrom
                    binding.yearFrom3.text = newListYearFrom[2].toString()
                    binding.yearFrom3.isChecked = newListYearFrom[2] == viewModel.yearFrom
                    binding.yearFrom4.text = newListYearFrom[3].toString()
                    binding.yearFrom4.isChecked = newListYearFrom[3] == viewModel.yearFrom
                    binding.yearFrom5.text = newListYearFrom[4].toString()
                    binding.yearFrom5.isChecked = newListYearFrom[4] == viewModel.yearFrom
                    binding.yearFrom6.text = newListYearFrom[5].toString()
                    binding.yearFrom6.isChecked = newListYearFrom[5] == viewModel.yearFrom
                    binding.yearFrom7.text = newListYearFrom[6].toString()
                    binding.yearFrom7.isChecked = newListYearFrom[6] == viewModel.yearFrom
                    binding.yearFrom8.text = newListYearFrom[7].toString()
                    binding.yearFrom8.isChecked = newListYearFrom[7] == viewModel.yearFrom
                    binding.yearFrom9.text = newListYearFrom[8].toString()
                    binding.yearFrom9.isChecked = newListYearFrom[8] == viewModel.yearFrom
                    binding.yearFrom10.text = newListYearFrom[9].toString()
                    binding.yearFrom10.isChecked = newListYearFrom[9] == viewModel.yearFrom
                    binding.yearFrom11.text = newListYearFrom[10].toString()
                    binding.yearFrom11.isChecked = newListYearFrom[10] == viewModel.yearFrom
                    binding.yearFrom12.text = newListYearFrom[11].toString()
                    binding.yearFrom12.isChecked = newListYearFrom[11] == viewModel.yearFrom
                }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.listYearToFlow.collect { newListYearTo ->
                    Log.d(TAG, "newListYearTo = $newListYearTo")
                    binding.textPeriod2.text = periodText(newListYearTo)
                    binding.yearTo1.text = newListYearTo[0].toString()
                    binding.yearTo1.isChecked = newListYearTo[0] == viewModel.yearTo
                    binding.yearTo2.text = newListYearTo[1].toString()
                    binding.yearTo2.isChecked = newListYearTo[1] == viewModel.yearTo
                    binding.yearTo3.text = newListYearTo[2].toString()
                    binding.yearTo3.isChecked = newListYearTo[2] == viewModel.yearTo
                    binding.yearTo4.text = newListYearTo[3].toString()
                    binding.yearTo4.isChecked = newListYearTo[3] == viewModel.yearTo
                    binding.yearTo5.text = newListYearTo[4].toString()
                    binding.yearTo5.isChecked = newListYearTo[4] == viewModel.yearTo
                    binding.yearTo6.text = newListYearTo[5].toString()
                    binding.yearTo6.isChecked = newListYearTo[5] == viewModel.yearTo
                    binding.yearTo7.text = newListYearTo[6].toString()
                    binding.yearTo7.isChecked = newListYearTo[6] == viewModel.yearTo
                    binding.yearTo8.text = newListYearTo[7].toString()
                    binding.yearTo8.isChecked = newListYearTo[7] == viewModel.yearTo
                    binding.yearTo9.text = newListYearTo[8].toString()
                    binding.yearTo9.isChecked = newListYearTo[8] == viewModel.yearTo
                    binding.yearTo10.text = newListYearTo[9].toString()
                    binding.yearTo10.isChecked = newListYearTo[9] == viewModel.yearTo
                    binding.yearTo11.text = newListYearTo[10].toString()
                    binding.yearTo11.isChecked = newListYearTo[10] == viewModel.yearTo
                    binding.yearTo12.text = newListYearTo[11].toString()
                    binding.yearTo12.isChecked = newListYearTo[11] == viewModel.yearTo
                }
            }
    }

    private fun periodText(period: List<Int>): String = "${period.first()} - ${period.last()}"
}