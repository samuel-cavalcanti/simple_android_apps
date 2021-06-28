package edu.harvard.cs50.fiftygram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileDescriptor;
import java.io.IOException;

import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;

public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {
    private ImageView imageView;
    private Bitmap original;
    static int INPUT_IMAGE_REQUEST = 1;
    static int WRITE_EXTERNAL_STORAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);

    }

    public void apply(Transformation<Bitmap> filter) {
        if (original != null) {
            Glide
                    .with(this)
                    .load(original)
                    .apply(RequestOptions.bitmapTransform(filter))
                    .into(imageView);
        }
    }

    public void applySepia(View view) {
        apply(new SepiaFilterTransformation());
    }

    public void applyToon(View view) {
        apply(new ToonFilterTransformation());
    }

    public void applySketch(View view) {
        apply(new SketchFilterTransformation());
    }

    public void choosePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, INPUT_IMAGE_REQUEST);
    }

    public void savePhoto(View view) {
        Bitmap transformedImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String name = "newPhoto.jpg";
        String description = "Image Transformed by fyfygram";
        MediaStore.Images.Media.insertImage(getContentResolver(), transformedImage, name, description);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == INPUT_IMAGE_REQUEST) {
            inputImageRequestResult(data);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void inputImageRequestResult(Intent data) {
        try {
            Uri uri = data.getData();
            assert uri != null;
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            assert parcelFileDescriptor != null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            original = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            imageView.setImageBitmap(original);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
