package com.example.Stampelklockan.model;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import android.content.Context;

public class WorkTimeJsonSerializer {
	private Context mContext;
	private String mFilename;
	private String mFilenameCurrent;
	
	
	public WorkTimeJsonSerializer(Context mContext, String mFilename){
		this.mContext = mContext;
		this.mFilename = mFilename;
		mFilenameCurrent = "currentWork";
	}
	
	public ArrayList<WorkTime> loadWorkTimes() throws IOException, JSONException{
		ArrayList<WorkTime> workTimes = new ArrayList<WorkTime>();
		BufferedReader reader = null; 
		
		try{
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null; 
			while((line = reader.readLine()) != null)
				jsonString.append(line);

			//Parse the JSON using JSONTokener
			JSONArray array =  (JSONArray)new JSONTokener(jsonString.toString()).nextValue();
			
			for(int i = 0; i<array.length(); i++){
				workTimes.add(new WorkTime(array.getJSONObject(i)));
			}
			
		} catch(FileNotFoundException e){
			
		} finally{
			if(reader!=null){
				reader.close();
			}
		}
		return workTimes; 
	}
	
	
	public void saveWorkTimes(ArrayList<WorkTime> workTimes) throws JSONException, IOException{
		JSONArray array = new JSONArray();
		for(WorkTime workTime : workTimes)
			array.put(workTime.toJSON());
		
		Writer writer = null; 
		try{
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if(writer != null)
				writer.close();
		}
		
	}
	
	public void saveActiveTime(WorkTime workTime) throws JSONException, IOException{
		JSONArray array = new JSONArray();
		Object jsonWorkTime = workTime.toJSON();
		array.put(jsonWorkTime);
		
		Writer writer = null; 
		try{
			OutputStream out = mContext.openFileOutput(mFilenameCurrent, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if(writer != null)
				writer.close();
		}
	}
	
	public WorkTime loadCurrentWorkTime() throws IOException, JSONException{
		
		WorkTime curr = null;
		BufferedReader reader = null; 
		
		try{
			InputStream in = mContext.openFileInput(mFilenameCurrent);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null; 
			while((line = reader.readLine()) != null)
				jsonString.append(line);

			//Parse the JSON using JSONTokener
			JSONArray array =  (JSONArray)new JSONTokener(jsonString.toString()).nextValue();
			
			curr = new WorkTime(array.getJSONObject(0));
			mContext.deleteFile(mFilenameCurrent);
			
			
		} catch(FileNotFoundException e){
			
		} finally{
			if(reader!=null){
				reader.close();
			}
		}
		return curr; 
	}
	
}