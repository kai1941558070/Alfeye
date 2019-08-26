// Generated code from Butter Knife. Do not modify!
package com.alfeye.facedemo.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.alfeye.facedemo.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Face11RecogActivity_ViewBinding implements Unbinder {
  private Face11RecogActivity target;

  @UiThread
  public Face11RecogActivity_ViewBinding(Face11RecogActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Face11RecogActivity_ViewBinding(Face11RecogActivity target, View source) {
    this.target = target;

    target.ivBmp = Utils.findRequiredViewAsType(source, R.id.iv_bmp, "field 'ivBmp'", ImageView.class);
    target.ivCardbmp = Utils.findRequiredViewAsType(source, R.id.iv_cardbmp, "field 'ivCardbmp'", ImageView.class);
    target.tvFaceInfo = Utils.findRequiredViewAsType(source, R.id.tv_face_info, "field 'tvFaceInfo'", TextView.class);
    target.tvLivenessStatus = Utils.findRequiredViewAsType(source, R.id.tv_liveness_status, "field 'tvLivenessStatus'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    Face11RecogActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ivBmp = null;
    target.ivCardbmp = null;
    target.tvFaceInfo = null;
    target.tvLivenessStatus = null;
  }
}
