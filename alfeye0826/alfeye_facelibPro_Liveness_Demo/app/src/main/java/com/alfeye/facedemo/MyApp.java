package com.alfeye.facedemo;

import android.app.Application;

import com.facelib.faceConfig.FaceLicConfig;
import com.facelib.faceRecog.Face1NRecogManager;
import com.facelib.utils.FaceUtils;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FaceUtils.startRegLicense(this, new FaceLicConfig() {
            @Override
            public int getLicMode() {
                return FACE_LIC_NETWOEK_MODE;
            }

            @Override
            public String getCompanyCode() {
                return "10010";
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Face1NRecogManager.ins().destroy();
    }
}
