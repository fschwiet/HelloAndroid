package com.example.videoexperiment.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.example.videoexperiment.FileUtil;

public class FileUtilTest extends AndroidTestCase {
	
	File workingPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TempForTesting");
			
	public FileUtilTest() {
	}
	
	public void setUp() throws IOException, InterruptedException {
		if (workingPath.exists()) {
			FileUtil.DeleteDirectory(workingPath);
		}
		
		Assert.assertTrue(workingPath.mkdirs() || workingPath.isDirectory());
	}
	
	public void testAppendFiles() throws IOException {
		
		String[] files = new String[]{
			workingPath.toString() + "/file1",	
			workingPath.toString() + "/file2",	
			workingPath.toString() + "/file3"	
		};
		
		String outputFile = workingPath.toString() + "/appended";
		
		for(int i = 0; i < files.length; i++) {
			
			File file = new File(files[i]);
			Assert.assertTrue(file.createNewFile());
			OutputStream output = new FileOutputStream(file);
			
			try{
				output.write(i);
			}
			finally {
				output.close();
			}
		}
		
		FileUtil.AppendFiles(outputFile, files);
		
		InputStream resultStream = new FileInputStream(new File(outputFile));
		
		try {
			byte[] buffer = new byte[4];
			int bytesRead = resultStream.read(buffer);
			
			Assert.assertEquals(3, bytesRead);
			Assert.assertEquals(0, buffer[0]);
			Assert.assertEquals(1, buffer[1]);
			Assert.assertEquals(2, buffer[2]);
			
			Assert.assertEquals(-1, resultStream.read(buffer));
			
		} finally {
			resultStream.close();
		}
	}
}
