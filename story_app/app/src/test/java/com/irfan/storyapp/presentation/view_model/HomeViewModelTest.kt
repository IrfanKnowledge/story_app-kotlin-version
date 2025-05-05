package com.irfan.storyapp.presentation.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.irfan.storyapp.data.repository.HomeRepository
import com.irfan.storyapp.data_dummy.DataDummy
import com.irfan.storyapp.domain.entity.story.StoryEntity
import com.irfan.storyapp.presentation.adapter.ListStoryAdapter
import com.irfan.storyapp.utils.MainDispatcherRule
import com.irfan.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Skenario
 *  - Ketika berhasil memuat list story
 *      - Memastikan data tidak null
 *      - Memastikan jumlah data sesuai dengan yang diharapkan
 *      - Memastikan data pertama yang dikembalikan sesuai
 *  - Ketika tidak ada list story (kosong)
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var homeRepository: HomeRepository

    @Test
    fun `when Get List Story Should Not Null and Return Data`() = runTest {
        val dataDummy = DataDummy.generateDummyListStory()
        val data: PagingData<StoryEntity> = HomePagingSource.snapshot(dataDummy)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        Mockito.`when`(homeRepository.getStories(5, 1)).thenReturn(expectedStory)

        val mainViewModel = HomeViewModel(homeRepository)
        val actualStory: PagingData<StoryEntity> = mainViewModel.listStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dataDummy.size, differ.snapshot().size)
        Assert.assertEquals(dataDummy[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get List Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        Mockito.`when`(homeRepository.getStories(5, 1)).thenReturn(expectedStory)

        val homeViewModel = HomeViewModel(homeRepository)
        val actualStory: PagingData<StoryEntity> = homeViewModel.listStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class HomePagingSource : PagingSource<Int, List<StoryEntity>>() {
    override fun getRefreshKey(state: PagingState<Int, List<StoryEntity>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, List<StoryEntity>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}