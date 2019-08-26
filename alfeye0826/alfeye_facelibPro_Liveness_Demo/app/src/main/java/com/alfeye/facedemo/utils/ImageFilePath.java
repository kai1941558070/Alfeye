package com.alfeye.facedemo.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageFilePath {

    /**
     * 从intent中获取图片的content uri，如果intent中的uri数据是file uri，那么就需要进行转换<br>
     * get the image content uri( content://) from intent, if the data uri is
     * file uri(file:///), then need to convert
     * 
     * @param context
     *            上下文<br>
     *            application or activity context
     * @param intent
     *            intent对象（由相册应用传入）<br>
     *            Intent object( pass in from Gallery application)
     * @return content://格式的uri<br>
     *         the content uri of image
     */
    public static Uri getImageContentUri(Context context, Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'")
                        .append(")");
                Cursor cur = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID }, buff.toString(), null, null);

                if (cur != null && cur.moveToFirst()) {
                    int id = cur.getInt(cur.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + id);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}