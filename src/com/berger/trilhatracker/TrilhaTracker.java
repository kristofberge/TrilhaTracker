package com.berger.trilhatracker;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class TrilhaTracker {
	
	int linesWritten = 3;
	
	private final String NAME_HEAD = "name>>>";
	private final String NAME_TAIL = "<<<name";
	private final String TIME_HEAD = "time>>>";
	private final String TIME_TAIL = "<<<time";
	private final String LOCATIONS_HEAD = "loc>>>";
	private final String LOCATIONS_TAIL = "<<<loc";
	private final String NEW_LINE = "\n";
	
	Home homescreen;
	TrilhaLocationListener locationListener;
	Timer timer;
	LocationManager lm;
	BufferedLocationWriter bufferedWriter;

	public TrilhaTracker(Home homescreen) {
		this.homescreen = homescreen;
	}

	public void init() {
		lm = (LocationManager) homescreen
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new TrilhaLocationListener(this,
				(LocationManager) homescreen
						.getSystemService(Context.LOCATION_SERVICE));
		timer = new Timer();
	}

	public void setupNewTrilha(String name) {
		Date startTime = new Date();
		SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMddkkmmss");
		SimpleDateFormat fileContentFormat = new SimpleDateFormat("ddMMyyyy kk:mm");
		String timeFormattedForFilename = fileNameFormat.format(startTime);
		String timeFormattedForFileContent = fileContentFormat.format(startTime);
		String nameFormattedForFilename = name.replaceAll("\\W", "");
		String nameFormattedForFileContent = name.replaceAll("[\\<>\n\r/]", "");
		
		bufferedWriter = Factory.setupNewBufferedLocationWriter(
				Factory.setupNewFileHandler(nameFormattedForFilename + "_"
						+ timeFormattedForFilename));
		bufferedWriter.write(NAME_HEAD + nameFormattedForFileContent + NAME_TAIL + NEW_LINE);
		bufferedWriter.write(TIME_HEAD + timeFormattedForFileContent + TIME_TAIL + NEW_LINE);
		bufferedWriter.write(LOCATIONS_HEAD + NEW_LINE);
	}

	public Home getHomescreen() {
		return homescreen;
	}

	public LocationManager getLocationManager() {
		return lm;
	}

	public void setLocationManager(LocationManager lm) {
		this.lm = lm;
	}
	
	public void updateLocation(Location loc) {
		if (loc != null) {
			bufferedWriter.write(loc.getLatitude() + ";" + loc.getLongitude() + ";" + loc.getAltitude() + NEW_LINE);
			linesWritten++;
			homescreen.updateStatus(loc.getLatitude() + ";" + loc.getLongitude());
		} else {
			homescreen.updateStatus("Location not set");
		}
	}
	
	public void stopTracking(){
		timer.cancel();
		updateLocation(locationListener.getCurrentLocation());
		bufferedWriter.write(LOCATIONS_TAIL);
		linesWritten++;
		homescreen.updateStatus("Lines written: " + linesWritten);
		bufferedWriter.close();
	}
	


	public void startTracking() {
		timer.scheduleAtFixedRate(locationListener, 0, 4000);
	}

}
