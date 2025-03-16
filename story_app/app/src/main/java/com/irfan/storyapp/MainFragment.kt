package com.irfan.storyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.datasource.dataStore
import com.irfan.storyapp.databinding.FragmentMainBinding
import com.irfan.storyapp.presentation.view_model.SettingViewModel
import com.irfan.storyapp.presentation.view_model_factory.SettingViewModelFactory

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = requireActivity().dataStore
        val factorySetting = SettingViewModelFactory.getInstance(dataStore)
        val viewModelSetting: SettingViewModel by viewModels {
            factorySetting
        }

        showLoading()

        viewModelSetting.fetchToken()

        viewModelGetTokenResultObserve(viewModelSetting, view)
    }

    private fun viewModelGetTokenResultObserve(
        viewModelSetting: SettingViewModel,
        view: View,
    ) {
        viewModelSetting.getTokenResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                Log.d(TAG, "viewModelGetTokenResultObserve, resultState: $resultState")

                when (resultState) {
                    is ResultState.Initial -> Unit
                    is ResultState.Loading -> Unit
                    is ResultState.NoData -> {
                        view.findNavController()
                            .navigate(R.id.action_mainFragment_to_signInFragment)
                    }

                    is ResultState.HasData -> {
                        view.findNavController().navigate(R.id.action_mainFragment_to_homeFragment)
                    }

                    is ResultState.Error -> {
                        view.findNavController()
                            .navigate(R.id.action_mainFragment_to_signInFragment)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            signInProgressBar.visibility =  View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MainFragment"
    }
}