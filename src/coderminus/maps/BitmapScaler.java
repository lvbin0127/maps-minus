package coderminus.maps;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapScaler {
	private static Matrix matrix = new Matrix();
	
	public static Bitmap scaleTo(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
       
        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
       
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        //matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = 
        	Bitmap.createBitmap(bitmap, 0, 0,
                          width, height, matrix, true); 	
        	
		return resizedBitmap;
	}
}
