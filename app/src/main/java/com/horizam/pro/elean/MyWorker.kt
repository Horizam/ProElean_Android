package com.horizam.pro.elean

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.StoreUserInfoRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse

class MyWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val fcmToken = inputData.getString(Constants.KEY_TOKEN)

        return try {
            if (fcmToken.isNullOrEmpty()) {
                Log.e(MyWorker::class.java.simpleName,"Invalid token")
                throw IllegalArgumentException("Invalid token")
            }
            val apiService = RetrofitBuilder.apiService
            val storeUserInfoRequest = StoreUserInfoRequest(
                device_id = fcmToken
            )
            val data : GeneralResponse = apiService.storeUserInfo(storeUserInfoRequest)
            return if (data.status == Constants.STATUS_OK){
                Result.success()
            }else{
                Result.retry()
            }
        } catch (throwable: Throwable) {
            Log.e(MyWorker::class.java.simpleName,"error saving token")
            Result.failure()
        }
    }
}