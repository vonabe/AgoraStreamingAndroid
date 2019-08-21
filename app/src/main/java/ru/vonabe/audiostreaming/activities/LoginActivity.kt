package ru.vonabe.audiostreaming.activities

import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.login_activity.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.network.pojo.User
import ru.vonabe.audiostreaming.only.AGApplication

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

        setContentView(R.layout.activity_login)

        txtReceivePassword?.paintFlags = txtReceivePassword.paintFlags and UNDERLINE_TEXT_FLAG

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        login.isEnabled = true

        login.setOnClickListener {

            if (!username.text.isNullOrEmpty() and !password.text.isNullOrEmpty()) {
                loading.visibility = View.VISIBLE
                AGApplication.service.login(
                    email = RequestBody.create(MediaType.get("multipart/form-data"), username.text.toString()),
                    password = RequestBody.create(MediaType.get("multipart/form-data"), password.text.toString())
                )
                    .enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            Toast.makeText(this@LoginActivity, "${response.body().toString()}", Toast.LENGTH_LONG)
                                .show()
                            loading.visibility = View.GONE
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, "Error ${t.message}", Toast.LENGTH_LONG).show()
                            loading.visibility = View.GONE
                            t.printStackTrace()
                        }
                    })
            }

        }

    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
