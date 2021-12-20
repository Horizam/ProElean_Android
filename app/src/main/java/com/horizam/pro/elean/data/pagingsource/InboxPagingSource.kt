package com.horizam.pro.elean.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.horizam.pro.elean.data.model.Inbox
import com.horizam.pro.elean.data.model.Message
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException


class InboxPagingSource(
    private val query: Query
): PagingSource<QuerySnapshot, Inbox>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Inbox> {
        return try {
            val currentPage = params.key ?: query.get().await()
            val nextPage:QuerySnapshot? = if (currentPage.isEmpty){
                null
            }else{
                val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
                query.startAfter(lastVisibleProduct).get().await()
            }
            LoadResult.Page(
                data = currentPage.toObjects(Inbox::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException){
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Inbox>): QuerySnapshot? {
        return null
    }

}