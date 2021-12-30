package com.horizam.pro.elean.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.model.response.Offer
import retrofit2.HttpException
import java.io.IOException


class JobOffersPagingSource(
    private val apiHelper: ApiHelper,
    private val id: String
): PagingSource<Int, Offer>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Offer> {

        val position = params.key ?: Constants.STARTING_PAGE_INDEX

        return try {
            val response = apiHelper.getJobOffers(id,position)
            val sellers = response.offerList
            LoadResult.Page(
                data = sellers,
                prevKey = if (position == Constants.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (sellers.isEmpty()) null else position + 1
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException){
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Offer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}