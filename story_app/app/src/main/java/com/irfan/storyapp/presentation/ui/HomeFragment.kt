package com.irfan.storyapp.presentation.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.navGraphViewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.irfan.storyapp.R
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.datasource.dataStore
import com.irfan.storyapp.databinding.FragmentHomeBinding
import com.irfan.storyapp.presentation.adapter.ListStoryAdapter
import com.irfan.storyapp.presentation.adapter.LoadingStateAdapter
import com.irfan.storyapp.presentation.view_model.HomeViewModel
import com.irfan.storyapp.presentation.view_model.SettingViewModel
import com.irfan.storyapp.presentation.view_model_factory.HomeViewModelFactory
import com.irfan.storyapp.presentation.view_model_factory.SettingViewModelFactory
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterListStory: ListStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val animation = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = animation
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

        viewModelDeleteTokenObserve(viewModelSetting, view)

        viewModelGetTokenResultObserve(viewModelSetting, view) { token ->
            val factoryHome = HomeViewModelFactory.getInstance(token)
            val viewModelHome: HomeViewModel by navGraphViewModels(R.id.main_navigation) {
                factoryHome
            }
            showRecyclerView(viewModelHome, view)
        }

        showLoading(false)

        binding.homeToolbar.setOnMenuItemClickListener { menuItem ->
            onMenuItemClick(menuItem, viewModelSetting)
        }
    }

    private fun viewModelGetTokenResultObserve(
        viewModelSetting: SettingViewModel,
        view: View,
        onHasData: (token: String) -> Unit,
    ) {
        viewModelSetting.getTokenResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                Log.d(TAG, "viewModelGetTokenResultObserve, resultState: $resultState")
                when (resultState) {
                    is ResultState.HasData -> {
                        onHasData(resultState.data)
                    }

                    is ResultState.Error -> {
                        view.findNavController()
                            .navigate(R.id.action_homeFragment_to_signInFragment)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onMenuItemClick(
        menuItem: MenuItem,
        viewModelSetting: SettingViewModel,
    ) = when (menuItem.itemId) {
        R.id.home_menu_sign_out -> {
            viewModelSetting.deleteToken()
            true
        }

        else -> false
    }

    private fun viewModelDeleteTokenObserve(
        viewModelSetting: SettingViewModel,
        view: View,
    ) {
        viewModelSetting.getDeleteTokenResult().observe(viewLifecycleOwner) { resultState ->
            if (resultState != null) {
                when (resultState) {
                    is ResultState.Initial -> Unit
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.NoData -> showLoading(false)
                    is ResultState.HasData -> {
                        Snackbar.make(view, "Sign Out Success", Snackbar.LENGTH_SHORT).show()
                        viewModelSetting.setStateDeleteToken(ResultState.Initial)
                        view.findNavController()
                            .navigate(R.id.action_homeFragment_to_signInFragment)
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        Snackbar.make(view, "Sign Out Failed", Snackbar.LENGTH_SHORT).show()
                        showLoading(false)
                        viewModelSetting.setStateDeleteToken(ResultState.Initial)
                    }
                }
            }
        }
    }

    private fun showRecyclerView(viewModelHome: HomeViewModel, view: View) {
        binding.apply {
            homeRv.layoutManager = LinearLayoutManager(requireActivity())

            adapterListStory = ListStoryAdapter { story, bindingItem ->
                Log.d(TAG, "showRecyclerView, onTap, name: ${story.name}, id: ${story.id}")

                val id = story.id ?: ""
                val toDetailStoryFragment =
                    HomeFragmentDirections.actionHomeFragmentToDetailStoryFragment(id)

                val extras = FragmentNavigatorExtras(
                    bindingItem.itemRowStoryImgContent to "detail_story_img_story_transition",
                    bindingItem.itemRowStoryTvTitle to "detail_story_tv_title_transition",
                    bindingItem.itemRowStoryTvDescription to "detail_story_tv_deskripsi_transition"
                )

//                val extras = FragmentNavigatorExtras(
//                    bindingItem.itemRowStoryImgContent to "detail_story_img_story_transition",
//                    bindingItem.itemRowStoryTvTitle to "detail_story_tv_title_transition",
//                    bindingItem.itemRowStoryTvDescription to "detail_story_tv_deskripsi_transition"
//                )

                view.findNavController().navigate(toDetailStoryFragment, extras)
            }

            val errorMessage = getString(R.string.failed_to_load_data)

            homeRv.adapter = adapterListStory.withLoadStateFooter(
                footer = LoadingStateAdapter(errorMessage) {
                    adapterListStory.retry()
                }
            )

            viewModelHome.refreshListStory.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    Log.d(TAG, "showRecyclerView, refreshListStory: refresh")
                    adapterListStory.refresh()
                }
            }

            homeSwipeRefresh.setOnRefreshListener {
                adapterListStory.refresh()
            }

            adapterListStory.addLoadStateListener { loadState ->
                Log.d(TAG, "showRecyclerView, addLoadStateListener: trigger")
                Log.d(
                    TAG,
                    "showRecyclerView, addLoadStateListener, loadState.refresh: ${loadState.refresh}"
                )
                Log.d(
                    TAG,
                    "showRecyclerView, addLoadStateListener, loadState.isIdle: ${loadState.isIdle}"
                )
                Log.d(
                    TAG,
                    "showRecyclerView, addLoadStateListener, loadState.append: ${loadState.append}"
                )
                Log.d(
                    TAG,
                    "showRecyclerView, addLoadStateListener, loadState.prepend: ${loadState.prepend}"
                )
                Log.d(
                    TAG,
                    "showRecyclerView, addLoadStateListener, loadState.source: ${loadState.source}"
                )
                homeRv.isVisible = loadState.refresh is LoadState.NotLoading
                homeSwipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading

                if (loadState.refresh is LoadState.Loading) {
                    viewModelHome.updateListStory = true
                }
                if (loadState.refresh is LoadState.NotLoading && viewModelHome.updateListStory) {
                    viewModelHome.updateListStory = false
                    homeRv.scrollToPosition(0)
                }
            }

            viewModelHome.listStory.observe(viewLifecycleOwner) {
                adapterListStory.submitData(lifecycle, it)
            }

            homeFabAddStory.setOnClickListener {
                Log.d(TAG, "showRecyclerView, FAB, onTap: navigate to Add Story page")
                view.findNavController()
                    .navigate(R.id.action_homeFragment_to_addStoryFragment)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            homeGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
            homeProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}