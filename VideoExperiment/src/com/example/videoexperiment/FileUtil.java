package com.example.videoexperiment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	
	public static Uri getOutputVideoFileUri(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = getMediaStorageDir();
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
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        
        Log.d("FileUtil", "writing to " + mediaFile);

        return Uri.fromFile(mediaFile);
    }
	
	public static String[] getOutputVideoFiles() {
		
		ArrayList<String> results = new ArrayList<String>();
		
		for(File file : getMediaStorageDir().listFiles()) {
			String name = file.getName();
			if (name.startsWith("VID_") && name.endsWith(".mp4")) {
				results.add(file.toString());
			}
		}
		
		return results.toArray(new String[0]);
	}
	
	private static File getMediaStorageDir(){
		return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Captures");
	}
    
    public static void AppendFiles(String outputFilename, String[] inputFiles) {
    	OutputStream output = null;
    	InputStream input = null;
    	
    	try {
	    	try {
	    		output = new BufferedOutputStream(new FileOutputStream(outputFilename));
	    		
	    		for(int inputIndex = 0; inputIndex < inputFiles.length; inputIndex++) {
	    			
	    			input = new BufferedInputStream(new FileInputStream(inputFiles[inputIndex]));
	    			byte[] buffer = new byte[1024 *200];
	    			int len;
	    			while((len = input.read(buffer)) > 0) {
	    				output.write(buffer, 0, len);
	    			}
	    			
	    			input.close();
	    			input = null;
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
    
    public static void DeleteDirectory(File file) {
    	
    	if (!file.exists())
    		return;
    	
    	if (file.isDirectory()) {

        	String[] children = file.list();
        	
        	for(int i = 0; i < children.length; i++) {
        		DeleteDirectory(new File(file, children[i]));
        	}
        }
    
    	if (!file.delete()) {
    		throw new RuntimeException("Unable to delete " + file.toString());
    	}
    }
}
