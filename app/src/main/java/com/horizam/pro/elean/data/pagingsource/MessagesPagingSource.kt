package com.horizam.pro.elean.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.horizam.pro.elean.data.model.Message
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException


class MessagesPagingSource(
    private val query: Query
): PagingSource<QuerySnapshot, Message>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Message> {
        return try {
            val currentPage = params.key ?: query.get().await()
            val nextPage:QuerySnapshot? = if (currentPage.isEmpty){
                null
            }else{
                val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
                query.startAfter(lastVisibleProduct).get().await()
            }
            LoadResult.Page(
                data = currentPage.toObjects(Message::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException){
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Message>): QuerySnapshot? {
        return null
    }

}