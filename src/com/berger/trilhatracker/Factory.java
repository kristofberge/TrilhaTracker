package com.berger.trilhatracker;

import java.io.File;

import android.content.Context;
import android.location.LocationManager;
import android.os.Environment;

public class Factory {
	
	public static final String TRILHAS_DEFAULT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TrilhaTracker/";
	public static final String EXTENSION = ".txt";
	public static final int DEFAULT_BUFFER_SIZE = 10;
	
	
	

	public static TrilhaLocationListener newNewTrilhaLocationListener(TrilhaTracker tracker) {
		return new TrilhaLocationListener(tracker, (LocationManager) tracker
				.getHomescreen().getSystemService(Context.LOCATION_SERVICE));
	}
	
	 public static TrilhaTracker setupNewTrilhaTracker(Home home){
		 return new TrilhaTracker(home);
	 }
	 
	 public static BufferedLocationWriter setupNewBufferedLocationWriter(TrilhaFileWriterHelper handler){
		 return new BufferedLocationWriter(DEFAULT_BUFFER_SIZE, handler);
	 }
	 
	 public static BufferedLocationWriter setupNewBufferedLocationWriter(int bufferSize, TrilhaFileWriterHelper handler){
		 return new BufferedLocationWriter(bufferSize, handler);
	 }

	 public static TrilhaFileWriterHelper setupNewFileHandler(String path, String fileName){
		 TrilhaFileWriterHelper h = new TrilhaFileWriterHelper(new File(path), fileName+EXTENSION);
		 h.setupReadyToWrite();
		 return h;
	 }
	 
	 public static TrilhaFileWriterHelper setupNewFileHandler(String fileName){
		 TrilhaFileWriterHelper h = new TrilhaFileWriterHelper(new File (TRILHAS_DEFAULT_PATH), fileName+EXTENSION);
		 h.setupReadyToWrite();
		 return h;
	 }
	 
}
