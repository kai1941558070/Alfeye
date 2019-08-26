// Generated code from Butter Knife. Do not modify!
package com.alfeye.facedemo.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.alfeye.facedemo.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Face1NRecogActivity_ViewBinding implements Unbinder {
  private Face1NRecogActivity target;

  @UiThread
  public Face1NRecogActivity_ViewBinding(Face1NRecogActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Face1NRecogActivity_ViewBinding(Face1NRecogActivity target, View source) {
    this.target = target;

    target.ivBmp = Utils.findRequiredViewAsType(source, R.id.iv_bmp, "field 'ivBmp'", ImageView.class);
    target.ivCardbmp = Utils.findRequiredViewAsType(source, R.id.iv_cardbmp, "field 'ivCardbmp'", ImageView.class);
    target.tvFaceInfo = Utils.findRequiredViewAsType(source, R.id.tv_face_info, "field 'tvFaceInfo'", TextView.class);
    target.tvLivenessStatus = Utils.findRequiredViewAsType(source, R.id.tv_liveness_status, "field 'tvLivenessStatus'", TextView.class);
    target.surfaceViewCamera = Utils.findRequiredViewAsType(source, R.id.surfaceViewCamera, "field 'surfaceViewCamera'", SurfaceView.class);
    target.surfaceViewOverlap = Utils.findRequiredViewAsType(source, R.id.surfaceViewOverlap, "field 'surfaceViewOverlap'", SurfaceView.class);
    target.btnBack = Utils.findRequiredViewAsType(source, R.id.btn_back, "field 'btnBack'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    Face1NRecogActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ivBmp = null;
    target.ivCardbmp = null;
    target.tvFaceInfo = null;
    target.tvLivenessStatus = null;
    target.surfaceViewCamera = null;
    target.surfaceViewOverlap = null;
    target.btnBack = null;
  }
}
