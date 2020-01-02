package com.example.samrat.blutoothapp2;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button click, btn_uri, btn_remove;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    TextView tv_txtMsg;
    Boolean textFlag = false;
    private Uri filePath;
    BluetoothAdapter btAdapter;
    private static final int STORAGE_PERMISSION_CODE = 123;
    ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBluetooth();
        requestStoragePermission();
        initViews();
        sendFile();
        uri();
        remove();
    }

    private void remove() {
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = null;
                filePath = null;
                iv_image.setVisibility(View.GONE);
                btn_remove.setVisibility(View.GONE);
                tv_txtMsg.setText("No files selected. Please click on the button to choose file");

            }
        });
    }


    private void initViews() {
        btn_uri = (Button) findViewById(R.id.btn_uri);
        click = (Button) findViewById(R.id.btn_click);
        tv_txtMsg = (TextView) findViewById(R.id.tv_txtMsg);
        tv_txtMsg.setText("Please choose file to proceed");
        iv_image = (ImageView) findViewById(R.id.iv_image);
        btn_remove = (Button) findViewById(R.id.btn_remove);
    }

    private void initBluetooth() {

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth does not supported", Toast.LENGTH_SHORT).show();

        }
    }

    private void uri() {
        btn_uri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void sendFile() {

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_STREAM, filePath);

                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No file Selected. Please choose a file.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_image.setImageBitmap(bitmap);
                iv_image.setVisibility(View.VISIBLE);
                btn_remove.setVisibility(View.VISIBLE);
                iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePopUpDialog(bitmap);
                    }
                });

                tv_txtMsg.setText("Click on the Image to enlarge");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ImagePopUpDialog(Bitmap bitmap) {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_image_view);
            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
            dialog.setCancelable(false);

            ImageView img_alert_dialog_close = (ImageView) dialog.findViewById(R.id.img_alert_dialog_close);
            ImageView iv_photo = (ImageView) dialog.findViewById(R.id.iv_photo);
            iv_photo.setImageBitmap(bitmap);
            img_alert_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        } catch (Exception e) {
        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


}













