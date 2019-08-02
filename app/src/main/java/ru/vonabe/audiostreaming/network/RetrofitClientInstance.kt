package ru.vonabe.audiostreaming.network

import android.os.Build
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vonabe.audiostreaming.BuildConfig
import java.util.concurrent.TimeUnit

class RetrofitClientInstance {

    companion object {

        private val BASE_URL = "http://sablist.ratsys.ru/"
        private var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            if (retrofit == null) {
                val gson = GsonBuilder().serializeNulls().create()

                val clientBuilder = OkHttpClient.Builder()

                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                clientBuilder.addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("android", "API " + Build.VERSION.SDK_INT)
                        .addHeader("version", "" + BuildConfig.VERSION_CODE).build()
                    chain.proceed(request)
                }

                clientBuilder.addInterceptor(loggingInterceptor)
                clientBuilder.connectTimeout(10000, TimeUnit.MILLISECONDS)
                clientBuilder.readTimeout(10000, TimeUnit.MILLISECONDS)
                clientBuilder.writeTimeout(10000, TimeUnit.MILLISECONDS)
                clientBuilder.connectTimeout(5000, TimeUnit.MILLISECONDS)
                clientBuilder.retryOnConnectionFailure(true)

                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                return retrofit!!
            } else {
                return retrofit!!
            }
        }
    }


}