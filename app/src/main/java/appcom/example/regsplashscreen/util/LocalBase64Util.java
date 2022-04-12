package appcom.example.regsplashscreen.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class LocalBase64Util {

    // CONVERT BITMAP IMAGE TO A BASE64 ENCODED STRING
    public static String encodeImageToBase64String(Bitmap image) {
        Bitmap bitmapImage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageByteArray = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        Log.d("BASE64 ENCODED STRING", imageEncoded);
        return imageEncoded;
    }

    // CONVERT BASE64 ENCODED STRING TO A  BITMAP IMAGE
    public static Bitmap decodeBase64StringToImage(String encodedImageString) {
        if (null == encodedImageString || encodedImageString.isEmpty()) {
            return null;
        }
        byte[] decodedString = Base64.decode(encodedImageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
