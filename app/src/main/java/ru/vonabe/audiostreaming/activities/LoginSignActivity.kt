package ru.vonabe.audiostreaming.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.login_activity.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vonabe.audiostreaming.CustomEditText
import ru.vonabe.audiostreaming.LanguageViewModel
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.network.pojo.User
import ru.vonabe.audiostreaming.only.AGApplication
import ru.vonabe.audiostreaming.only.LoginAndPassword

class LoginSignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

        setContentView(R.layout.login_activity)

        val model = ViewModelProviders.of(this).get(LanguageViewModel::class.java)

//        val editLoginInput = editLoginInput
//        val editPasswordInput = editPasswordInput

        val editLogin: CustomEditText = editLogin
        val editPassword: CustomEditText = editPassword
        val error: TextView = errorOutput

        val textpassword = SpannableString(txtReceivePassword.text)
        textpassword.setSpan(UnderlineSpan(), 0, textpassword.length, 0)
        txtReceivePassword.text = textpassword

        val textregistration = SpannableString(txtRegistration.text)
        textregistration.setSpan(UnderlineSpan(), 0, textregistration.length, 0)
        txtRegistration.text = textregistration

        val colorEnable = ContextCompat.getColor(this, R.color.text_enabled)
        val colorDisable = ContextCompat.getColor(this, R.color.text_disabled)

        val txtrus: TextView = txtRu
        val txteng: TextView = txtEng

        txtrus.setTextColor(colorEnable)
        txteng.setTextColor(colorDisable)

        val onClickListener = View.OnClickListener {
            when (it) {
                txtrus -> {
                    txtrus.setTextColor(colorEnable)
                    txteng.setTextColor(colorDisable)
                    AGApplication.saveLanguage("rus")
                    model.language.value = ("rus")
                }
                txteng -> {
                    txteng.setTextColor(colorEnable)
                    txtrus.setTextColor(colorDisable)
                    AGApplication.saveLanguage("eng")
                    model.language.value = ("eng")
                }
                btnLogin -> {
                    btnLogin.isEnabled = false
                    if (!editLogin.text.isNullOrEmpty() and !editPassword.text.isNullOrEmpty()) {
                        loading.visibility = View.VISIBLE
                        AGApplication.service.login(
                            email = RequestBody.create(MediaType.get("multipart/form-data"), editLogin.text.toString()),
                            password = RequestBody.create(
                                MediaType.get("multipart/form-data"),
                                editPassword.text.toString()
                            )
                        ).enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                Toast.makeText(
                                    this@LoginSignActivity,
                                    "${response.body().toString()}",
                                    Toast.LENGTH_LONG
                                ).show()

                                loading.visibility = View.GONE
                                btnLogin.isEnabled = true

                                if (response.isSuccessful && response.body() != null) {
                                    val body = response.body()
                                    if (body?.status == 1) {
                                        AGApplication.setUser(body)

                                        AGApplication.saveAuth(
                                            LoginAndPassword(
                                                login = editLogin?.text.toString(),
                                                password = editPassword?.text.toString()
                                            )
                                        )
                                        startActivity(Intent(this@LoginSignActivity, HomeHearer::class.java))
                                    } else {
                                        error.visibility = TextView.VISIBLE
                                        error.text = "Неверный логин или пароль"
                                    }
                                }
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(this@LoginSignActivity, "Error ${t.message}", Toast.LENGTH_LONG)
                                    .show()
                                loading.visibility = View.GONE
                                btnLogin.isEnabled = true
                            }
                        })
                    }
                }
                txtRegistration -> {
                    startActivity(Intent(this, SigninActivity::class.java))
                }
            }
        }
        txtrus.setOnClickListener(onClickListener)
        txteng.setOnClickListener(onClickListener)
        btnLogin.setOnClickListener(onClickListener)
        txtRegistration.setOnClickListener(onClickListener)

        if (AGApplication.getLanguage() == "eng")
            txteng.performClick()

        editLogin.addTextChangedListener { loginEdit ->
            loginEdit?.let {
                if (loginEdit.contains(" ")) {
                    editLogin.setError(true)
                    error.visibility = TextView.VISIBLE
                    error.text = "Не должно содержаться пробелов"
//                    editLoginInput.error = "Не должно содержаться пробелов"
                } else {
                    editLogin.setError(false)
                    error.visibility = TextView.GONE
                    error.text = ""
//                    editLoginInput.error = null
                    editPassword.text?.let { passwordEdit ->
                        btnLogin.isEnabled = passwordEdit.length >= 6 && loginEdit.length >= 4
//                        error.visibility = TextView.VISIBLE
//                        error.text = "Пароль должен быть больше или равен 6 символам"
                    }
                }
            }
            false
        }

        editPassword.addTextChangedListener { passwordEdit ->
            passwordEdit?.let {
                if (editLogin.text!!.contains(" ")) {
                    editLogin.setError(true)
                    error.visibility = TextView.VISIBLE
                    error.text = "Не должно содержаться пробелов"
//                    editLoginInput.error = "Не должно содержаться пробелов"
                } else {
                    editLogin.setError(false)
                    error.visibility = TextView.GONE
                    error.text = ""
                    editLogin.text?.let { loginEdit ->
                        btnLogin.isEnabled = loginEdit.length >= 4 && passwordEdit.length >= 6
                    }
                }
            }
            false
        }

        model.language.observe(this, Observer<String> {
            println("LiveData --> $it")
            if (it == "rus") {
                txtrus.setTextColor(colorEnable)
                txteng.setTextColor(colorDisable)
            } else {
                txteng.setTextColor(colorEnable)
                txtrus.setTextColor(colorDisable)
            }
        })

        AGApplication.getAuth()?.let {
            editLogin.setText(it.login)
            editPassword.setText(it.password)
        }

//        var radioGroup = radioGroup
//        radioGroup.setOnCheckedChangeListener { group, checkedId ->
//            Toast.makeText(this, "Select Language $checkedId", Toast.LENGTH_LONG).show()
//        }
//
//        btnLogin.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//
//        btnSign.setOnClickListener {
//            startActivity(Intent(this, SigninActivity::class.java))
//        }

    }


}