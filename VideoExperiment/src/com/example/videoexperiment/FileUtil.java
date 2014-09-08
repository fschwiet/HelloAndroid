package com.example.videoexperiment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	
	public static Uri getOutputVideoFileUri(Context context) {
		return Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_VIDEO, context));
	}
	
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type, Context context){
          return Uri.fromFile(getOutputMediaFile(type, context));
    }
    
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Captures");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                throw new RuntimeException("failed to create directory " + mediaStorageDir.toString());
            }
            MediaScannerConnection.scanFile(context, new String[] {Uri.fromFile(mediaStorageDir).toString()}, null, null);
        }
               
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        
        Log.d("FileUtil", "writing to " + mediaFile);

        return mediaFile;
    }
    
    public static void AppendFiles(String outputFilename, String[] inputFiles) {
    	OutputStream output = null;
    	
    	try {
	    	try {
	    		output = new BufferedOutputStream(new FileOutputStream(outputFilename));
	    		
	    		for(int inputIndex = 0; inputIndex < inputFiles.length; inputIndex++) {
	    			
	    		}
	    	}
	    	finally {
	    		if (output != null) {
	    		    output.close();
	    		}
	    	}
    	}
    	catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }
}
