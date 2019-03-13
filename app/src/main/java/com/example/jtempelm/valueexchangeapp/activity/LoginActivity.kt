package com.example.jtempelm.valueexchangeapp.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jtempelm.valueexchangeapp.R
import kotlinx.android.synthetic.main.activity_login.emailEditText
import kotlinx.android.synthetic.main.activity_login.emailInput
import kotlinx.android.synthetic.main.activity_login.passwordEditText
import kotlinx.android.synthetic.main.activity_login.passwordInput
import kotlinx.android.synthetic.main.activity_login.signInButton

class LoginActivity : AppCompatActivity() {

    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signInButton.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        emailInput.error = null
        passwordInput.error = null

        val emailStr = emailEditText.text.toString()
        val passwordStr = passwordEditText.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            passwordInput.error = getString(R.string.error_invalid_password)
            focusView = passwordInput
            cancel = true
        }

        if (TextUtils.isEmpty(emailStr)) {
            emailInput.error = getString(R.string.error_field_required)
            focusView = emailInput
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            emailInput.error = getString(R.string.error_invalid_email)
            focusView = emailInput
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return (password.length > 4)
    }

    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) :
        AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            return mEmail == "user@example.com" && mPassword == "password"
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null

            if (success!!) {
                val intent = Intent(applicationContext, ApiDriverActivity::class.java)
                startActivity(intent)

                finish()
            } else {
                passwordInput.error = getString(R.string.error_incorrect_password)
                passwordInput.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
        }
    }
}
