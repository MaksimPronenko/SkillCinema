package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.ErrorBottomDialogBinding

class ErrorBottomDialogFragment : BottomSheetDialogFragment() {

    lateinit var binding: ErrorBottomDialogBinding

    override fun getTheme() = R.style.ErrorBottomDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ErrorBottomDialogBinding.bind(inflater.inflate(R.layout.error_bottom_dialog, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.setCanceledOnTouchOutside(false)
    }

}