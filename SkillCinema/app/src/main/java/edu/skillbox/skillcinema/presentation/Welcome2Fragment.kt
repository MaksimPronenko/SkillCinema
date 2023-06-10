package edu.skillbox.skillcinema.presentation

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
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Welcome"

@AndroidEntryPoint
class Welcome2Fragment : Fragment() {

    private var _binding: FragmentWelcome2Binding? = null
    private val binding get() = _binding!!

//    private val viewModel: WelcomeViewModel by activityViewModels {
//        WelcomeViewModelFactory(
//            requireContext().applicationContext as App
//        )
//    }

//    val app: App = context as App
//    private val viewModel: WelcomeViewModel by activityViewModels {
//        app.appComponent.welcomeViewModelFactory()
//    }

//    private val viewModel: WelcomeViewModel by activityViewModels {
//        DaggerAppComponent.create().welcomeViewModelFactory()
//    }

    @Inject
    lateinit var welcomeViewModelFactory: WelcomeViewModelFactory
    private val viewModel: WelcomeViewModel by activityViewModels {
        welcomeViewModelFactory
    }

//    @Inject
//    lateinit var welcomeViewModelFactory: WelcomeViewModelFactory
//    private val viewModel: WelcomeViewModel by viewModels {
//        welcomeViewModelFactory
//    }

//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return WelcomeViewModel(requireContext().applicationContext as Application) as T
//            }
//        }
//    }

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

//        binding.welcome2Fragment.setOnClickListener {
//            findNavController().navigate(R.id.action_Welcome2Fragment_to_Welcome3Fragment)
//        }

        binding.welcome2Fragment.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
//                Toast.makeText(
//                    context, "Swipe Left gesture detected",
//                    Toast.LENGTH_SHORT
//                ).show()
                findNavController().navigate(R.id.action_Welcome2Fragment_to_Welcome3Fragment)
            }

            override fun onClick() {
                super.onClick()
                findNavController().navigate(R.id.action_Welcome2Fragment_to_Welcome3Fragment)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
//                Toast.makeText(
//                    context,
//                    "Swipe Right gesture detected",
//                    Toast.LENGTH_SHORT
//                ).show()
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