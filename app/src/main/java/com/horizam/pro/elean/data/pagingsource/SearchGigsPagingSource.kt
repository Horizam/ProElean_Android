package com.horizam.pro.elean.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.model.response.ServiceDetail
import retrofit2.HttpException
import java.io.IOException


class SearchGigsPagingSource(
    private val apiHelper: ApiHelper,
    private val query: String,
    private val distance: String,
    private val filter: String,
    private val filterValue: String
) : PagingSource<Int, ServiceDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ServiceDetail> {

        val position = params.key ?: Constants.STARTING_PAGE_INDEX

        return try {
            val response = apiHelper.searchGigs(query, distance, filter, filterValue, position)
            val sellers = response.serviceList
            LoadResult.Page(
                data = sellers,
                prevKey = if (position == Constants.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (sellers.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ServiceDetail>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}