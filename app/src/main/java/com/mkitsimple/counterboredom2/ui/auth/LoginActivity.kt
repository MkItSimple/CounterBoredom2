package com.mkitsimple.counterboredom2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
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

        initAnimation()

        buttonLogin.setOnClickListener { performLogin() }
        textViewBackToRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initAnimation() {
        val fromtopbottom = AnimationUtils.loadAnimation(this, R.anim.fromtopbottom)
        val fromtopbottomtwo = AnimationUtils.loadAnimation(this, R.anim.fromtopbottomtwo)
        val smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig)

        imageViewLogo.startAnimation(smalltobig)
        editTextEmail.startAnimation(fromtopbottom)
        editTextPassword.startAnimation(fromtopbottom)
        buttonLogin.startAnimation(fromtopbottomtwo)
        textViewBackToRegister.startAnimation(fromtopbottomtwo)
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
                    //longToast(it.toString())
                } else {
                    longToast(it.toString())
                }
            })
        }
    }
}
