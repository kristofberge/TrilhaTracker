package com.berger.trilhatracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class TrilhaFileWriterHelper extends Activity {

	public enum STATE {
		READWRITE, READONLY, NOREADWRITE
	}

	boolean canRead, canWrite;
	String state;
	String name;
	File file, path;
	FileOutputStream fos;
	OutputStreamWriter output;

	public TrilhaFileWriterHelper(File path, String name) {
		this.name = name;
		this.path = path;
		this.file = new File(path, name);
	}

	public boolean setupReadyToWrite() {
		Log.i("berger", "path: " + path.toString());
		STATE s = checkState();
		Log.i("berger", "state=" + s);
		if (s.equals(STATE.READWRITE)) {
			
			path.mkdirs();
			

			try {
				fos = new FileOutputStream(file);
				output = new OutputStreamWriter(fos);
				Log.i("berger", "FileWrite setup OK");
			} catch (FileNotFoundException e) {
				
				Log.i("berger","File not found");
			} catch (IOException e) {
				Log.i("berger","IOException" + e.getMessage());
			}
			return true;

		}
		return false;
	}

	public boolean writeToFile(String toWrite) {
		try {
			output.write(toWrite);
			output.flush();
			return true;
		} catch (IOException e) {
			Log.i("berger", "error when writing:  " + e.getLocalizedMessage());
			return false;
		}
	}

	public boolean close() {
		
		try {
			
			fos.close();
			output.close();
			Log.i("berger", "output closed");
//			MediaScannerConnection.scanFile(this, 
//					new String[]{file.toString()}, 
//					null, 
//					new MediaScannerConnection.OnScanCompletedListener() {
//						public void onScanCompleted(String path, Uri uri) {
//							Log.i("berger", "scan complete");
//						}
//					});
		} catch (Exception e) {
			Log.i("berger", "Msg: " + e.getMessage());
			return false;
		}
		
		return true;
	}

	private STATE checkState() {
		state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return STATE.READWRITE;
		} else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return STATE.READONLY;
		}
		return STATE.NOREADWRITE;
	}
}
