package com.irfan.storyapp.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.irfan.storyapp.R
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.datasource.dataStore
import com.irfan.storyapp.databinding.FragmentSignInBinding
import com.irfan.storyapp.presentation.view_model.SettingViewModel
import com.irfan.storyapp.presentation.view_model.SignInViewModel
import com.irfan.storyapp.presentation.view_model_factory.AuthViewModelFactory
import com.irfan.storyapp.presentation.view_model_factory.SettingViewModelFactory

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factoryAuth = AuthViewModelFactory.getInstance(null)
        val viewModelSignIn: SignInViewModel by viewModels {
            factoryAuth
        }

        val dataStore = requireActivity().dataStore
        val factorySetting = SettingViewModelFactory.getInstance(dataStore)
        val viewModelSetting: SettingViewModel by viewModels {
            factorySetting
        }

        viewModelGetSignInResultObserve(
            viewModelSignIn,
            view,
        ) { token ->
            viewModelSetting.saveToken(token)
        }
        viewModelGetSaveTokenResultObserve(viewModelSetting, view)


        binding.signInBtnSignIn.setOnClickListener { onBtnSignInClicked(viewModelSignIn) }
        binding.signInBtnSignUp.setOnClickListener {
            it.findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun viewModelGetSignInResultObserve(
        viewModelSignIn: SignInViewModel,
        view: View,
        onHasData: (token: String) -> Unit,
    ) {
        viewModelSignIn.getSignInResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                Log.d(TAG, "viewModelGetSignInResultObserve, resultState: $resultState")
                when (resultState) {
                    is ResultState.Initial -> Unit
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.NoData -> showLoading(false)
                    is ResultState.HasData -> {
                        viewModelSignIn.setStateSignIn(ResultState.Initial)
                        val token = resultState.data?.data?.token ?: ""
                        onHasData(token)
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

    private fun viewModelGetSaveTokenResultObserve(viewModelSetting: SettingViewModel, view: View) {
        viewModelSetting.getSaveTokenResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                Log.d(TAG, "viewModelGetSaveTokenResultObserve, resultState: $resultState")
                when (resultState) {
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.HasData -> {
                        viewModelSetting.setStateSaveToken(ResultState.Initial)
                        Snackbar.make(view, "Sign In Success", Snackbar.LENGTH_SHORT).show()
                        view.findNavController()
                            .navigate(R.id.action_signInFragment_to_homeFragment)
                        showLoading(false)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun onBtnSignInClicked(viewModel: SignInViewModel) {
        val email = binding.signInEtEmail.text.toString()
        val password = binding.signInEtPassword.text.toString()

        viewModel.signIn(email, password)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            signInGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
            signInProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SignInFragment"
    }
}