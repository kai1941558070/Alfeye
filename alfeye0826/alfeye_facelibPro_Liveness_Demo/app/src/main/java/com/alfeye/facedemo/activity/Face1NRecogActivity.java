package com.alfeye.facedemo.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfeye.a1io.A1IoDevBaseUtil;
import com.alfeye.a1io.A1IoDevManager;
import com.alfeye.facedemo.R;
import com.bumptech.glide.Glide;
import com.facelib.activity.BaseCameraAndFaceLivenessRecogActivity;
import com.facelib.entity.ResultFaceFS;
import com.facelib.faceRecog.Face1NRecogManager;
import com.facelib.faceRecog.OnFaceRecogListener;
import com.facelib.faceRecog.OnLoadFeatureListener;
import com.facelib.faceUtil.FaceEIS;
import com.facelib.utils.CameraProxy;
import com.facelib.utils.FaceConstants;
import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceOrientation;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 1:N  人脸比对
 */
public class Face1NRecogActivity extends BaseCameraAndFaceLivenessRecogActivity {

    private static final String TAG = "Face1NRecogActivity";

    @BindView(R.id.iv_bmp)
    ImageView ivBmp;
    @BindView(R.id.iv_cardbmp)
    ImageView ivCardbmp;
    @BindView(R.id.tv_face_info)
    TextView tvFaceInfo;
    @BindView(R.id.tv_liveness_status)
    TextView tvLivenessStatus;
    @BindView(R.id.surfaceViewCamera)
    SurfaceView surfaceViewCamera;
    @BindView(R.id.surfaceViewOverlap)
    SurfaceView surfaceViewOverlap;
    @BindView(R.id.btn_back)
    Button btnBack;

    private Face1NRecogManager face1NRecogManager;
    private A1IoDevBaseUtil a1IoDevBaseUtil;

    @Override
    protected int getDefaultCameraFacing() {
        return CAMERA_FACING_FRONT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_face1n_recog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        /**
         * 初始化补光灯工具类
         */
        a1IoDevBaseUtil = A1IoDevManager.initIOManager();
        /**
         * 查询可用摄像头数量
         * 目前黑白红外摄像头id为0，彩色摄像头id为1。
         */
        int numberOfCameras = Camera.getNumberOfCameras();
        Log.d("zhangjikai", "摄像头数量:" + numberOfCameras);

        face1NRecogManager = Face1NRecogManager.ins();
        //加载特征库
        face1NRecogManager.loadFeatureInfos(new OnLoadFeatureListener() {
            @Override
            public void onLoaded(int count) {
                if (count == 0) {
                    Toast.makeText(Face1NRecogActivity.this, "请先加载人脸库", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        face1NRecogManager.startFaceRecog();
        /*
        补光灯设置为20
         */
        a1IoDevBaseUtil.openLED(20);
        /*
        打开红外
         */
        a1IoDevBaseUtil.openIRDA();
    }

    @Override
    protected void onPause() {
        super.onPause();
        face1NRecogManager.stopFaceRecog();
    }

    @Override
    protected int getFaceOrientation(CameraProxy cameraProxy) {
        //A1 设备此处为  StFaceOrientation.ST_FACE_RIGHT;

        return StFaceOrientation.ST_FACE_RIGHT;
    }

    @Override
    protected void onTrackFace(byte[] data, StFace[] faces) {
        super.onTrackFace(data, faces);

        if (faces == null) {
            //找不到人脸
            tvLivenessStatus.setText("");
        }
    }

    @Override
    public void onLivenessResult(final boolean liveness, byte[] bytes, StFace face) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLivenessStatus.setText(liveness ? "活体真人" : "图片攻击");
            }
        });

        //人证比对
        if (liveness && !face1NRecogManager.isFaceRecoging()) {
            face1NRecogManager.startFaceRecog();
            face1NRecogManager.runFaceRecog(bytes, getCameraProxy(), face, onFaceRecogListener);
        }
    }

    private OnFaceRecogListener onFaceRecogListener = new OnFaceRecogListener() {
        private long curTime;

        @Override
        public void onStartFaceRecog() {
            curTime = System.currentTimeMillis();
        }

        @Override
        public void onFaceRecoged(FaceEIS faceEIS, ResultFaceFS resultFaceFS) {
            Log.d(TAG, "比对结果: " + resultFaceFS.getPictureName() + " 相似度：" + resultFaceFS.getFaceFS());

            if (resultFaceFS.getFaceFS() >= FaceConstants.ST_THRESHOLD) {
                //比对成功
                //比对的现场照
                //faceEIS.getBitmap()
            }

            Glide.with(Face1NRecogActivity.this).load(resultFaceFS.getFilePath()).into(ivCardbmp);
            ivBmp.setImageBitmap(faceEIS.getBitmap());
            String faceFs = String.format("比对结果：%6f", resultFaceFS.getFaceFS());
            tvFaceInfo.setText(faceFs);
        }

        @Override
        public void onFaceRecogError(int i, String s) {
            Log.d(TAG, "onFaceRecogError: " + s);
        }

        @Override
        public void onEndFaceRecog() {
            Log.d(TAG, "比对时间: " + (System.currentTimeMillis() - curTime));
        }
    };

    @Override
    protected void onDestroy() {
        /**
         * 释放资源
         */
        face1NRecogManager.stopFaceRecog();
        a1IoDevBaseUtil.openLED(0);
        a1IoDevBaseUtil.closeIRDA();
        onFaceRecogListener = null;
        super.onDestroy();
    }

    /**
     * 测试红外摄像机是否开启
     */
//    @Override
//    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Camera camera = Camera.open(0);
//                try {
//                    camera.setPreviewDisplay(holder);
//                    camera.setPreviewCallback(new Camera.PreviewCallback() {
//                        @Override
//                        public void onPreviewFrame(byte[] bytes, Camera camera) {
//
//                        }
//                    });
//                    camera.setDisplayOrientation(90);
//                    camera.startPreview();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//    }
}
