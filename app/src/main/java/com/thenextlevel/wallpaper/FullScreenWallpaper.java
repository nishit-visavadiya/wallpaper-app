package com.thenextlevel.wallpaper;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.thenextlevel.wallpaper.Model.WallpaperModel;

import java.io.File;
import java.io.IOException;

public class FullScreenWallpaper extends AppCompatActivity {

    Bitmap bitmap;
    PhotoView photoView;
    ProgressBar progressBar;
    Button setWallpaperButton, downloadWallpaperButton;

    String originalUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        getSupportActionBar().hide();

        photoView = findViewById(R.id.photoView);
        progressBar = findViewById(R.id.progressBar);
        setWallpaperButton = findViewById(R.id.setWallPaper);
        downloadWallpaperButton = findViewById(R.id.download_button);
        progressBar.setVisibility(View.VISIBLE);

        loadImage();

        progressBar.setVisibility(View.GONE);

        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper();
            }
        });

        downloadWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadWallpaper();
            }
        });
    }

    private void loadImage() {
        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");

        Glide.with(this)
                .load(originalUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(photoView);
    }

    private void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();

        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(FullScreenWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadWallpaper() {

        /*Uri uri = Uri.parse(originalUrl);

        BitmapDrawable drawable = (BitmapDrawable) photoView.getDrawable();
        bitmap = drawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File directory = new File(file.getAbsolutePath() + "Wallpaper");
        directory.mkdir();

        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(directory, fileName);

        Toast.makeText(this, "clicked!", Toast.LENGTH_SHORT).show();

        try {
            outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //File path = new File(originalUrl);
        String fileName = String.format("%d.jpg", System.currentTimeMillis());

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(originalUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription(fileName);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        downloadManager.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        Toast.makeText(this, "Downloading Start", Toast.LENGTH_SHORT).show();
    }
}