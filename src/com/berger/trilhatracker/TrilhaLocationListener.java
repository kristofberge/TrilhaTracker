package com.berger.trilhatracker;

import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class TrilhaLocationListener extends TimerTask {
	private LocationListener locListener;
	private TrilhaTracker tracker;
	private LocationManager lm;

	private String provider;
	private String providers[];
	private Criteria criteria;
	private int numberOfTimesRan = 0;

	public TrilhaLocationListener(TrilhaTracker tracker, LocationManager lm) {
		this.tracker = tracker;
		this.lm = lm;

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = lm.getBestProvider(criteria, true);

		Log.i("berger", "provider: " + provider);

		locListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

				TrilhaLocationListener.this.tracker
						.getHomescreen()
						.showTestMsg("Status changed",
								"Provider: " + provider + "\nStatus: " + status);

			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.i("berger", "Provider enabled: " + provider);
				TrilhaLocationListener.this.updateProvider();
			}

			@Override
			public void onProviderDisabled(String provider) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TrilhaLocationListener.this.tracker.getHomescreen());
				builder.setTitle("GPS is disabled");
				builder.setCancelable(false);
				builder.setPositiveButton("Enable GPS", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent enableGps = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						TrilhaLocationListener.this.tracker.getHomescreen()
								.startActivity(enableGps);
					}
				});

				builder.setNegativeButton("Leave", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

				AlertDialog enableGpsDialog = builder.create();
				enableGpsDialog.show();
			}

			@Override
			public void onLocationChanged(Location location) {

			}
		};

		if (provider == null) {
			locListener.onProviderDisabled(provider);
		}

		lm.requestLocationUpdates(provider, 2000, 0, locListener);
		lm.getLastKnownLocation(provider);

	}

	private void updateProvider() {
		provider = lm.getBestProvider(criteria, true);
		Log.i("berger", "Provider updated: " + provider);
	}

	public Location getCurrentLocation() {
		Location loc = lm.getLastKnownLocation(provider);
		if (loc == null && !provider.equals(LocationManager.GPS_PROVIDER)) {
			loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return loc;
	}

	@Override
	public void run() {
		numberOfTimesRan++;
		if (numberOfTimesRan >= 3) {
			updateProvider();
			numberOfTimesRan=0;
		}
		this.tracker.updateLocation(getCurrentLocation());

	}
}
