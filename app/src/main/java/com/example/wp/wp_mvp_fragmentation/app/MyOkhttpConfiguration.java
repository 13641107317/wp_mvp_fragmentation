package com.example.wp.wp_mvp_fragmentation.app;

import android.content.Context;

import com.jess.arms.di.module.ClientModule;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by wangpeng on 2018/5/31.
 * 这里可以自己自定义配置Okhttp的参数
 */
class MyOkhttpConfiguration implements ClientModule.OkhttpConfiguration {
    @Override
    public void configOkhttp(Context context, OkHttpClient.Builder okhttpBuilder) {
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        //开启使用一行代码监听 Retrofit／Okhttp 上传下载进度监听,以及 Glide 加载进度监听
        // 详细使用方法查看 https://github.com/JessYanCoding/ProgressManager
        me.jessyan.progressmanager.ProgressManager.getInstance().with(okhttpBuilder);
    }
}
