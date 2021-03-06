package com.reactnativedownload;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = DownloadModule.NAME)
public class DownloadModule extends ReactContextBaseJavaModule {
    public static final String NAME = "Download";
    private static ReactApplicationContext reactContext;

    public DownloadModule(ReactApplicationContext context) {
      super(reactContext);
      reactContext = context;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


  @ReactMethod
  public void downloadFile(String url, String name, Callback callback) {
    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

    if(!name.contains(".")){
      String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
      name = name.concat("." + fileExtension);
    }
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    request.setTitle(name);
    request.setVisibleInDownloadsUi(true);
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name);
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
    request.allowScanningByMediaScanner();
    DownloadManager downloadManager = (DownloadManager) reactContext.getSystemService(Context.DOWNLOAD_SERVICE);
    long downloadId = downloadManager.enqueue(request);

    reactContext.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, new ContentObserver(new Handler()) {
      boolean flag = false;
      @Override
      public void onChange(boolean selfChange, Uri uri) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        try (Cursor cursor = downloadManager.query(query)) {
          if (cursor != null && cursor.moveToFirst()) {
            switch (cursor.getInt(cursor.getColumnIndex((DownloadManager.COLUMN_STATUS)))){
              case DownloadManager.STATUS_FAILED:
                if(flag) return;
                reactContext.getContentResolver().unregisterContentObserver(this);
                callback.invoke();
                cursor.close();
                flag = true;
                break;
              case DownloadManager.STATUS_SUCCESSFUL:
                if(flag) return;
                reactContext.getContentResolver().unregisterContentObserver(this);
                callback.invoke("success");
                cursor.close();
                flag = true;
                break;
              default:
                break;
            }
          }
          }
        }
    });
  }
}
