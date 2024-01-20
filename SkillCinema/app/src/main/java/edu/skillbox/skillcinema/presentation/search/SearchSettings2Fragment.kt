package edu.skillbox.skillcinema.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import edu.skillbox.skillcinema.databinding.FragmentSearchSettings2Binding
import javax.inject.Inject

@AndroidEntryPoint
class SearchSettings2Fragment : Fragment() {

    private var _binding: FragmentSearchSettings2Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchSettings2ViewModelFactory: SearchSettings2ViewModelFactory
    private val viewModel: SearchSettings2ViewModel by activityViewModels {
        searchSettings2ViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = true

        _binding = FragmentSearchSettings2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchCountryField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                viewModel.searchCountry(s.toString())
            }
        })
        binding.searchCountryField.setText(viewModel.countrySearchText)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            SearchViewModelState.Searching -> {
                                binding.searchFailed.isGone = true
                                binding.chipScrollView.isGone = true
                            }
                            SearchViewModelState.SearchSuccessfull -> {
                                binding.searchFailed.isGone = true
                                binding.chipScrollView.isGone = false

                                binding.russia.isGone =
                                    viewModel.countryChipsActive["Россия"] != true
                                binding.russia.isChecked = viewModel.chosenCountryCode == 34

                                binding.ussr.isGone =
                                    viewModel.countryChipsActive["СССР"] != true
                                binding.ussr.isChecked = viewModel.chosenCountryCode == 33

                                binding.usa.isGone =
                                    viewModel.countryChipsActive["США"] != true
                                binding.usa.isChecked = viewModel.chosenCountryCode == 1

                                binding.france.isGone =
                                    viewModel.countryChipsActive["Франция"] != true
                                binding.france.isChecked = viewModel.chosenCountryCode == 3

                                binding.greatBritain.isGone =
                                    viewModel.countryChipsActive["Великобритания"] != true
                                binding.greatBritain.isChecked = viewModel.chosenCountryCode == 5

                                binding.germany.isGone =
                                    viewModel.countryChipsActive["Германия"] != true
                                binding.germany.isChecked = viewModel.chosenCountryCode == 9

                                binding.germanyFrg.isGone =
                                    viewModel.countryChipsActive["Германия (ФРГ)"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 12

                                binding.italy.isGone =
                                    viewModel.countryChipsActive["Италия"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 10

                                binding.japan.isGone =
                                    viewModel.countryChipsActive["Япония"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 16

                                binding.china.isGone =
                                    viewModel.countryChipsActive["Китай"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 21

                                binding.india.isGone =
                                    viewModel.countryChipsActive["Индия"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 7

                                binding.australia.isGone =
                                    viewModel.countryChipsActive["Австралия"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 13

                                binding.canada.isGone =
                                    viewModel.countryChipsActive["Канада"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 14

                                binding.spain.isGone =
                                    viewModel.countryChipsActive["Испания"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 8

                                binding.mexico.isGone =
                                    viewModel.countryChipsActive["Мексика"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 15

                                binding.switzerland.isGone =
                                    viewModel.countryChipsActive["Швейцария"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 2

                                binding.poland.isGone =
                                    viewModel.countryChipsActive["Польша"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 4

                                binding.sweden.isGone =
                                    viewModel.countryChipsActive["Швеция"] != true
                                binding.germanyFrg.isChecked = viewModel.chosenCountryCode == 6

                                binding.countriesGroup.setOnCheckedStateChangeListener { _, _ ->
                                    if (binding.russia.isChecked) viewModel.setAndSaveCountry(34)
                                    if (binding.ussr.isChecked) viewModel.setAndSaveCountry(33)
                                    if (binding.usa.isChecked) viewModel.setAndSaveCountry(1)
                                    if (binding.france.isChecked) viewModel.setAndSaveCountry(3)
                                    if (binding.greatBritain.isChecked) viewModel.setAndSaveCountry(5)
                                    if (binding.germany.isChecked) viewModel.setAndSaveCountry(9)
                                    if (binding.germanyFrg.isChecked) viewModel.setAndSaveCountry(12)
                                    if (binding.italy.isChecked) viewModel.setAndSaveCountry(10)
                                    if (binding.japan.isChecked) viewModel.setAndSaveCountry(16)
                                    if (binding.china.isChecked) viewModel.setAndSaveCountry(21)
                                    if (binding.india.isChecked) viewModel.setAndSaveCountry(7)
                                    if (binding.australia.isChecked) viewModel.setAndSaveCountry(13)
                                    if (binding.canada.isChecked) viewModel.setAndSaveCountry(14)
                                    if (binding.spain.isChecked) viewModel.setAndSaveCountry(8)
                                    if (binding.mexico.isChecked) viewModel.setAndSaveCountry(15)
                                    if (binding.switzerland.isChecked) viewModel.setAndSaveCountry(2)
                                    if (binding.poland.isChecked) viewModel.setAndSaveCountry(4)
                                    if (binding.sweden.isChecked) viewModel.setAndSaveCountry(6)
                                    findNavController().navigate(R.id.action_SearchSettings2Fragment_to_SearchSettings1Fragment)
                                }
                            }
                            else -> {
                                binding.searchFailed.isGone = false
                                binding.chipScrollView.isGone = true
                            }
                        }
                    }
            }
    }
}