package com.irfan.storyapp.presentation.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.irfan.storyapp.R
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.common.loadImage
import com.irfan.storyapp.data.datasource.dataStore
import com.irfan.storyapp.databinding.FragmentDetailStoryBinding
import com.irfan.storyapp.presentation.view_model.DetailStoryViewModel
import com.irfan.storyapp.presentation.view_model.SettingViewModel
import com.irfan.storyapp.presentation.view_model_factory.DetailStoryViewModelFactory
import com.irfan.storyapp.presentation.view_model_factory.SettingViewModelFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DetailStoryFragment : Fragment() {
    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        val view = binding.root

        val animation = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        postponeEnterTransition(200, TimeUnit.MILLISECONDS)

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
            val factoryDetailStory = DetailStoryViewModelFactory.getInstance(token)
            val viewModelDetailStory: DetailStoryViewModel by viewModels {
                factoryDetailStory
            }

            onRefresh(viewModelDetailStory)

            showDetailStory(viewModelDetailStory, view)
        }
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
                            .navigate(R.id.action_detailStoryFragment_to_signInFragment)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showDetailStory(viewModelDetailStory: DetailStoryViewModel, view: View) {
        binding.apply {
            detailStoryToolbar.setNavigationOnClickListener {
                view.findNavController().navigateUp()
            }

            detailStorySwipeRefresh.setOnRefreshListener {
                onRefresh(viewModelDetailStory)
                detailStorySwipeRefresh.isRefreshing = false
            }

            viewModelDetailStory.getDetailStoryResult().observe(viewLifecycleOwner) { resultState ->
                if (resultState != null) {
                    when (resultState) {
                        is ResultState.Initial -> {
                            showLoading(true)
                            showErrorMessage(isError = false)
                        }
                        is ResultState.Loading -> {
                            showLoading(true)
                            showErrorMessage(isError = false)
                        }
                        is ResultState.NoData -> {
                            showLoading(false)
                            val noData = getString(R.string.failed_to_load_data)
                            showErrorMessage(noData)
                        }
                        is ResultState.HasData -> {
                            showLoading(false)
                            showErrorMessage(isError = false)

                            val detailStory = resultState.data?.data

                            detailStoryImgStory.loadImage(detailStory?.photoUrl ?: "")

                            detailStoryTvDateTime.text = formatDateTime(detailStory?.createdAt ?: "")
                            detailStoryTvTitle.text = detailStory?.name
                            detailStoryTvDeskripsi.text = detailStory?.description
                        }

                        is ResultState.Error -> {
                            showLoading(false)
                            showErrorMessage(resultState.error.peekContent())
                            resultState.error.getContentIfNotHandled()?.let { message ->
                                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onRefresh(viewModelDetailStory: DetailStoryViewModel) {
        val args = DetailStoryFragmentArgs.fromBundle(arguments as Bundle)
        val id = args.id
        viewModelDetailStory.fetchDetailStory(id)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            detailStoryGroupOnHasData.visibility = if (isLoading) View.GONE else View.VISIBLE
            detailStoryProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showErrorMessage(errorMessage: String = "", isError: Boolean = true) {
        binding.apply {
            if (isError) {
                detailStoryTvErrorMessage.text = errorMessage
                detailStoryTvErrorMessage.visibility = View.VISIBLE
            } else {
                detailStoryTvErrorMessage.text = ""
                detailStoryTvErrorMessage.visibility = View.GONE
            }

        }
    }

    private fun formatDateTime(dateTimeString: String): String {
        return try {
            MyLogger.d(TAG, "formatDateTime, dateTimeString: $dateTimeString")
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val date = simpleDateFormat.parse(dateTimeString) as Date
            DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG).format(date)
        } catch (e: Exception) {
            MyLogger.d(TAG, "formatDateTime, e: $e")
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DetailStoryFragment"
    }
}