package com.anfilin.android.pagedlist.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

class SampleDataFactory : DataSource.Factory<Long, Any>() {
    val mutableLiveData: MutableLiveData<SampleDatasource> = MutableLiveData()
    private var dataSource: SampleDatasource? = null

    override fun create(): DataSource<Long, Any> {
        dataSource = SampleDatasource()
        mutableLiveData.postValue(dataSource)
        return dataSource!!
    }
}
