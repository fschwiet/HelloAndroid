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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	
	@SuppressLint("SimpleDateFormat") 
	public static Uri getOutputVideoFileUri(Context context) {

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
		
		Collections.sort(results, new Comparator<String>() {
			
			@Override
			public int compare(String arg0, String arg1) {
				Long firstDate = new File(arg0).lastModified();
				Long lastDate = new File(arg1).lastModified();
				
				if (firstDate < lastDate)
					return -1;
				else if (firstDate == lastDate)
					return 0;
				else
					return 1;
			}
		});
		
		return results.toArray(new String[0]);
	}
	
	private static File getMediaStorageDir(){
		
		String storageState = Environment.getExternalStorageState();
		
		if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
			throw new RuntimeException("Environment.getExternalStorageState() returned " + storageState + ", expected " + Environment.MEDIA_MOUNTED);
		}
		
		File externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		if (!externalStorage.exists()) {
			throw new RuntimeException("Could not find external storage at " + externalStorage.toString() + ", try taking a picture then rebooting your device.");
		}
		
		return new File(externalStorage, "Captures");
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
