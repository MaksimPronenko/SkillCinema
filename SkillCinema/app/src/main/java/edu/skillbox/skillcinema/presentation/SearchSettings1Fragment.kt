package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.FragmentSearchSettings1Binding
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "SearchSettings.Fragment1"

@AndroidEntryPoint
class SearchSettings1Fragment : Fragment() {

    private var _binding: FragmentSearchSettings1Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var searchSettings1ViewModelFactory: SearchSettings1ViewModelFactory
    private val viewModel: SearchSettings1ViewModel by activityViewModels {
        searchSettings1ViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSettings1Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshAppData()

        binding.chipAll.isChecked = viewModel.type == "ALL"
        binding.chipFilms.isChecked = viewModel.type == "FILM"
        binding.chipSerials.isChecked = viewModel.type == "TV_SERIES"

        binding.textCountry.text = countryText(viewModel.chosenCountryCode)
        binding.textGenre.text = genreText(viewModel.chosenGenreCode)
        binding.textYears.text = periodText(viewModel.yearFrom, viewModel.yearTo)
        binding.ratingSlider.values = listOf(viewModel.ratingFrom.toFloat(), viewModel.ratingTo.toFloat())
        Log.d(TAG, "ratingSlider.values = ${binding.ratingSlider.values}")

        binding.chipDate.isChecked = viewModel.order == "YEAR"
        binding.chipPopularity.isChecked = viewModel.order == "NUM_VOTE"
        binding.chipRating.isChecked = viewModel.order == "RATING"

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_SearchSettings1Fragment_to_SearchFragment)
        }

        binding.filmTypesChipGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.chipAll.isChecked) viewModel.setAndSaveType("ALL")
            if (binding.chipFilms.isChecked) viewModel.setAndSaveType("FILM")
            if (binding.chipSerials.isChecked) viewModel.setAndSaveType("TV_SERIES")
        }

        binding.countryButton.setOnClickListener {
            findNavController().navigate(R.id.action_SearchSettings1Fragment_to_SearchSettings2Fragment)
        }

        binding.genreButton.setOnClickListener {
            findNavController().navigate(R.id.action_SearchSettings1Fragment_to_SearchSettings3Fragment)
        }

        binding.yearsButton.setOnClickListener {
            findNavController().navigate(R.id.action_SearchSettings1Fragment_to_SearchSettings4Fragment)
        }

        binding.ratingSlider.addOnChangeListener { _, _, _ ->
            val values = binding.ratingSlider.values
            viewModel.setAndSaveRatingFrom(values[0].roundToInt())
            viewModel.setAndSaveRatingTo(values[1].roundToInt())
            Log.d(TAG, "Рейтинг от ${viewModel.ratingFrom} до ${viewModel.ratingTo}")
        }

        binding.sortingTypesChipGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.chipDate.isChecked) viewModel.setAndSaveOrder("YEAR")
            if (binding.chipPopularity.isChecked) viewModel.setAndSaveOrder("NUM_VOTE")
            if (binding.chipRating.isChecked) viewModel.setAndSaveOrder("RATING")
        }

        binding.showWatchedButton.setOnClickListener {
            if (viewModel.changeAndSaveShowWatched()) {
                binding.viewedImage.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.watched,
                        null
                    )
                )
                binding.viewedImage.setColorFilter(resources.getColor(R.color.blue, null))
                binding.textHideViewed.text = resources.getText(R.string.watched)
            } else {
                binding.viewedImage.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.not_watched,
                        null
                    )
                )
                binding.viewedImage.setColorFilter(resources.getColor(R.color.grey_4, null))
                binding.textHideViewed.text = resources.getText(R.string.not_watched)
            }
        }
    }

    private fun countryText(code: Int?): String = when (code) {
        1 -> "США"
        2 -> "Швейцария"
        3 -> "Франция"
        4 -> "Польша"
        5 -> "Великобритания"
        6 -> "Швеция"
        7 -> "Индия"
        8 -> "Испания"
        9 -> "Германия"
        10 -> "Италия"
        12 -> "Германия (ФРГ)"
        13 -> "Австралия"
        14 -> "Канада"
        15 -> "Мексика"
        16 -> "Япония"
        21 -> "Китай"
        33 -> "СССР"
        34 -> "Россия"
        else -> resources.getString(R.string.she_is_any)
    }

    private fun genreText(code: Int?): String = when (code) {
        1 -> "Триллер"
        2 -> "Драма"
        3 -> "Криминальный фильм"
        4 -> "Мелодрама"
        5 -> "Детектив"
        6 -> "Фантастика"
        11 -> "Боевик"
        12 -> "Фэнтези"
        13 -> "Комедия"
        14 -> "Военный фильм"
        15 -> "Исторический фильм"
        17 -> "Ужасы"
        18 -> "Мультфильм"
        19 -> "Семейный фильм"
        33 -> "Детский фильм"
        else -> resources.getString(R.string.he_is_any)
    }

    private fun periodText(yearFrom: Int?, yearTo: Int?): String {
        return if (yearFrom == null && yearTo == null) resources.getText(R.string.he_is_any)
            .toString()
        else if (yearFrom == null) "до $yearTo"
        else if (yearTo == null) "c $yearFrom"
        else "c $yearFrom до $yearTo"
    }
}