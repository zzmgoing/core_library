package com.zzming.core.net

import com.zzming.core.LibCore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author ZhongWei
 * @time 2020/7/15 16:57
 * @description
 **/
class RetrofitUtils private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RetrofitUtils() }
    }

    /**
     * Retrofit
     */
    var retrofit: Retrofit? = null
        get() = if (field == null) {
            defaultRetrofit()
        } else {
            field
        }

    /**
     * BASE_URL
     */
    var baseUrl: String? = null
        get() = if (field == null) {
            LibCore.BASE_URL ?: ""
        } else {
            field
        }

    /**
     * 初始化Retrofit
     */
    fun initRetrofit(retrofit: Retrofit): RetrofitUtils {
        this.retrofit = retrofit
        return this
    }

    /**
     * 设置BASE_URL
     */
    fun initBaseUrl(baseUrl: String): RetrofitUtils {
        this.baseUrl = baseUrl
        return this
    }

    /**
     * 默认Retrofit
     */
    private fun defaultRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(LibCore.BASE_URL ?: "")
            .client(defaultHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * OkHttpClient
     */
    private fun defaultHttpClient(): OkHttpClient {
        return LibCore.httpClient ?: OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(LogInterceptor())
            .connectTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

}