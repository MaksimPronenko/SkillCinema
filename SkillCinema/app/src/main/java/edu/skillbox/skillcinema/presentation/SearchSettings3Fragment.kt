package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import edu.skillbox.skillcinema.databinding.FragmentSearchSettings3Binding
import javax.inject.Inject

private const val TAG = "SearchSettings3.Fragment"

@AndroidEntryPoint
class SearchSettings3Fragment : Fragment() {

    private var _binding: FragmentSearchSettings3Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchSettings3ViewModelFactory: SearchSettings3ViewModelFactory
    private val viewModel: SearchSettings3ViewModel by activityViewModels {
        searchSettings3ViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = true

        _binding = FragmentSearchSettings3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchGenreField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                Log.d(TAG, "Фиксировано изменение текста")
                viewModel.searchGenre(s.toString())
            }
        })
        binding.searchGenreField.setText(viewModel.genreSearchText)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            SearchViewModelState.Searching -> {
                                Log.d(TAG, "Перешли в состояние поиска")
                                binding.searchFailed.isGone = true
                                binding.chipScrollView.isGone = true
                            }
                            SearchViewModelState.SearchSuccessfull -> {
                                Log.d(TAG, "Перешли в состояние удачного поиска")
                                Log.d(TAG, viewModel.genreChipsActive.toString())
                                binding.searchFailed.isGone = true
                                binding.chipScrollView.isGone = false

                                binding.detective.isGone =
                                    viewModel.genreChipsActive["Детектив"] != true
                                binding.detective.isChecked = viewModel.chosenGenreCode == 5

                                binding.thriller.isGone =
                                    viewModel.genreChipsActive["Триллер"] != true
                                binding.thriller.isChecked = viewModel.chosenGenreCode == 1

                                binding.actionMovie.isGone =
                                    viewModel.genreChipsActive["Боевик"] != true
                                binding.actionMovie.isChecked = viewModel.chosenGenreCode == 11

                                binding.crimeFilm.isGone =
                                    viewModel.genreChipsActive["Криминальный фильм"] != true
                                binding.crimeFilm.isChecked = viewModel.chosenGenreCode == 3

                                binding.drama.isGone =
                                    viewModel.genreChipsActive["Драма"] != true
                                binding.drama.isChecked = viewModel.chosenGenreCode == 2

                                binding.melodrama.isGone =
                                    viewModel.genreChipsActive["Мелодрама"] != true
                                binding.melodrama.isChecked = viewModel.chosenGenreCode == 4

                                binding.familyMovie.isGone =
                                    viewModel.genreChipsActive["Семейный фильм"] != true
                                binding.familyMovie.isChecked = viewModel.chosenGenreCode == 19

                                binding.comedy.isGone =
                                    viewModel.genreChipsActive["Комедия"] != true
                                binding.comedy.isChecked = viewModel.chosenGenreCode == 13

                                binding.cartoon.isGone =
                                    viewModel.genreChipsActive["Мультфильм"] != true
                                binding.cartoon.isChecked = viewModel.chosenGenreCode == 18

                                binding.childrenFilm.isGone =
                                    viewModel.genreChipsActive["Детский фильм"] != true
                                binding.childrenFilm.isChecked = viewModel.chosenGenreCode == 33

                                binding.fantasy.isGone =
                                    viewModel.genreChipsActive["Фэнтези"] != true
                                binding.fantasy.isChecked = viewModel.chosenGenreCode == 12

                                binding.scienceFiction.isGone =
                                    viewModel.genreChipsActive["Фантастика"] != true
                                binding.scienceFiction.isChecked = viewModel.chosenGenreCode == 6

                                binding.horrorMovie.isGone =
                                    viewModel.genreChipsActive["Ужасы"] != true
                                binding.horrorMovie.isChecked = viewModel.chosenGenreCode == 17

                                binding.warFilm.isGone =
                                    viewModel.genreChipsActive["Военный фильм"] != true
                                binding.warFilm.isChecked = viewModel.chosenGenreCode == 14

                                binding.historicalFilm.isGone =
                                    viewModel.genreChipsActive["Исторический фильм"] != true
                                binding.historicalFilm.isChecked = viewModel.chosenGenreCode == 15

                                binding.genresGroup.setOnCheckedStateChangeListener { _, _ ->
                                    if (binding.detective.isChecked) viewModel.setAndSaveGenre(5)
                                    if (binding.thriller.isChecked) viewModel.setAndSaveGenre(1)
                                    if (binding.actionMovie.isChecked) viewModel.setAndSaveGenre(11)
                                    if (binding.crimeFilm.isChecked) viewModel.setAndSaveGenre(3)
                                    if (binding.drama.isChecked) viewModel.setAndSaveGenre(2)
                                    if (binding.melodrama.isChecked) viewModel.setAndSaveGenre(4)
                                    if (binding.familyMovie.isChecked) viewModel.setAndSaveGenre(19)
                                    if (binding.comedy.isChecked) viewModel.setAndSaveGenre(13)
                                    if (binding.cartoon.isChecked) viewModel.setAndSaveGenre(18)
                                    if (binding.childrenFilm.isChecked) viewModel.setAndSaveGenre(33)
                                    if (binding.fantasy.isChecked) viewModel.setAndSaveGenre(12)
                                    if (binding.scienceFiction.isChecked) viewModel.setAndSaveGenre(6)
                                    if (binding.horrorMovie.isChecked) viewModel.setAndSaveGenre(17)
                                    if (binding.warFilm.isChecked) viewModel.setAndSaveGenre(14)
                                    if (binding.historicalFilm.isChecked) viewModel.setAndSaveGenre(15)
                                    Log.d(TAG, "Listener. chosenType = ${viewModel.chosenGenreCode}")
                                    findNavController().navigate(R.id.action_SearchSettings3Fragment_to_SearchSettings1Fragment)
                                }
                            }
                            else -> {
                                Log.d(TAG, "Перешли в состояние неудачного поиска")
                                binding.searchFailed.isGone = false
                                binding.chipScrollView.isGone = true
                            }
                        }
                    }
            }
    }
}