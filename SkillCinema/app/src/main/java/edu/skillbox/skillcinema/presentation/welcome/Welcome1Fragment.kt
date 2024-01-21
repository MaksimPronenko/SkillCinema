package edu.skillbox.skillcinema.presentation.welcome

import android.annotation.SuppressLint
import android.os.Bundle
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
import edu.skillbox.skillcinema.databinding.FragmentWelcome1Binding
import edu.skillbox.skillcinema.presentation.OnSwipeTouchListener
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Welcome1Fragment : Fragment() {

    private var _binding: FragmentWelcome1Binding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var welcomeViewModelFactory: WelcomeViewModelFactory
    private val viewModel: WelcomeViewModel by activityViewModels {
        welcomeViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val bottomNavigation: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
        if (bottomNavigation != null) bottomNavigation.isGone = true

        _binding = FragmentWelcome1Binding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeSlides()

        binding.welcome1Fragment.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().navigate(R.id.action_Welcome1Fragment_to_Welcome2Fragment)
            }

            override fun onClick() {
                super.onClick()
                findNavController().navigate(R.id.action_Welcome1Fragment_to_Welcome2Fragment)
            }
        })

        binding.buttonSkip.setOnClickListener {
            findNavController().navigate(R.id.action_Welcome1Fragment_to_MainFragment_no_animation)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeSlides() {
        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                val jobDelay = viewModel.changeScreenScope.launch {
                    viewModel.countdownToChangeScreen()
                }
                jobDelay.join()
                findNavController().navigate(R.id.action_Welcome1Fragment_to_Welcome2Fragment)
            }
    }
}