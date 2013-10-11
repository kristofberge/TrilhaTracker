package com.berger.trilhatracker;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.util.Log;

public class BufferedLocationWriter {
	LinkedBlockingQueue<String> buffer;
	TrilhaFileWriterHelper helper;
	int buffersize;

	public BufferedLocationWriter(int buffersize, TrilhaFileWriterHelper helper) {
		this.buffersize = buffersize;
		buffer = new LinkedBlockingQueue<String>(buffersize);
		this.helper = helper;
	}

	public void write(String s) {
		try {
			buffer.put(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (buffer.remainingCapacity() == 0) {
			writeData();
		}
	}

	private void writeData() {
		
			int arraySize = buffersize - buffer.remainingCapacity();
			final String[] toWrite = new String[arraySize];
			for (int i = 0; i < arraySize; i++) {
				try {
					toWrite[i] = String
							.copyValueOf(buffer.take().toCharArray());
				} catch (InterruptedException e) {
					Log.i("berger", " " + e.getLocalizedMessage());
				}
			}

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					for (String s : toWrite) {
						helper.writeToFile(s);
					}
				}
			});
			
			try {
				t.start();
				t.join();
			} catch (InterruptedException e) {
				Log.e("berger", e.getMessage());
			}

	}

	public void close() {
		if (!buffer.isEmpty()) {
			writeData();
		}
		helper.close();
	}

}
