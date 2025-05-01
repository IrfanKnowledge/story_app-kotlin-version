package com.irfan.storyapp.presentation.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.irfan.storyapp.R
import com.irfan.storyapp.common.CameraHelper
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.common.reduceFileImage
import com.irfan.storyapp.data.datasource.dataStore
import com.irfan.storyapp.databinding.FragmentAddStoryBinding
import com.irfan.storyapp.presentation.view_model.AddStoryViewModel
import com.irfan.storyapp.presentation.view_model.HomeViewModel
import com.irfan.storyapp.presentation.view_model.SettingViewModel
import com.irfan.storyapp.presentation.view_model_factory.AddStoryViewModelFactory
import com.irfan.storyapp.presentation.view_model_factory.SettingViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private var viewModelAddStory: AddStoryViewModel? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModelAddStory?.currentImageUri = uri
            showImage()
        } else {
            MyLogger.d(TAG, "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            viewModelAddStory?.currentImageUri = null
        }
    }


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

        showLoadingOnBtnUpload(true)

        viewModelGetTokenResultObserve(viewModelSetting, view) { token ->
            val viewModelHome: HomeViewModel by navGraphViewModels(R.id.main_navigation)

            val factoryAddStory = AddStoryViewModelFactory.getInstance(token)
            viewModelAddStory = ViewModelProvider(this, factoryAddStory)[AddStoryViewModel::class.java]

            viewModelAddStory!!.getAddStoryResult().observe(viewLifecycleOwner) { resultState ->
                MyLogger.d(TAG, "onViewCreated, getAddStoryResult, resultState: $resultState")
                when (resultState) {
                    is ResultState.Initial -> Unit
                    is ResultState.Loading -> showLoadingOnBtnUpload(true)
                    is ResultState.NoData -> showLoadingOnBtnUpload(false)
                    is ResultState.HasData -> {
                        viewModelHome.refreshListStory()
                        resultState.data?.message?.getContentIfNotHandled()?.let { message ->
                            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                        }
                        view.findNavController().popBackStack()
                    }
                    is ResultState.Error -> {
                        showLoadingOnBtnUpload(false)
                        resultState.error.getContentIfNotHandled()?.let { message ->
                            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            showImage()

            binding.apply {
                addStoryBtnGallery.setOnClickListener {
                    startGallery()
                }

                addStoryBtnCamera.setOnClickListener {
                    startCamera()
                }

                addStoryBtnUpload.setOnClickListener {
                    uploadImage(viewModelAddStory!!, view)
                }
            }
        }

        showLoadingOnBtnUpload(false)
    }


    private fun viewModelGetTokenResultObserve(
        viewModelSetting: SettingViewModel,
        view: View,
        onHasData: (token: String) -> Unit,
    ) {
        viewModelSetting.getTokenResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                MyLogger.d(HomeFragment.TAG, "viewModelGetTokenResultObserve, resultState: $resultState")
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

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        viewModelAddStory?.apply {
            currentImageUri = CameraHelper.getImageUri(requireActivity())
            launcherIntentCamera.launch(currentImageUri)
        }
    }

    private fun showImage() {
        viewModelAddStory?.apply {
            currentImageUri?.let {
                MyLogger.d(TAG, "showImage: $it")
                binding.addStoryImgStory.setImageURI(it)
            }
        }
    }

    private fun uploadImage(
        addStoryViewModel: AddStoryViewModel,
        view: View,
    ) {
        viewModelAddStory?.apply {
            currentImageUri?.let { uri ->
                showLoadingOnBtnUpload(true)
                lifecycleScope.launch {
                    val imageFile: File
                    withContext(Dispatchers.IO) {
                        MyLogger.d(TAG, "uploadImage, uriToFile, reduceFileImage")
                        imageFile = CameraHelper.uriToFile(uri, requireActivity()).reduceFileImage()
                    }
                    withContext(Dispatchers.Main) {
                        MyLogger.d(TAG, "uploadImage, imageFile: ${imageFile.path}")
                        val description = binding.addStoryEdtDescriptionValue.text.toString()

                        addStoryViewModel.addStory(imageFile, description)
                    }
                }
            }
            if (currentImageUri == null) {
                Snackbar.make(view, getString(R.string.image_upload_empty), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLoadingOnBtnUpload(isLoading: Boolean) {
        binding.apply {
            addStoryBtnUpload.visibility = if (isLoading) View.GONE else View.VISIBLE
            addStoryLoadingUpload.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddStoryFragment"
    }
}