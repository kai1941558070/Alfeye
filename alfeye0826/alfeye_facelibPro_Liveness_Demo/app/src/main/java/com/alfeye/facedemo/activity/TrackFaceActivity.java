package com.alfeye.facedemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfeye.facedemo.R;
import com.facelib.activity.BaseCameraAndFaceRecogActivity;
import com.facelib.utils.CameraProxy;
import com.facelib.utils.FaceUtils;
import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceFeature;
import com.sensetime.faceapi.StFaceOrientation;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 人脸 识别 演示
 * Created by DELL on 2018/9/18.
 */

public class TrackFaceActivity extends BaseCameraAndFaceRecogActivity {
    private static String TAG = "TrackFaceActivity";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TextView mFaceInfoTextView = null;
    //测试比对的人脸特征
    private StFaceFeature tmpFeature;

    @Override
    protected void onBeforeSetContentView(Bundle savedInstanceState) {
        super.onBeforeSetContentView(savedInstanceState);
//        //设置 状态栏透明
//        transparentStatusBar(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_track;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFaceInfoTextView = (TextView) findViewById(R.id.face_info_textview);

        findViewById(R.id.btn_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        //跳转比对演示界面
        findViewById(R.id.btn_verify_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrackFaceActivity.this, VerifyActivity.class));
            }
        });

        ImageView iv_pic = findViewById(R.id.iv_bmp);
        iv_pic.setImageResource(R.drawable.tmp_pic);
    }

    /**
     * 获取 算法识别的人脸方向
     *
     * @param cameraProxy
     * @return
     */
    @Override
    protected int getFaceOrientation(CameraProxy cameraProxy) {
        //A1 设备 此处设置为 : StFaceOrientation.ST_FACE_RIGHT

        return StFaceOrientation.ST_FACE_RIGHT;
    }

    private boolean isRunFlag = false;

    @Override
    protected void onTrackFace(byte[] data, StFace[] faces) {
        super.onTrackFace(data, faces);
        
        if (faces != null) {
            //跟踪到人脸,  faces[0]为最大的人脸. 如人脸与图片方向不一致，需要重新查找人脸

            if (!isRunFlag) {
                isRunFlag = true;

                long time = System.currentTimeMillis();

                //获取预览图片， 可以进行人脸识别比对
                runFaceComparison(data, faces[0]);

                Log.d(TAG, "onTrackFace-- time: " + (System.currentTimeMillis() - time));
            }
        }
    }

    //=================================================================================

    /**
     * 演示  人脸比对
     *
     * @param data
     * @param face
     */
    private void runFaceComparison(byte[] data, StFace face) {
        //缓存数据
        final byte[] bytes = Arrays.copyOf(data, data.length);

        Disposable disposable = Observable.just("1")
                .map(new Function<String, String>() {

                    @Override
                    public String apply(String s) throws Exception {
                        if (tmpFeature == null) {
                            // 获取比对图片人脸特征

                            Bitmap tmpBmp = BitmapFactory.decodeResource(getResources(), R.drawable.tmp_pic);
                            StFace[] faces = FaceUtils.queryFace(tmpBmp);
                            if (faces != null && faces.length > 0) {
                                tmpFeature = FaceUtils.getFaceFeature(tmpBmp, faces[0]);
                            }
                        }
                        return "";
                    }
                })
                .map(new Function<String, Float>() {
                    @Override
                    public Float apply(String s) throws Exception {
                        float result = 0f;

                        Bitmap cameraBmp = getCameraProxy().getCameraBmp(bytes);
                        StFace[] faces = FaceUtils.queryFace(cameraBmp);
                        if (faces != null && faces.length > 0) {
                            //获取人脸特征
                            StFaceFeature feature = FaceUtils.getFaceFeature(cameraBmp, faces[0]);

                            if (feature != null && tmpFeature != null) {
                                //两个人脸特征比对
                                result = FaceUtils.compareFeature(feature, tmpFeature);
                                Log.d(TAG, String.format("比对结果: %.5f", result));

                                feature.recycle();
                            }
                        }
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Float>() {
                    @Override
                    public void accept(Float result) throws Exception {
                        mFaceInfoTextView.setText(String.format("比对结果: %.5f", result));

                        isRunFlag = false;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isRunFlag = false;
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

}
