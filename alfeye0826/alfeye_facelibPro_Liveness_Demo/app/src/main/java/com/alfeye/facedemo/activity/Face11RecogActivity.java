package com.alfeye.facedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfeye.facedemo.R;
import com.alfeye.readcardlib.entity.CardInfo;
import com.alfeye.readcardlib.readcard.ReadCardUtil;
import com.facelib.activity.BaseCameraAndFaceLivenessRecogActivity;
import com.facelib.entity.ResultFaceFS;
import com.facelib.faceRecog.Face11RecogUtil;
import com.facelib.faceRecog.OnFaceRecogListener;
import com.facelib.faceUtil.FaceEIS;
import com.facelib.utils.CameraProxy;
import com.facelib.utils.FaceConstants;
import com.facelib.utils.FaceLibState;
import com.sensetime.faceapi.StFace;
import com.sensetime.faceapi.StFaceOrientation;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 1:1 人证比对
 */
public class Face11RecogActivity extends BaseCameraAndFaceLivenessRecogActivity implements ReadCardUtil.OnReadCardListener {

    private static final String TAG = "Face11RecogActivity";

    @BindView(R.id.iv_bmp)
    ImageView ivBmp;
    @BindView(R.id.iv_cardbmp)
    ImageView ivCardbmp;
    @BindView(R.id.tv_face_info)
    TextView tvFaceInfo;
    @BindView(R.id.tv_liveness_status)
    TextView tvLivenessStatus;

    private Face11RecogUtil face11RecogUtil;
    private ReadCardUtil readCardUtil;
    private CardInfo mCardInfo = null;

    @Override
    protected int getDefaultCameraFacing() {
        return CAMERA_FACING_FRONT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_face11_recog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        readCardUtil = new ReadCardUtil(this, this);

        face11RecogUtil = new Face11RecogUtil(new OnFaceRecogListener() {
            @Override
            public void onStartFaceRecog() {

            }

            @Override
            public void onFaceRecoged(FaceEIS faceEIS, ResultFaceFS resultFaceFS) {
                Log.d(TAG, "人证比对结果: " + resultFaceFS.getFaceFS());

                if (resultFaceFS.getFaceFS() >= FaceConstants.ST_THRESHOLD) {
                    //比对成功

                    //比对的现场照
                    //faceEIS.getBitmap()

                    Toast.makeText(Face11RecogActivity.this, "人证比对成功", Toast.LENGTH_SHORT).show();
                }

                //演示 结果
                ivBmp.setImageBitmap(faceEIS.getBitmap());

                String faceFs = String.format("比对结果：%6f", resultFaceFS.getFaceFS());
                tvFaceInfo.setText(faceFs);
            }

            @Override
            public void onFaceRecogError(int i, String s) {
                Log.d(TAG, "onFaceRecogError: " + s + " errNo：" + i);

                if (i == FaceLibState.ERROR_FACE11_INIT_FEATURE) {
                    startReadCard();
                }
            }

            @Override
            public void onEndFaceRecog() {
                startReadCard();
            }
        });
    }

    @Override
    protected int getFaceOrientation(CameraProxy cameraProxy) {
        //A1 设备此处为  StFaceOrientation.ST_FACE_RIGHT;

        return StFaceOrientation.ST_FACE_RIGHT;
    }

    @Override
    protected void onTrackFace(byte[] data, StFace[] faces) {
        super.onTrackFace(data, faces);
        Log.d(TAG, "mCardInfo: " + mCardInfo + " faces: " + faces);

//        //人证比对
//        if (mCardInfo != null && faces != null && !face11RecogUtil.isFaceRecoging()) {
//            face11RecogUtil.runFaceRecog(data, getCameraProxy(), faces[0]);
//        }

        if (faces == null) {
            //找不到人脸
            tvLivenessStatus.setText("");
        }
    }

    @Override
    public void onLivenessResult(final boolean liveness, byte[] bytes, StFace stFace) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLivenessStatus.setText(liveness ? "活体真人" : "图片攻击");
            }
        });

        //人证比对
        if (liveness && mCardInfo != null && !face11RecogUtil.isFaceRecoging()) {
            face11RecogUtil.runFaceRecog(bytes, getCameraProxy(), stFace);
        }
    }

    /**
     * 开始读卡
     */
    private void startReadCard() {
        mCardInfo = null;
        readCardUtil.startReadCard();

        Toast.makeText(this, "请刷身份证", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        face11RecogUtil.stopFaceRecog();
        startReadCard();
    }

    @Override
    protected void onPause() {
        super.onPause();

        face11RecogUtil.stopFaceRecog();
        readCardUtil.stopReadCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        face11RecogUtil.destroy();
        readCardUtil.destroy();
    }

    @Override
    public void onReadCardSucceed(String s, CardInfo cardInfo, Intent intent) {
        if (mCardInfo == null || !mCardInfo.getCardNum().equals(cardInfo.getCardNum())) {

            if (face11RecogUtil.initFeature(cardInfo.getPhoto())) {
                Log.d(TAG, "cardInfo: " + cardInfo.getCardNum() + " 开始人证比对");

                //
                mCardInfo = cardInfo;
                readCardUtil.stopReadCard();

                ivCardbmp.setImageBitmap(mCardInfo.getPhoto());

                //开始 人证比对
                face11RecogUtil.startFaceRecog();


                Toast.makeText(Face11RecogActivity.this, "开始人证比对", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReadCardFail(int i, String s) {

    }
}

