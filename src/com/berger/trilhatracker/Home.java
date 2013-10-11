package com.berger.trilhatracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.berger.trilhatracker.R;

public class Home extends Activity{
	Button bToggleTracking, bPause;
	TextView tvStatus;
	Chronometer cmChrono;

	TrilhaTracker tracker;
	AlertDialog.Builder builder;
	AlertDialog newTrilhaDialog, areYouSureDialog;
	Thread trackerThread;

	boolean isTracking, isPaused;
	String trilhaName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);

		initUI();
		initListeners();
		initVariables();
		initDialogs();

	}

	private void initUI() {
		bToggleTracking = (Button) findViewById(R.id.bToggleTracking);
		bPause = (Button) findViewById(R.id.bPause);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		cmChrono = (Chronometer) findViewById(R.id.cmChrono);
	}

	private void initVariables() {
		tracker = new TrilhaTracker(this);
		tracker.setLocationManager((LocationManager) getSystemService(LOCATION_SERVICE));
		builder = new AlertDialog.Builder(this);
		tracker.init();
	}

	private void initDialogs() {
		final EditText etNameInput = new EditText(this);
		etNameInput.setHint("Trilha name");
		
		newTrilhaDialog = new AlertDialog.Builder(this).setTitle("New trilha")
				.setMessage("Choose a name for your trilha.")
				.setView(etNameInput)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								trilhaName = etNameInput.getText().toString();
								areYouSureDialog.setMessage("Are you sure you want to start a trilha with name: " + trilhaName + "?");
								areYouSureDialog.show();
								dialog.dismiss();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
							}
						})
				.create();
		
		areYouSureDialog = new AlertDialog.Builder(this).setTitle("Are you sure?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								bToggleTracking.setText(R.string.StopTracking);
								tvStatus.setText(R.string.StatusTracking);
								cmChrono.setBase(SystemClock.elapsedRealtime());
								cmChrono.start();
								tracker.init();
								tracker.setupNewTrilha(trilhaName);
								tracker.startTracking();
								isTracking = true;
								dialog.dismiss();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								trilhaName = null;
							}
						})
				.create();
		
	}

	private void initListeners() {
		bToggleTracking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if (!isTracking) {
					
					newTrilhaDialog.show();
					
				} else {
					bToggleTracking.setText(R.string.StartTracking);
					tvStatus.setText(R.string.StatusReady);
					cmChrono.stop();
					isTracking = false;
					tracker.stopTracking();
				}
			}
		});
	
		bPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPaused = !isPaused;
				if (isPaused) {
					bPause.setText(R.string.ResumeButton);
					tvStatus.setText(R.string.StatusPaused);
					cmChrono.stop();
				} else {
					bPause.setText(R.string.PauseButton);
					tvStatus.setText(R.string.StatusTracking);
					cmChrono.start();
				}
			}
		});
	}

	public void stopStartChrono(View v) {

	}

	public void showTestMsg(final String title, final String text) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				builder.setTitle(title).setMessage(text).show();
			}
		});
	}

	public void updateStatus(final String text) {
		tvStatus.post(new Runnable() {
			@Override
			public void run() {
				tvStatus.setText(text);
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
}
