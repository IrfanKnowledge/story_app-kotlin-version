package com.irfan.storyapp.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.databinding.FragmentSignUpBinding
import com.irfan.storyapp.presentation.view_model.SignUpViewModel
import com.irfan.storyapp.presentation.view_model_factory.AuthViewModelFactory

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factorySignUp = AuthViewModelFactory.getInstance(null)
        val viewModelSignUp: SignUpViewModel by viewModels { factorySignUp }

        viewModelObserve(viewModelSignUp, view)

        binding.signUpBtnSignUp.setOnClickListener { onBtnSignUpClicked(viewModelSignUp) }
    }

    private fun viewModelObserve(
        viewModel: SignUpViewModel,
        view: View,
    ) {
        viewModel.getSignUpResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                fun showLoading(isLoading: Boolean) {
                    binding.apply {
                        signUpGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
                        signUpProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                when (resultState) {
                    is ResultState.Initial -> Unit
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.NoData -> showLoading(false)
                    is ResultState.HasData -> {
                        showLoading(false)
                        resultState.data?.message?.getContentIfNotHandled()?.let { message ->
                            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        resultState.error.getContentIfNotHandled()?.let { message ->
                            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun onBtnSignUpClicked(viewModel: SignUpViewModel) {
        val name = binding.signUpEtName.text.toString()
        val email = binding.signUpEtEmail.text.toString()
        val password = binding.signUpEtPassword.text.toString()

        viewModel.signUp(name, email, password)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "SignUpFragment"
    }
}