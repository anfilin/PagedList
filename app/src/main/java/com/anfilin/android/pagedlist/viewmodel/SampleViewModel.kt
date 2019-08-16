package com.anfilin.android.pagedlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagedList.BoundaryCallback
import androidx.paging.toLiveData
import com.anfilin.android.pagedlist.data.SampleDataFactory
import com.anfilin.android.pagedlist.data.SampleDatasource

class SampleViewModel : ViewModel() {
    val items: LiveData<PagedList<Any>>
    val dataLoading: LiveData<Boolean>

    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean>
        get() = _empty

    private val boundaryCallback = object: BoundaryCallback<Any>() {
        override fun onZeroItemsLoaded() {
            _empty.postValue(true)
        }

        override fun onItemAtFrontLoaded(itemAtFront: Any) {
            _empty.postValue(false)

        }
    }

    init {
        val factory = SampleDataFactory()

        items = factory.toLiveData(
            Config(pageSize = 7, enablePlaceholders = true, maxSize = 7 * 15, prefetchDistance = 3),
            boundaryCallback = boundaryCallback)

        dataLoading = Transformations.switchMap(factory.mutableLiveData, SampleDatasource::loadingData)
    }

    fun refresh() {
        items.value?.dataSource?.invalidate()
    }
}
