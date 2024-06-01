package com.example.startopenapp.display_manager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {
    public static final int REQUEST_CODE_CAMERA = 123;
    public static final int REQUEST_CODE_FOLDER = 456;
    public static ImageView currentImageView;

    // Function to rotate the image based on the Exif orientation
    private static Bitmap rotateImageIfRequired(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(photoPath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            case ExifInterface.ORIENTATION_UNDEFINED:
                return rotateImage(bitmap, 0);
            case ExifInterface.ORIENTATION_NORMAL:
                return rotateImage(bitmap, 0);
            default:
                return rotateImage(bitmap, 90);
        }
    }

    // Function to rotate a bitmap
    private static Bitmap rotateImage(Bitmap bitmap, int degree) {
        if (degree<=0){
            return bitmap;
        }
        Matrix matrix = new Matrix();
        if (bitmap.getWidth()>bitmap.getHeight()){
            matrix.setRotate(degree);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
        }
        return bitmap;
    }

    public static void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, Activity activity) {
        if (resultCode == Activity.RESULT_OK && currentImageView != null) {
            if (requestCode == REQUEST_CODE_CAMERA && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                try {
                    // Save the image to a temporary file and get the path
                    File tempFile = saveBitmapToFile(bitmap, activity);
                    String photoPath = tempFile.getAbsolutePath();
                    // Rotate the image if required
                    bitmap = rotateImageIfRequired(photoPath, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentImageView.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_CODE_FOLDER && data != null) {
                Uri uri = data.getData();
                try {
                    InputStream inputStream = activity.getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    currentImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(activity, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private static File saveBitmapToFile(Bitmap bitmap, Activity activity) throws IOException {
        File tempFile = File.createTempFile("tempImage", ".jpg", activity.getExternalFilesDir(null));
        FileOutputStream fos = new FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        return tempFile;
    }

    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    public static void openGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_FOLDER);
    }

    public static void requestCameraPermission(Activity activity, ImageView imageView) {
        currentImageView = imageView;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        } else {
            openCamera(activity);
        }
    }

    public static void requestGalleryPermission(Activity activity, ImageView imageView) {
        currentImageView = imageView;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_FOLDER);
        } else {
            openGallery(activity);
        }
    }

    // Hàm để chuyển đổi Bitmap sang chuỗi Base64
    public static String convertImageViewToBase64(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Loại bỏ các ký tự xuống dòng không mong muốn
            String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
            base64String = base64String.replaceAll("\n", "");
            return base64String;
        } else {
            // Trả về null hoặc thông báo lỗi
            return null;
        }
    }


    // Hàm để chuyển đổi chuỗi Base64 sang Bitmap
    public static Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
