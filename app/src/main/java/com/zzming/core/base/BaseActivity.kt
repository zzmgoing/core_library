package com.zzming.core.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.zzming.core.common.Constant
import com.zzming.core.extension.A_TAG
import com.zzming.core.extension.logError
import com.zzming.core.utils.APPUtils
import com.zzming.core.utils.ViewUtils

/**
 * @author ZhongZiMing
 * @time 2020/6/5 18:57
 * @description Activity基类
 **/
abstract class BaseActivity : AppCompatActivity(), ViewListener {

    /**
     * 判断当前Activity是否在前台
     */
    var isActive: Boolean = false

    /**
     * 当前Activity的实例
     */
    var activity: BaseActivity? = null

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        beforeContentView()
        initContentView()
        initViewModel()
        initView()
    }

    /**
     * 绑定view之前
     */
    fun beforeContentView() {}

    /**
     * 绑定view
     */
    abstract fun initContentView()

    /**
     * 初始化view
     */
    abstract fun initView()

    /**
     * 初始化ViewModel
     */
    open fun initViewModel() {}

    /**************生命周期****************/

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity = null
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hieKeyboard(currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(APPUtils.attachBaseContext(newBase))
    }

    /**************生命周期****************/


    /**
     * Loading
     */
    override fun showLoadingState(type: Int) {

    }

    /**
     * LoadMore
     */
    override fun showLoadMoreState(type: Int) {

    }

    /**
     * 页面跳转
     */
    override fun startActivity(toTag: String, rootTag: String?, bundle: Bundle?, type: String?) {
        try {
            val intent = Intent(this, Class.forName(toTag))
            val mBundle: Bundle = Bundle().apply {
                putString(Constant.PAGE_FROM_TAG, A_TAG)
                putString(Constant.PAGE_FROM_TYPE, type)
                putString(Constant.PAGE_ROOT_TAG, rootTag ?: A_TAG)
            }
            bundle?.let { mBundle.putAll(it) }
            intent.putExtras(mBundle)
            startActivityForResult(intent, Constant.PAGE_REQUEST_CODE)
        } catch (e: ClassNotFoundException) {
            logError("$A_TAG 中跳转页面失败，未找到$toTag 请检查配置", e)
        }
    }

    /**
     * 处理返回页面逻辑
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Constant.PAGE_REQUEST_CODE == requestCode && Constant.PAGE_RESULT_CODE == resultCode) {
            val rootTag = data?.getStringExtra(Constant.PAGE_ROOT_TAG)
            if (!rootTag.isNullOrEmpty() && A_TAG != rootTag) {
                onBackPressed()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * onBackPressed
     */
    override fun onBackPressed() {
        super.onBackPressed()
        backToRootPage()
    }

    /**
     * 回到根页面
     */
    private fun backToRootPage() {
        val intent = Intent()
        val rootTag = getIntent().extras?.getString(Constant.PAGE_ROOT_TAG)
        if (!rootTag.isNullOrEmpty()) {
            intent.putExtra(Constant.PAGE_ROOT_TAG, rootTag)
            setResult(Constant.PAGE_RESULT_CODE, intent)
        }
        finish()
    }

}