/*	ATT G…RA	*/
//
//	SQLITE! spara i databas
//	spara aktiv tid pŒ egen plats. LŠs in aktiv tid
//

package com.example.Stampelklockan.controller;


import com.example.Stampelklockan.R;
import com.example.Stampelklockan.model.WorkTime;
import com.example.Stampelklockan.model.WorkTimeLab;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ClockInFragment extends Fragment {

	private static ClockInFragment clockInFragment;
	private Thread clockThread;
	private Thread breakThread;
	private View mContentView;

	private Button mClockInButton, mClockOutButton;
	private TextView clockDisplay, breakDisplay;
	
	private TimeListFragment timeListFragment;
	private WorkTime currentWorkTime;

	private long mShortAnimationDuration;
	private boolean onDestroyInterruption;
	private boolean oldWorkIsLoaded; 
	private boolean activeWork;


	public static ClockInFragment getInstance() {
		if (clockInFragment == null) {
			clockInFragment = new ClockInFragment();
		}
		return clockInFragment;
	}
	
	public boolean existActiveWork(){
		return activeWork;
	}
	
	public ClockInFragment() {
		timeListFragment = TimeListFragment.getInstance();
		onDestroyInterruption = false;
		
	}

	@Override
	public void onDestroy(){

		super.onDestroy();
	}
	
	public void onPause(){
		
		/*	Destroy the threads for the display, nothing else shall happen	*/
		onDestroyInterruption = true;

		if(clockThread != null){
			clockThread.interrupt();
			try {
				clockThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(breakThread != null){
			breakThread.interrupt();
			try {
				breakThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		onDestroyInterruption = false;
		if(currentWorkTime!=null){
			WorkTimeLab.getInstance(getActivity()).saveCurrentWorkTimeToSandbox(currentWorkTime);
			currentWorkTime.setStillActive(true);
			//FIX LATER
		}
		/*END OF DESTROY*/
		
		/*	Save workTimes to memory, nothing else shall happen		*/
		WorkTimeLab.getInstance(getActivity()).saveWorkTimes();
		super.onPause();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		mShortAnimationDuration = getResources().getInteger(
	                android.R.integer.config_shortAnimTime);
		
		/*	If active workTime exist when app starts/restarts from destroy -->
		 * 	--> workTime must be reloaded to the clockInFragment display
		 */
		currentWorkTime = WorkTimeLab.getInstance(getActivity()).initCurrentWorkTime();

		
		if (currentWorkTime != null) {
			oldWorkIsLoaded = true;
		} else{
			oldWorkIsLoaded = false;
		}
	}

	@Override
	public void onStart(){
		
		/*	If old workTime is loaded --> 
		 * 	-->	set correct clockTime and breakTime
		 *  -->	start the clockThread or the breakThread
		 */
		if(oldWorkIsLoaded){
			activeWork = true;
			currentWorkTime.restartActiveBreak();
			clockDisplay.setText(currentWorkTime.getWorkTimeToDisplay());
			breakDisplay.setText(currentWorkTime.getBreakTimeToDisplayOnStart());
			if (currentWorkTime.havingBreak()) {
				startBreakThread();
			} else {
				startClockThread();
			}
			
		}
		
		super.onStart();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.fragment_clock_in, parent, false);

		/*	Display	*/
		clockDisplay = (TextView) mContentView.findViewById(R.id.clock_display);
		breakDisplay = (TextView) mContentView.findViewById(R.id.break_display);

		/*	ClockInButton	*/
		mClockInButton = (Button) mContentView.findViewById(R.id.button_clock_in);
		mClockInButton.setBackgroundResource(R.drawable.green_button_property);
		mClockInButton.setOnClickListener(startClockInListener());

		/*	ClockOutButton	*/
		mClockOutButton = (Button) mContentView.findViewById(R.id.button_clock_out);
		mClockOutButton.setBackgroundResource(R.drawable.orange_button_property);
		mClockOutButton.setOnClickListener(startClockOutListener()); 
		
		return mContentView;
	}

	private OnClickListener startClockOutListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/*	Worker is working (wants to clock out)	*/
				if (currentWorkTime != null && !currentWorkTime.havingBreak()) {
					/*	Interrupt clockThread, after that: start breakThread*/
					if (clockThread != null) {
						clockThread.interrupt();
						try {
							clockThread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					startBreakThread();
				}
			}
		};
	}

	private OnClickListener startClockInListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				/*	Worker clocks in on a new work	
				 * 	--> create new workTime 
				 * 	-->show in active view(active work =true)
				 * 	--> save a boolean telling active work exists	*/
				if (currentWorkTime == null) {
					activeWork = true;
					currentWorkTime = new WorkTime();
					
				} 
				/*	Worker is already clocked in --> nothing shall happen	*/
				else if (!currentWorkTime.havingBreak()) {
					return;

				}
				/*	Worker clocks in again after break	*/
				else if (currentWorkTime.havingBreak()) {
					if(breakThread != null){
						breakThread.interrupt();	
						try {
							breakThread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				startClockThread();
			}
		};
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main , menu);
		menu.findItem(R.id.menu_item_new_workTime);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		
		/*	Create new workTime
		 * 	--> We must destroy the old threads
		 * 	--> Clear the display	
		 * */
		case R.id.menu_item_new_workTime:
			oldWorkIsLoaded = false;
			activeWork = false;
			
			
			if(clockThread != null){
					clockThread.interrupt();
					try {
						clockThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			if(breakThread != null){
				breakThread.interrupt();	
				try {
					breakThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/*	If workTime not is created --> Nothing has to be reset	*/
			if(currentWorkTime == null){
				crossfade();
				return true;
			}
			
			/*	Add workTime to list	*/
			WorkTimeLab.getInstance(getActivity()).addWorkTime(currentWorkTime);
			timeListFragment.notifyChanges();
			
			/*	Reset the display and clockOut the worker
			 * 	Notify the TimeList and ... 	//fix later(if neccessarry) 	
			 */	

			clockDisplay.setText("00:00:00");
			breakDisplay.setText("00:00:00");
			currentWorkTime.clockOut();
			currentWorkTime.calculateBreakTime();
			currentWorkTime = null;
			
			crossfade();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void crossfade() {

	    // Set the content view to 0% opacity but visible, so that it is visible
	    // (but fully transparent) during the animation.
		mContentView.setAlpha(0f);
	    mContentView.setVisibility(View.VISIBLE);

	    // Animate the content view to 100% opacity, and clear any animation
	    // listener set on the view.
	    mContentView.animate()
	            .alpha(1f)
	            .setDuration(mShortAnimationDuration);
	            
	}
	private void startClockThread() {
		clockThread = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								clockDisplay.setText(currentWorkTime
										.getWorkTimeToDisplay());
							}
						});
					}
				} catch (InterruptedException e) {
					if(!onDestroyInterruption){
						currentWorkTime.startBreak();						
					}
				}
			}
		};

		clockThread.start();
	}
	
	private void startBreakThread() {
		breakThread = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								breakDisplay.setText(currentWorkTime
										.getBreakTimeToDisplay());
							}
						});
					}
				} catch (InterruptedException e) {
					if(!onDestroyInterruption){
						currentWorkTime.endBreak();						
					}
				}
			}
		};

		breakThread.start();
	}

	public WorkTime getCurrentWorkTime() {
		// TODO Auto-generated method stub
		return currentWorkTime;
	}
}
