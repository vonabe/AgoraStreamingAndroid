package ru.vonabe.audiostreaming.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.btnLogin
import kotlinx.android.synthetic.main.login_activity.txtEng
import kotlinx.android.synthetic.main.login_activity.txtRu
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.Utils
import ru.vonabe.audiostreaming.livedata.LanguageViewModelJavaV1
import ru.vonabe.audiostreaming.livedata.StockLiveDataV1
import ru.vonabe.audiostreaming.network.pojo.Registration
import ru.vonabe.audiostreaming.only.AGApplication

class SigninActivity : AppCompatActivity() {

    private var editLogin: AppCompatEditText? = null
    private var editEmail: AppCompatEditText? = null
    private var editPassword: AppCompatEditText? = null
    private var editPasswordConfirm: AppCompatEditText? = null
    private var errorOutput: TextView? = null
    private var progress: ProgressBar? = null
    private var isStreamer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

        setContentView(R.layout.activity_registration)

        val stockModel = StockLiveDataV1.getInstance()
        val model = ViewModelProviders.of(this).get(LanguageViewModelJavaV1::class.java)

        editLogin = findViewById(R.id.editLogin)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm)
        errorOutput = findViewById(R.id.errorOutput)
        progress = findViewById(R.id.progressRegistration)

        editLogin?.addTextChangedListener { loginEdit ->
            loginEdit?.let {
                editPassword?.text?.let { passwordEdit ->
                    btnLogin.isEnabled = passwordEdit.length >= 6 && loginEdit.length >= 4
                }
            }
            false
        }
        editPassword?.addTextChangedListener { passwordEdit ->
            passwordEdit?.let {
                editLogin?.text?.let { loginEdit ->
                    btnLogin.isEnabled = loginEdit.length >= 4 && passwordEdit.length >= 6
                }
            }
            false
        }

        var underline = true
        var txtLicense = txtLicense
        var spannable = SpannableString(resources.getString(R.string.license_registration))
        var spanclick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(baseContext, "Click License Registration", Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = underline
//                Toast.makeText(baseContext, "updateDrawState", Toast.LENGTH_SHORT).show()
//                println("UpdateDrawState")
            }
        }
        spannable.setSpan(
            spanclick,
            spannable.indexOf("Пользовательского соглашения."),
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtLicense.text = spannable
        txtLicense.movementMethod = LinkMovementMethod.getInstance()
        txtLicense.highlightColor = Color.TRANSPARENT

//        txtLicense.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                underline = false
//            } else if (event.action == MotionEvent.ACTION_UP) {
//                underline = true
//            }
//            false
//        }

//        txtLicense.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val txtrus: TextView = txtRu
        val txteng: TextView = txtEng

        val colorEnable = ContextCompat.getColor(this, R.color.text_enabled)
        val colorDisable = ContextCompat.getColor(this, R.color.text_disabled)

        txtrus.setTextColor(colorEnable)
        txteng.setTextColor(colorDisable)

        val onClickListener = View.OnClickListener {
            when (it) {
                txtrus -> {
                    txtrus.setTextColor(colorEnable)
                    txteng.setTextColor(colorDisable)
                    AGApplication.saveLanguage("rus")
                    model.data?.postValue("rus")
                    stockModel.postData("rus")
                }
                txteng -> {
                    txteng.setTextColor(colorEnable)
                    txtrus.setTextColor(colorDisable)
                    AGApplication.saveLanguage("eng")
//                    model.data?.postValue("eng")
                    stockModel.postData("eng")
                }
                btnLogin -> {
                    progress?.visibility = ProgressBar.VISIBLE
                    this.login()
                    startActivity(Intent(this, HomeHearer::class.java))
                }
            }
        }

        stockModel.observe(this, Observer { language ->
            Log.e("SigninActivity", "Language $language")
            if (language == "rus") {
                txtrus.setTextColor(colorEnable)
                txteng.setTextColor(colorDisable)
            } else {
                txteng.setTextColor(colorEnable)
                txtrus.setTextColor(colorDisable)
            }
        })

//        model.data?.observe(this, Observer<String> {
//            //            if (AGApplication.getLanguage() == "eng")txteng.performClick() else txtrus.performClick()
//            if (it == "rus") {
//                txtrus.setTextColor(colorEnable)
//                txteng.setTextColor(colorDisable)
//            } else {
//                txteng.setTextColor(colorEnable)
//                txtrus.setTextColor(colorDisable)
//            }
//        })

        txtrus.setOnClickListener(onClickListener)
        txteng.setOnClickListener(onClickListener)
        btnLogin.setOnClickListener(onClickListener)

//        if (AGApplication.getLanguage() == "eng")
//            txteng.performClick()

        var btnStreamer = findViewById<View>(R.id.circle_streamer)
        var btnHearer = findViewById<View>(R.id.circle_hearer)

        btnStreamer.setBackgroundResource(R.drawable.btn_circle_streamer_disable)
        btnHearer.setBackgroundResource(R.drawable.btn_circle_hearer_enabled)

        btnStreamer.setOnClickListener {
            btnStreamer.setBackgroundResource(R.drawable.btn_circle_streamer_enabled)
            btnHearer.setBackgroundResource(R.drawable.btn_circle_hearer_disable)
            isStreamer = true
        }

        btnHearer.setOnClickListener {
            btnHearer.setBackgroundResource(R.drawable.btn_circle_hearer_enabled)
            btnStreamer.setBackgroundResource(R.drawable.btn_circle_streamer_disable)
            isStreamer = false
        }

//        val editUsername = findViewById<TextInputEditText>(R.id.editTextUsername)
//        val editEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
//        val editPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
//        val editPasswordconfirm = findViewById<TextInputEditText>(R.id.editTextPasswordConfirm)
//
//        toggleStreamerOrUser.onChangeListener = object : ToggleSwitch.OnChangeListener {
//            override fun onToggleSwitchChanged(position: Int) {}
//        }
//        toggleStreamerOrUser.setCheckedPosition(1)
//
//        btnConfirm.setOnClickListener {
//            var validData = true
//            if (editUsername.text.isNullOrEmpty()) {
//                editUsername.error = "Введите свой никнейм"
//                validData = false
//            }
//            if(editEmail.text.isNullOrEmpty()){
//                editEmail.error = "Укажите свой email"
//                validData = false
//            }
//            if(editPassword.text.isNullOrEmpty()){
//                editPassword.error = "Введите пароль"
//                validData = false
//            }
//            if(editPasswordconfirm.text.isNullOrEmpty()){
//                editPasswordconfirm.error = "Подтвердите введенный пароль"
//                validData = false
//            }
//
//            if(!editPassword.text.isNullOrEmpty() && !editPasswordconfirm.text.isNullOrEmpty()){
//                if(editPassword.text != editPasswordconfirm.text){
//                    validData = false
//                    editPasswordconfirm.error = "Неправильно введен пароль"
//                }
//            }
//
//            if(validData){
//                val registration = AGApplication.service.registration(
//                    email = RequestBody.create(MediaType.get("multipart/form-data"), editEmail.text.toString()),
//                    password = RequestBody.create(MediaType.get("multipart/form-data"), editPassword.text.toString()),
//                    is_streamer = RequestBody.create(
//                        MediaType.get("multipart/form-data"),
//                        toggleStreamerOrUser.getCheckedPosition().toString()
//                    ),
//                    language = RequestBody.create(MediaType.get("multipart/form-data"), 2.toString())
//                )
//                registration.enqueue(object: retrofit2.Callback<Registration> {
//                    override fun onFailure(call: Call<Registration>, t: Throwable) {
//                    }
//                    override fun onResponse(call: Call<Registration>, response: Response<Registration>) {
//                    }
//                })
//            }
//
//        }

    }

    private fun login() {
        var validData = true
        if (editLogin?.text.isNullOrEmpty()) {
            errorOutput?.text = "Введите свой никнейм"
            validData = false
        }
        if (editEmail?.text.isNullOrEmpty()) {
            errorOutput?.text = "Укажите свой email"
            validData = false
        }
        if (editPassword?.text.isNullOrEmpty()) {
            errorOutput?.text = "Введите пароль"
            validData = false
        }
        if (editPasswordConfirm?.text.isNullOrEmpty()) {
            errorOutput?.text = "Подтвердите введенный пароль"
            validData = false
        }

        if (!editPassword?.text.isNullOrEmpty() && !editPasswordConfirm?.text.isNullOrEmpty()) {
            if (editPassword?.text.toString() != editPasswordConfirm?.text.toString()) {
                validData = false
                errorOutput?.text = "Неправильно введен пароль"
            }
        }

        if (!Utils.isValidEmailAddress(editEmail!!.text.toString())) {
            validData = false
            errorOutput?.text = "Неправильный email"
        }

        if (validData) {
            errorOutput?.text = ""
            val registration = AGApplication.service.registration(
                email = RequestBody.create(MediaType.get("multipart/form-data"), editEmail?.text.toString()),
                password = RequestBody.create(MediaType.get("multipart/form-data"), editPassword?.text.toString()),
                is_streamer = RequestBody.create(
                    MediaType.get("multipart/form-data"),
                    if (isStreamer) 1.toString() else 0.toString()
                ),
                language = RequestBody.create(
                    MediaType.get("multipart/form-data"),
                    if (AGApplication.getLanguage() == "rus") 2.toString() else 1.toString()
                )
            )
            registration.enqueue(object : retrofit2.Callback<Registration> {
                override fun onFailure(call: Call<Registration>, t: Throwable) {
                    Toast.makeText(this@SigninActivity, "Error ${t.message}", Toast.LENGTH_LONG).show()
//                    loading.visibility = View.GONE
                    t.printStackTrace()
                    progress?.visibility = ProgressBar.GONE
                }

                override fun onResponse(call: Call<Registration>, response: Response<Registration>) {
                    Toast.makeText(this@SigninActivity, "${response.body().toString()}", Toast.LENGTH_LONG).show()
                    progress?.visibility = ProgressBar.GONE
                }
            })
        } else {
            progress?.visibility = ProgressBar.GONE
        }
    }

}