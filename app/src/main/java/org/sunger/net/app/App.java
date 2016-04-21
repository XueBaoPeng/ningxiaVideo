package org.sunger.net.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.squareup.okhttp.OkHttpClient;

import org.sunger.net.entity.OauthUserEntity;
import org.sunger.net.support.okhttp.OkHttpClientManager;
import org.sunger.net.util.FileUtils;
import org.sunger.net.utils.DeviceUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by sunger on 2015/10/27.
 */
public class App extends Application {

    private static final int CONNECT_TIMEOUT_MILLIS = 10 * 1000; // 15s
    private static final int READ_TIMEOUT_MILLIS = 15 * 1000; // 20s
    private static App instance;
    private OkHttpClient okHttpClient;
    private OauthUserEntity entity;

    public static final String OPLAYER_CACHE_BASE = Environment.getExternalStorageDirectory() + "/oplayer";
    public static final String OPLAYER_VIDEO_THUMB = OPLAYER_CACHE_BASE + "/thumb/";
    public static final String PREF_KEY_FIRST = "application_first";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        initOkHttp();
        DeviceUtils.init(this);
    }
    private void init() {
        FileUtils.createIfNoExists(OPLAYER_CACHE_BASE);
        FileUtils.createIfNoExists(OPLAYER_VIDEO_THUMB);
    }
    private void initOkHttp() {
        okHttpClient =
                OkHttpClientManager.getInstance().getOkHttpClient();
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }


    public void setOauth(OauthUserEntity entity) {
        this.entity = entity;
    }

    public OauthUserEntity getOauthUserEntity() {
        return entity;
    }

    public static Context getContext() {
        return instance;
    }
    public static App getInstance() {
        return instance;
    }
}
