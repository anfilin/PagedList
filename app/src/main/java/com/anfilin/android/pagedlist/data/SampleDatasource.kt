package com.anfilin.android.pagedlist.data

import androidx.paging.PageKeyedDataSource
import androidx.lifecycle.MutableLiveData
import com.anfilin.android.pagedlist.data.entity.HeaderItem
import com.anfilin.android.pagedlist.data.entity.SampleItem

class SampleDatasource : PageKeyedDataSource<Long, Any>() {

    // Variable are for updating the UI when data is being fetched by displaying a loading indicator
    val loadingData: MutableLiveData<Boolean> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams<Long>,
                             callback: LoadInitialCallback<Long, Any>) {

        loadingData.postValue(true)

        // Fill data array
        val list = ArrayList<Any>()

        list.add(HeaderItem().apply { message = "Header message" })

        for (i in 1..PAGE_SIZE) {
            list.add(SampleItem(i.toLong()).apply {
                title = "Title $id"
                message = "Message $id"
            })
        }

        loadingData.postValue(false)
        callback.onResult(list, null, 2L)
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Any>) {}

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Any>) {
        loadingData.postValue(true)

        val page = params.key

        val list = ArrayList<Any>()

        for (i in 1..PAGE_SIZE) {
            list.add(SampleItem(page * PAGE_SIZE + i).apply {
                title = "Title $id"
                message = "Message $id"
            })
        }

        loadingData.postValue(false)
        callback.onResult(list, page + 1)
    }

    companion object {
        const val PAGE_SIZE = 7
    }
}
