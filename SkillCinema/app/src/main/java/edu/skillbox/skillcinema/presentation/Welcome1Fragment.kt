package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.FragmentWelcome1Binding
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Welcome"

@AndroidEntryPoint
class Welcome1Fragment : Fragment() {

    private var _binding: FragmentWelcome1Binding? = null
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
//    private val viewModel: Welcome1ViewModel by viewModels()

//    private val viewModel: Welcome1ViewModel by viewModels {
//        DaggerAppComponent.create().welcome1ViewModelFactory()
//    }



//    lateinit var welcome1ViewModelFactory: Welcome1ViewModelFactory
//    private val viewModel: Welcome1ViewModel by viewModels {
//        welcome1ViewModelFactory
//    }

//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return Welcome1ViewModel(requireContext().applicationContext as App) as T
//            }
//        }
//    }

//    @Inject
//    lateinit var vmFactory: WelcomeViewModelFactory
//    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWelcome1Binding.inflate(inflater, container, false)
//        (context?.applicationContext as App).appComponent.inject(this)
//        viewModel = ViewModelProvider(this, vmFactory).get(WelcomeViewModel::class.java)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeSlides()

        binding.welcome1Fragment.setOnClickListener {
            findNavController().navigate(R.id.action_Welcome1Fragment_to_Welcome2Fragment)
        }
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