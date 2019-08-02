package ru.vonabe.audiostreaming.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.login_activity.*
import ru.vonabe.audiostreaming.R

class LoginSignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

        setContentView(R.layout.login_activity)

        val txtrus: TextView = txtRu
        val txteng: TextView = txtEng

        val editLogin: AppCompatEditText = editLogin
        val editPassword: AppCompatEditText = editPassword

        val textpassword = SpannableString(txtReceivePassword.text)
        textpassword.setSpan(UnderlineSpan(), 0, textpassword.length, 0)
        txtReceivePassword.text = textpassword

        val textregistration = SpannableString(txtRegistration.text)
        textregistration.setSpan(UnderlineSpan(), 0, textregistration.length, 0)
        txtRegistration.text = textregistration

        val colorEnable = ContextCompat.getColor(this, R.color.text_enabled)
        val colorDisable = ContextCompat.getColor(this, R.color.text_disabled)

        txtrus.setTextColor(colorEnable)
        txteng.setTextColor(colorDisable)

        val onClickListener = View.OnClickListener {
            when (it) {
                txtrus -> {
                    txtrus.setTextColor(colorEnable)
                    txteng.setTextColor(colorDisable)
                }
                txteng -> {
                    txteng.setTextColor(colorEnable)
                    txtrus.setTextColor(colorDisable)
                }
                btnLogin -> {

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

        editLogin.addTextChangedListener { loginEdit ->
            loginEdit?.let {
                editPassword.text?.let { passwordEdit ->
                    btnLogin.isEnabled = passwordEdit.length >= 6 && loginEdit.length >= 4
                }
            }
            false
        }

        editPassword.addTextChangedListener { passwordEdit ->
            passwordEdit?.let {
                editLogin.text?.let { loginEdit ->
                    btnLogin.isEnabled = loginEdit.length >= 4 && passwordEdit.length >= 6
                }
            }
            false
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