package com.horizam.pro.elean.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.google.gson.Gson
import com.horizam.pro.elean.App
import com.horizam.pro.elean.data.model.ErrorResponse
import com.horizam.pro.elean.ui.main.events.LogoutEvent
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import java.io.File
import java.net.URLConnection
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.text.Spannable

import android.text.style.ForegroundColorSpan

import android.text.SpannableString
import okhttp3.OkHttpClient


class BaseUtils {

    companion object {

        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
        var CurrentScreen = ""
        var screenHeight: Int = 0
        var screenWidth: Int = 0
        var DEVICE_ID: String = ""
        var isUserProfileScreen: Boolean = false

        fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

        fun getError(exception: Exception): String {
            return try {
                val httpException: HttpException = exception as HttpException
                val errorBody: String = httpException.response()!!.errorBody()!!.string()
                val errorResponse: ErrorResponse =
                    Gson().fromJson(errorBody, ErrorResponse::class.java)
                if (httpException.code() == 401) {
                    EventBus.getDefault().post(LogoutEvent())
                }
                errorResponse.message
            } catch (e: Exception) {
                exception.message.toString()
            }
        }

        fun createRequestBodyFromString(param: String): RequestBody {
            return param.toRequestBody("text/plain;charset=utf-8".toMediaType())
        }

        fun compressAndCreateFormData(filePath: String, s: String): MultipartBody.Part {
            //val file: File = Compressor.compress(this, File(profileImageUrl), Dispatchers.Main)
            val file: File = File(filePath)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaType())
            return MultipartBody.Part.createFormData(
                s,
                file.name,
                requestBody
            )
        }

        suspend fun compressAndCreateImageData(
            filePath: String,
            s: String,
            requireContext: Context
        ): MultipartBody.Part {
            val file: File = Compressor.compress(requireContext, File(filePath), Dispatchers.Main)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaType())
            return MultipartBody.Part.createFormData(
                s,
                file.name,
                requestBody
            )
        }

        fun isImageFile(path: String): Boolean {
            val mimeType: String = URLConnection.guessContentTypeFromName(path)
            return mimeType.startsWith("image")
        }

        fun Fragment.hideKeyboard() {
            view?.let { activity?.hideKeyboard(it) }
        }

        fun Activity.hideKeyboard() {
            hideKeyboard(currentFocus ?: View(this))
        }

        private fun Context.hideKeyboard(view: View) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun isNumber(s: String?): Boolean {
            return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }
        }

        fun localToGMT(date: Date): Date? {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return DateFormat.getDateInstance().parse(sdf.format(date))
        }

        fun gmtToLocal(date: Date): Date {
            val timeZone = Calendar.getInstance().timeZone.id
            return Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
        }

        fun utcToLocal(dateString: String): String {
            var df=SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("UTC")
            df.timeZone = TimeZone.getDefault()
            val date = df.parse(dateString)
            return df.format(date!!)
        }

        fun getMillisecondsFromUtc(dateString: String): Long {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date = df.parse(dateString)
            df.timeZone = TimeZone.getDefault()
            return date!!.time
        }

        private fun millisecondsToLocalDate(milliseconds: Long): Date {
            val timeZone = Calendar.getInstance().timeZone.id
            return Date(milliseconds + TimeZone.getTimeZone(timeZone).getOffset(milliseconds))
        }

        fun getTimeAgoFirst(milliseconds: Long): String {
            val localDate = millisecondsToLocalDate(milliseconds)
            return DateUtils.getRelativeDateTimeString(
                App.getAppContext(),
                localDate.time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0
            ).toString()
        }

        fun getTimeAgo(milliseconds: Long): String? {
            var time = milliseconds
            if (time < 1000000000000L) {
                time *= 1000
            }
            val now = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }
            val diff = now - time
            return when {
                diff < MINUTE_MILLIS -> {
                    "just now"
                }
                diff < 2 * MINUTE_MILLIS -> {
                    "a minute ago"
                }
                diff < 50 * MINUTE_MILLIS -> {
                    (diff / MINUTE_MILLIS).toString() + " minutes ago"
                }
                diff < 90 * MINUTE_MILLIS -> {
                    "an hour ago"
                }
                diff < 24 * HOUR_MILLIS -> {
                    (diff / HOUR_MILLIS).toString() + " hours ago"
                }
                diff < 48 * HOUR_MILLIS -> {
                    "yesterday"
                }
                else -> {
                    (diff / DAY_MILLIS).toString() + " days ago"
                }
            }
        }

        fun scanFile(ctxt: Context, f: File, mimeType: String) {
            MediaScannerConnection.scanFile(ctxt, arrayOf(f.absolutePath), arrayOf(mimeType), null)
        }

        fun getFileSize(filePath: String): Long {
            val file: File = File(filePath)
            // Get length of file in bytes
            val fileSizeInBytes: Long = file.length()
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            val fileSizeInKB = fileSizeInBytes / 1024
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            return fileSizeInKB / 1024
        }

        fun animationOpenScreen(): NavOptions {
            return navOptions { // Use the Kotlin DSL for building NavOptions
                anim {
                    enter = R.animator.fade_in
                    exit = R.animator.fade_out
                }
            }
        }
        fun shareOutSideIntent(context: Context, data: String) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT,"Pro eLean")
            intent.putExtra(Intent.EXTRA_TEXT, data)
            context.startActivity(Intent.createChooser(intent, "choose one"))
        }
    }
}

