package com.example.Stampelklockan.controller;

import java.util.ArrayList;

import com.example.Stampelklockan.R;
import com.example.Stampelklockan.model.WorkTime;
import com.example.Stampelklockan.model.WorkTimeLab;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TimeListFragment extends ListFragment {
	private ArrayList<WorkTime> mWorkTimes;
	private static TimeListFragment timeListFragment;
	private Adapter adapter;
	private WorkTime curr; 
	private View mContentView;
	private TextView textViewRight;
	private TextView textViewRightRight;
	private long mShortAnimationDuration;
	private View activeTimeView;
	
	//Active work item
	private TextView textViewRightActive;
	private TextView textViewLeftUpActive;
	private TextView textViewLeftDownActive;


	public static TimeListFragment getInstance() {
		if (timeListFragment == null) {
			timeListFragment = new TimeListFragment();
		}
		return timeListFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWorkTimes = WorkTimeLab.getInstance(getActivity()).getWorkTimes();

		adapter = new TimeAdapter(mWorkTimes);
		setListAdapter((ListAdapter) adapter);
		mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		
		TimeAdapter adapter = (TimeAdapter)getListAdapter();
		WorkTime workTime = adapter.getItem(position);
		
		switch(item.getItemId()){
			case R.id.menu_item_delete_workTime:
				
				WorkTimeLab.getInstance(getActivity()).deleteWorkTime(workTime);
				
				adapter.notifyDataSetChanged();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.contextual_menu, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_time_list, parent, false);
		mContentView = v; 
		ListView listView = (ListView) v.findViewById(android.R.id.list);
		activeTimeView = v.findViewById(R.id.relative_parent);

		textViewLeftUpActive = (TextView) activeTimeView
				.findViewById(R.id.upperPartOfSquareActive);
		textViewLeftDownActive = (TextView) activeTimeView
				.findViewById(R.id.lowerPartOfSquareActive);
		textViewRightActive = (TextView) activeTimeView
				.findViewById(R.id.clock_in_textActive);



		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	
				switch (item.getItemId()) {
				case R.id.menu_item_delete_workTime:
					TimeAdapter adapter = (TimeAdapter) getListAdapter();
					WorkTimeLab workTimeLab = WorkTimeLab
							.getInstance(getActivity());

					for (int i = adapter.getCount() - 1; i >= 0; i--) {
						if (getListView().isItemChecked(i)) {
							workTimeLab.deleteWorkTime(adapter.getItem(i));
						}
					}
					mode.finish();
					adapter.notifyDataSetChanged();
					return true;
				default:
					return false;
				}

				
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				
				// VID LÅGTRYCK SÔøΩ KALLAS DENNA METOD
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.contextual_menu, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				crossfade();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {				
			}
			
		});
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	public WorkTime addNewWorkTime() {
		WorkTime tmp = new WorkTime();
		mWorkTimes.add(tmp);
		notifyChanges();
		return tmp;
	}

	public void notifyChanges() {
		((TimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	private class TimeAdapter extends ArrayAdapter<WorkTime> {

		public TimeAdapter(ArrayList<WorkTime> timeList) {
			super(getActivity(), 0, timeList);// pass a zero since predefined
												// layout is not in use
		}

		@Override
		public long getItemId(int position) {
			return mWorkTimes.get(position).getId();
		}
	
	
		@Override
		public boolean hasStableIds() {
			return true;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.time_item, null);
			}
			curr = getItem(position);
			
			TextView textViewLeftUp = (TextView) convertView
					.findViewById(R.id.upperPartOfSquare);
			TextView textViewLeftDown = (TextView) convertView
					.findViewById(R.id.lowerPartOfSquare);
			textViewLeftUp.setText(curr.getDayDateIn());
			textViewLeftDown.setText(curr.getMonthIn());

			textViewRight = (TextView) convertView
					.findViewById(R.id.clock_in_text);
			
			textViewRightRight = (TextView) convertView.
					findViewById(R.id.clock_detail_text);
			setClockOutTime(curr);
			return convertView;
		}
	}

	public void setClockOutTime(WorkTime currentWorkTime) {
		curr = currentWorkTime;
		String dateIn = curr.getTimeIn();
		String dateOut = curr.getTimeOut();
		String result = "In: " + dateIn + "\n" + "Ut: " + dateOut;
		textViewRight.setText(result);
		if(curr.workTimeIsActive()){
			textViewRightRight.setText("Totaltid: " + "\nRast:\t\t\t\t");
			
		}else{
			textViewRightRight.setText("Totaltid: " + curr.getTotalTime() + "\nRast:\t\t\t\t" + curr.getTotalBreakTime());
		}
		
		notifyChanges();
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
	void showActive(boolean show){
		if(show){
			activeTimeView.setVisibility(View.VISIBLE);
		}else{
			activeTimeView.setVisibility(View.GONE);
		}
	}
	
	public void writeActiveTime(){
		WorkTime curr = ClockInFragment.getInstance().getCurrentWorkTime();
		if(curr !=null){
			textViewLeftUpActive.setText(curr.getDayDateIn());
			textViewLeftDownActive.setText(curr.getMonthIn());

			String in = curr.getTimeIn();
			String out = curr.getTimeOut();
			if(out == null){
				out = "";
			}
			

			String totalTime = curr.getTotalTime();
			String totalBreak = curr.getTotalBreakTime();
			
			if(totalTime == null){
				totalTime = "";
			}
			
			if(totalBreak == null){
				totalBreak = "";
			}
			
			textViewRightActive.setText("In: "+in+ "\nUt: " + out);
			
		}
	}
}
