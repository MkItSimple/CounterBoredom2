package com.mkitsimple.counterboredom2.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mkitsimple.counterboredom2.BaseApplication
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.ui.main.MainActivity
import com.mkitsimple.counterboredom2.utils.Coroutines
import com.mkitsimple.counterboredom2.utils.longToast
import com.mkitsimple.counterboredom2.utils.toast
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ( this.applicationContext as BaseApplication).appComponent
            .newAuthComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory)[AuthViewModel::class.java]

        buttonLogin.setOnClickListener { performLogin() }
        textViewBackToRegister.setOnClickListener{ finish() }
    }

    private fun performLogin() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Please fill out email/pw.")
            return
        }

        Coroutines.main {
            viewModel.performLogin(email, password)
            viewModel.loginResult?.observe(this, Observer {
                if (it == true) {
                    startActivity(intentFor<MainActivity>().clearTask().newTask())
                } else {
                    longToast("Failed to log in: ${it}")
                }
            })
        }
    }

//    private fun performPair(){
//        Coroutines.main {
//            viewModel.performPair()
//            viewModel.pairResult?.observe(this, Observer {
//                if (it.first)
//                    toast(it.second)
//            })
//        }
//    }
}
