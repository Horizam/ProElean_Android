package com.horizam.pro.elean.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.model.response.PostedJob
import retrofit2.HttpException
import java.io.IOException


class PostedJobsPagingSource(
    private val apiHelper: ApiHelper,
    private val status: String
) : PagingSource<Int, PostedJob>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostedJob> {

        val position = params.key ?: Constants.STARTING_PAGE_INDEX

        return try {
            val response = apiHelper.getPostedJobs(position,status)
            val postedJobs = response.postedJobsData.postedJobsList
            LoadResult.Page(
                data = postedJobs,
                prevKey = if (position == Constants.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (postedJobs.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostedJob>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}