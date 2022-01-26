package com.horizam.pro.elean.utils

import android.util.Patterns
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class Validator {

    companion object {

        fun validateUserName(userName: String): Boolean {
            val p: Pattern = Pattern.compile("[^A-Za-z0-9_]")
            val m: Matcher = p.matcher(userName)
            val b: Boolean = m.find()
            return b
        }

        private fun getText(data: Any): String {
            var str = ""
            if (data is EditText) {
                str = data.text.toString().trim()
            } else if (data is String) {
                str = data
            }
            return str
        }

        fun isValidName(data: Any, updateUI: Boolean = true): Boolean {
            val str = getText(data)
            val valid = str.trim().length > 2
            if (updateUI) {
                val error: String? = if (valid) null else App.getAppContext()!!
                    .getString(R.string.str_enter_valid_name)
                setError(data, error)
            }
            return valid
        }

        fun isValidUserName(data: Any, updateUI: Boolean = true): Boolean {
            val str = getText(data)
            var valid = true
            if (str.trim().length < 5) {
                valid = false
            } else if (validateUserName(str.trim())) {
                valid = false
            }
            if (updateUI) {
                val error: String? = if (valid) null else App.getAppContext()!!
                    .getString(R.string.str_enter_valid_username)
                setError(data, error)
            }
            return valid
        }

        fun isValidEmail(data: Any, updateUI: Boolean = true): Boolean {
            val str = getText(data)
            val valid = Patterns.EMAIL_ADDRESS.matcher(str).matches()
            if (updateUI) {
                val error: String? = if (valid) null else App.getAppContext()!!
                    .getString(R.string.str_enter_valid_email_address)
                setError(data, error)
            }
            return valid
        }


        fun isValidPassword(data: Any, updateUI: Boolean = true): Boolean {
            val str = getText(data)
            var valid = true
            if (str.length < 6) {
                valid = false
            }
            if (updateUI) {
                val error: String? = if (valid) null else App.getAppContext()!!
                    .getString(R.string.str_enter_valid_password_policy)
                setError(data, error)
            }
            return valid
        }

        fun isValidPhone(data: Any, updateUI: Boolean = true): Boolean {
            val str = getText(data)
            val valid = Patterns.PHONE.matcher(str).matches()

            // Set error if required
            if (updateUI) {
                val error: String? = if (valid) null else App.getAppContext()!!
                    .getString(R.string.str_enter_valid_phone)
                setError(data, error)
            }

            return valid
        }

        fun isValidAddress(data: Any, updateUI: Boolean = true): Boolean {
            val str = getText(data)
            val valid = str.trim().isNotEmpty()

            // Set error if required
            if (updateUI) {
                val error: String? = if (valid) null else App.getAppContext()!!
                    .getString(R.string.str_enter_valid_address)
                setError(data, error)
            }

            return valid
        }

        fun setError(data: Any, error: String?) {
            if (data is EditText) {
                if (data.parent.parent is TextInputLayout) {
                    (data.parent.parent as TextInputLayout).error = error
                } else {
                    data.error = error
                }
            }
        }

        fun newPasswordConfirmNewPasswordValidation(data1: Any, data2: Any): Boolean {
            if (getText(data1) == getText(data2)) {
                return true
            } else {
                setError(data1, "Password not matched")
                setError(data2, "Password not matched")
                return false
            }
        }

        fun validateVerificationCode(data1: Any): Boolean{
            if(getText(data1).trim().isEmpty()){
                setError(data1 , "Verification Code is Empty")
                return true
            }else{
                setError(data1 , null)
                return false
            }
        }
    }
}