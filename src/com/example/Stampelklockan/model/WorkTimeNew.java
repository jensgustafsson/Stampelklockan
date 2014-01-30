package com.example.Stampelklockan.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkTimeNew {

	// JSON
	private static final String JSON_ID = "id";
	private static final String JSON_CLOCK_IN = "clockInTime";
	private static final String JSON_CLOCK_OUT = "clockOutTime";
	private static final String JSON_BREAK_TIME = "breakTime";
	private static final String JSON_ACTIVE = "active";
	private static final String JSON_WORKING = "working";
	private static final String JSON_LAST_SAVED_BREAK_TIME = "lastSavedBreakTime";
	
	private long id;
	//if the workTime is the active time or not showing on the clockInFragment
	private boolean active;
	
	//if the worker is on break or if he is working 
	private boolean working; 

	// Time when worker clocks in and out(for break or for end of work day)
	private Date clockInTime;
	private Date clockOutTime;



	//Total breakTime
	private long breakTime;

	//start of breakTime
	private long breakTimeStart;
	
	// Used to format the date output correctly
	private SimpleDateFormat timeFormatHourMinuteSecond;
	private SimpleDateFormat dayFormat;
	private SimpleDateFormat monthFormat;

	/*
	 * Constructors
	 * *************************************************************
	 * ************************
	 */
	public WorkTimeNew() {
		initOtherVars();
		clockInTime = new Date();
		active = true;
		breakTime = 0;
		id = UUID.randomUUID().getLeastSignificantBits();
		working = true;
	}

	public WorkTimeNew(JSONObject json) throws JSONException {
		id = json.getLong(JSON_ID);
		clockInTime = new Date(json.getLong(JSON_CLOCK_IN));
		clockOutTime = new Date(json.getLong(JSON_CLOCK_OUT));
		active = json.getBoolean(JSON_ACTIVE);
		working = json.getBoolean(JSON_WORKING);
		breakTime = json.getLong(JSON_BREAK_TIME);
		breakTimeStart = json.getLong(JSON_LAST_SAVED_BREAK_TIME);
		initOtherVars();
	}

	private void initOtherVars() {
		timeFormatHourMinuteSecond = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		dayFormat = new SimpleDateFormat("d", Locale.getDefault());
		monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
		
	}
	
	/*
	 * *************************************************************************************
	 */

	/*
	 * Get the total workTime to show in the listView for this workTime
	 */
	public String getTotalTime() {
		if(clockOutTime != null && clockInTime != null){
			return getDisplayString(clockOutTime.getTime()-clockInTime.getTime()-breakTime);
		}
		return "00:00:00";
	}

	/*
	 * Get the total breakTime to show in the listView for this workTime
	 */
	public String getTotalBreakTime() {
		if(breakTime == 0){
			return "NOLL";
		}
		return getDisplayString(breakTime);
	}


	/*
	 * For the clock on the clockInScreen
	 * */
	public String getBreakTimeToDisplay() {
		long currTime = Calendar.getInstance().getTimeInMillis();
		
		long currRes;
		if(breakTimeStart != 0){
			currRes = breakTime + currTime - breakTimeStart;
		}else{
			currRes = breakTime; 
		}
		return getDisplayString(currRes);
	}
	
	public String getBreakTimeToDisplayOnStart() {
		return getDisplayString(breakTime);
	}

	/*
	 * Call this method when the break starts. 
	 */
	public void startBreak() {
		working = false;
		clockOutTime = new Date();
		breakTimeStart = Calendar.getInstance().getTimeInMillis();
	}
	
	/*
	 * Call this method when the break ends. 
	 */
	public void endBreak() {
		working = true;
		breakTime += Calendar.getInstance().getTimeInMillis() - breakTimeStart;
		breakTimeStart = 0;
	}
	
	/*
	 * Calculate the total breakTime for this workTime
	 */
	public void calculateBreakTime() {
		if (breakTimeStart == 0) {
			return;
		}
		if (breakTime == 0) {
			breakTime = Calendar.getInstance().getTimeInMillis()
					- breakTimeStart;
		}
	}

	/*
	 * For the clock on the clockInScreen
	 * */
	public String getWorkTimeToDisplay() {

		long currTime = Calendar.getInstance().getTimeInMillis();
		long currRes = currTime - clockInTime.getTime()  - breakTime;
		return getDisplayString(currRes);
	}

	/*
	 * Get id of this WorkTimeObject
	 */
	public long getId() {
		return id;
	}

	/*
	 * Return clockInTime to the listView
	 */
	public String getTimeIn() {
		return timeFormatHourMinuteSecond.format(clockInTime);
	}

	/*
	 * Return clockOutTime to the listView
	 */
	public String getTimeOut() {
		if (clockOutTime != null) {
			return timeFormatHourMinuteSecond.format(clockOutTime);
		}
		return "";
	}

	/*
	 * Save the time when the worker clocks out for the day(when he/she creates a new workTime)
	 */
	public void clockOut() {
		active = false;
		clockOutTime = new Date();
	}
	
	/*
	 * Return the day clocked in to the listView
	 */
	public String getDayDateIn() {
		return dayFormat.format(clockInTime);
	}

	/*
	 * Return the month clocked in to the listView
	 */
	public String getMonthIn() {
		return monthFormat.format(clockInTime).toUpperCase();
	}

	
	public void restartActiveBreak(){
		if(working){
			return;
		}else{
			breakTime += Calendar.getInstance().getTimeInMillis() - breakTimeStart;
			breakTimeStart = Calendar.getInstance().getTimeInMillis(); 			
		}
	}


	public boolean workTimeIsActive(){
		return active;
	}

	/*
	 * Used to tell whether the worker is on break or not. 
	 */
	public boolean havingBreak() {
		return !working;
	}

	/*
	 * Save WorkTime as JSON-object
	 */
	public Object toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, id);
		json.put(JSON_CLOCK_IN, clockInTime.getTime());
		if(clockOutTime==null){
			clockOutTime = new Date();
		}
		json.put(JSON_CLOCK_OUT, clockOutTime.getTime());
		json.put(JSON_BREAK_TIME, breakTime);
		json.put(JSON_ACTIVE, active);
		json.put(JSON_WORKING, working);
		json.put(JSON_LAST_SAVED_BREAK_TIME, breakTimeStart);
		return json;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WorkTimeNew))
			return false;
		WorkTimeNew other = (WorkTimeNew) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
	
	
	
	/*
	 * Private methods
	 * *****************************************************************************
	 */
	private String getDisplayString(long currRes) {
		String stringHours;
		String stringMinutes;
		String stringSeconds;

		currRes /= 1000; // Convert to seconds
		long hours = getHours(currRes);
		long minutes = getMinutes((currRes - hours * 3600));
		long seconds = getSeconds(currRes, hours, minutes);

		if (hours > 9) {
			stringHours = "" + hours;
		} else {
			stringHours = "0" + hours;
		}

		if (minutes > 9) {
			stringMinutes = "" + minutes;
		} else {
			stringMinutes = "0" + minutes;
		}

		if (seconds > 9) {
			stringSeconds = "" + seconds;
		} else {
			stringSeconds = "0" + seconds;
		}
		return stringHours + ":" + stringMinutes + ":" + stringSeconds;
	}

	private long getMinutes(long seconds) {
		return seconds / 60;
	}

	private long getHours(long seconds) {
		return seconds / 3600;
	}

	private long getSeconds(long currRes, long hours, long minutes) {
		return currRes - hours * 3600 - minutes * 60;
	}

}
