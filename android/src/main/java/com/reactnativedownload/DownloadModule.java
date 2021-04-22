package com.reactnativedownload;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

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
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    request.setTitle(name);
    request.setVisibleInDownloadsUi(true);
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name);
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
    request.allowScanningByMediaScanner();
    DownloadManager downloadManager = (DownloadManager) reactContext.getSystemService(Context.DOWNLOAD_SERVICE);
    long downloadId = downloadManager.enqueue(request);
    reactContext.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, new ContentObserver(new Handler()) {
      @Override
      public void onChange(boolean selfChange, Uri uri) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        try (Cursor cursor = downloadManager.query(query)) {
          if (cursor != null && cursor.moveToFirst()) {
            switch (cursor.getInt(cursor.getColumnIndex((DownloadManager.COLUMN_STATUS)))){
              case DownloadManager.STATUS_FAILED:
                reactContext.getContentResolver().unregisterContentObserver(this);
                callback.invoke();
                cursor.close();
                break;
              case DownloadManager.STATUS_SUCCESSFUL:
                reactContext.getContentResolver().unregisterContentObserver(this);
                callback.invoke("success");
                cursor.close();
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
