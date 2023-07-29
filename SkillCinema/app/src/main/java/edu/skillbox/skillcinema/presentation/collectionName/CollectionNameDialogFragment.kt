package edu.skillbox.skillcinema.presentation.collectionName

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.CollectionNameDialogBinding
import javax.inject.Inject

private const val ARG_NEW_COLLECTION_NAME = "newCollectionName"

@AndroidEntryPoint
class CollectionNameDialogFragment : AppCompatDialogFragment() {

    @Inject
    lateinit var collectionNameDialogViewModel: CollectionNameDialogViewModelFactory
    private val viewModel: CollectionNameDialogViewModel by viewModels { collectionNameDialogViewModel }

    private var _binding: CollectionNameDialogBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.CollectionNameDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CollectionNameDialogBinding.bind(
            inflater.inflate(
                R.layout.collection_name_dialog,
                container
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.collectionNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                viewModel.newCollectionName = s.toString()
                viewModel.determineCorrectName(s.toString())
            }
        })

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            CollectionNameViewModelState.DataIsEmpty -> {
                                binding.readyButton.isEnabled = false
                                binding.inputError.isVisible = false
                            }
                            CollectionNameViewModelState.DataIsValid -> {
                                binding.readyButton.isEnabled = true
                                binding.inputError.isVisible = false
                            }
                            CollectionNameViewModelState.DataIsNotValid -> {
                                binding.readyButton.isEnabled = false
                                binding.inputError.isVisible = true
                            }
                        }
                    }
            }

        binding.readyButton.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ARG_NEW_COLLECTION_NAME,
                viewModel.newCollectionName
            )
            dialog?.dismiss()
        }

        binding.closeButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("FilmVM", "CollectionNameDialogFragment. onDestroyView.")
    }
}