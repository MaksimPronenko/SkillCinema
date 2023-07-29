package edu.skillbox.skillcinema.presentation.welcome

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
import edu.skillbox.skillcinema.databinding.FragmentWelcome2Binding
import edu.skillbox.skillcinema.presentation.OnSwipeTouchListener
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Welcome2Fragment : Fragment() {

    private var _binding: FragmentWelcome2Binding? = null
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

        _binding = FragmentWelcome2Binding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeSlides()

        binding.welcome2Fragment.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().navigate(R.id.action_Welcome2Fragment_to_Welcome3Fragment)
            }

            override fun onClick() {
                super.onClick()
                findNavController().navigate(R.id.action_Welcome2Fragment_to_Welcome3Fragment)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                findNavController().popBackStack()
            }
        })

        binding.buttonSkip.setOnClickListener {
            findNavController().navigate(R.id.action_Welcome2Fragment_to_MainFragment_no_animation)
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
                findNavController().navigate(R.id.action_Welcome2Fragment_to_Welcome3Fragment)
            }
    }
}