package com.horizam.pro.elean.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.model.response.ServiceReviews
import retrofit2.HttpException
import java.io.IOException

class GetGigsPagingSource(val apiHelper: ApiHelper, val id: String)
    : PagingSource<Int, ServiceReviews>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ServiceReviews> {
        val position = params.key ?: Constants.STARTING_PAGE_INDEX

        return try {
            val response = apiHelper.getReviews(id)
            val sellers = response.data
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

    override fun getRefreshKey(state: PagingState<Int, ServiceReviews>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}