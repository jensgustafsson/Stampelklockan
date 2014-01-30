package com.example.Stampelklockan.model;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;


public class WorkTimeLab {
	private static final String TAG = "WorkTimeLab";
	private static final String FILENAME = "workTimes.json";
	private static WorkTimeLab workTimeLab;
	private ArrayList<WorkTime> mWorkTimes;
	private Context mAppContext;
	private WorkTimeJsonSerializer mSerializer;
	
	
	private WorkTimeLab(Context appContext){

		mAppContext = appContext;
		mSerializer = new WorkTimeJsonSerializer(mAppContext, FILENAME);
		
		try{
			mWorkTimes = mSerializer.loadWorkTimes();
		} catch(Exception e){
			mWorkTimes = new ArrayList<WorkTime>();
			Log.e(TAG, "Error loading workTimes:", e);
		}
	}
	
	public static WorkTimeLab getInstance(Context appContext){
		if(workTimeLab == null){
			workTimeLab = new WorkTimeLab(appContext);
		}
		return workTimeLab; 
	}
	
	public ArrayList<WorkTime> getWorkTimes(){//REMOVE LATER
		return mWorkTimes;
	}
	
	public void addWorkTime(WorkTime workTime){
		mWorkTimes.add(0, workTime);
	}
	
	public boolean saveWorkTimes(){
		try{
			mSerializer.saveWorkTimes(mWorkTimes);
			Log.d(TAG,"workTimes saved successfully");
			return true;
		} catch(Exception e){
			Log.e(TAG,"Error saving workTimes", e);
			return false;
		}
	}

	public int getWorkTimeId(WorkTime currentWorkTime) {
		for(int i=0; i<mWorkTimes.size(); i++){
			if(currentWorkTime.equals(mWorkTimes.get(i))){
				return i;
			}
		}
		return -1;
	}

	public void deleteWorkTime(WorkTime item) {
		for(int i=0; i< mWorkTimes.size(); i++){
			if(mWorkTimes.get(i).equals(item)){
				mWorkTimes.remove(i);
			}
		}
		
	}

	public void saveCurrentWorkTimeToSandbox(WorkTime workTime) {
		try {
			mSerializer.saveActiveTime(workTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public WorkTime initCurrentWorkTime() {
		try {
			return mSerializer.loadCurrentWorkTime();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}
