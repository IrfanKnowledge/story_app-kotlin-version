package com.irfan.storyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.datasource.dataStore
import com.irfan.storyapp.databinding.FragmentAddStoryBinding
import com.irfan.storyapp.presentation.ui.HomeFragment
import com.irfan.storyapp.presentation.view_model.HomeViewModel
import com.irfan.storyapp.presentation.view_model.SettingViewModel
import com.irfan.storyapp.presentation.view_model_factory.SettingViewModelFactory

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
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

        showLoading(true)

        viewModelGetTokenResultObserve(viewModelSetting, view) { token ->
            val viewModelHome: HomeViewModel by navGraphViewModels(R.id.main_navigation)

            onPop(viewModelHome, view)
        }

        showLoading(false)
    }

    private fun viewModelGetTokenResultObserve(
        viewModelSetting: SettingViewModel,
        view: View,
        onHasData: (token: String) -> Unit,
    ) {
        viewModelSetting.getTokenResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                Log.d(HomeFragment.TAG, "viewModelGetTokenResultObserve, resultState: $resultState")
                when (resultState) {
                    is ResultState.HasData -> {
                        onHasData(resultState.data)
                    }

                    is ResultState.Error -> {
                        view.findNavController()
                            .navigate(R.id.action_addStoryFragment_to_signInFragment)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onPop(viewModelHome: HomeViewModel, view: View) {
        val navController = view.findNavController()

        val callBack = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: refresh")
                viewModelHome.refreshListStory()

                navController.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
    }

    private fun showLoading(isLoading: Boolean) {
        // todo: show loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddStoryFragment"
    }
}