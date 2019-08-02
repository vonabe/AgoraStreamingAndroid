package ru.vonabe.audiostreaming.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ru.vonabe.audiostreaming.R

class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

        setContentView(R.layout.activity_registration)

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

}