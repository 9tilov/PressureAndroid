package com.mobsoftmaster.bloodpressurediary;

import java.util.LinkedList;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.view.KeyEvent;
import android.graphics.Color;
import android.util.Log;

public class Graph extends TrackedActivity {

	MyDB db;

	SharedPreference sharedPref;

	final String LOG_TAG = "myLogs";

	boolean rotation;
	Resources res;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		db = new MyDB(this);
		sharedPref = new SharedPreference(this);

		res = getResources();
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();

		LinearLayout layoutWeek = (LinearLayout) findViewById(R.id.graphWeek);
		LinearLayout layoutMonth = (LinearLayout) findViewById(R.id.graphMonth);
		LinearLayout layout3Month = (LinearLayout) findViewById(R.id.graph3Month);
		LinearLayout layoutAllPeriod = (LinearLayout) findViewById(R.id.graphAllPeriod);

		int stat_id = sharedPref.LoadID();
		rotation = sharedPref.LoadRotation();

		if (!rotation) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				&& (rotation)) {
			finish();
		}

		setTab("tag1", R.id.graphWeek, res.getString(R.string.graphWeek), tabs);
		setTab("tag2", R.id.graphMonth, res.getString(R.string.graphMonth),
				tabs);
		setTab("tag3", R.id.graph3Month, res.getString(R.string.graph3Month),
				tabs);
		setTab("tag4", R.id.graphAllPeriod,
				res.getString(R.string.graphAllPeriod), tabs);

		Log.d(LOG_TAG, "stat_idssss = " + stat_id);

		GraphView graphViewWeek = new LineGraphView(this,
				res.getString(R.string.pressure_stat));
		GraphView graphViewMonth = new LineGraphView(this,
				res.getString(R.string.pressure_stat));
		GraphView graphView3Month = new LineGraphView(this,
				res.getString(R.string.pressure_stat));
		GraphView graphViewAllPeriod = new LineGraphView(this,
				res.getString(R.string.pressure_stat));

		db = new MyDB(this);
		db.open();

		int all_stat_records = db.getCountElementsStat(stat_id);
		createGraph(7, all_stat_records, layoutWeek, graphViewWeek, stat_id);
		createGraph(30, all_stat_records, layoutMonth, graphViewMonth, stat_id);
		createGraph(90, all_stat_records, layout3Month, graphView3Month,
				stat_id);
		createGraph(all_stat_records, all_stat_records, layoutAllPeriod,
				graphViewAllPeriod, stat_id);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((rotation) && (keyCode == KeyEvent.KEYCODE_BACK)) {
			// preventing default implementation previous to
			// android.os.Build.VERSION_CODES.ECLAIR
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		// закрываем подключение при выходе
		super.onDestroy();
	}

	void setTab(String tag, int id, String periodName, TabHost tabs) {
		TabHost.TabSpec spec;
		spec = tabs.newTabSpec(tag);
		spec.setContent(id);
		spec.setIndicator(periodName);
		tabs.addTab(spec);
	}

	private void createGraph(int period, int all_stat_records,
			LinearLayout layout, GraphView graphView, int id) {
		if (period <= all_stat_records) {
			LinkedList<String[]> list = new LinkedList<String[]>();
			list = db.getStat(id, period);
			int count = Integer.valueOf(period);
			GraphViewData[] dataPulse = new GraphViewData[count];
			GraphViewData[] dataSys = new GraphViewData[count];
			GraphViewData[] dataDias = new GraphViewData[count];

			for (int i = 0; i < count; ++i) {
				dataPulse[i] = new GraphViewData(i + 1, Double.valueOf(list
						.get(0)[i]));
				dataSys[i] = new GraphViewData(i + 1, Double.valueOf(list
						.get(1)[i]));
				dataDias[i] = new GraphViewData(i + 1, Double.valueOf(list
						.get(2)[i]));
			}

			GraphViewSeriesStyle stylePulse = new GraphViewSeriesStyle();
			stylePulse.color = Color.rgb(0, 500, 0);
			GraphViewSeries seriesPulse = new GraphViewSeries(
					res.getString(R.string.pulse), stylePulse, dataPulse);

			GraphViewSeriesStyle styleSys = new GraphViewSeriesStyle();
			styleSys.color = Color.rgb(200, 50, 0);
			GraphViewSeries seriesSys = new GraphViewSeries(
					res.getString(R.string.sys), styleSys, dataSys);

			GraphViewSeriesStyle styleDias = new GraphViewSeriesStyle();
			styleDias.color = Color.rgb(500, 500, 0);
			GraphViewSeries seriesDias = new GraphViewSeries(
					res.getString(R.string.dias), styleDias, dataDias);

			graphView.addSeries(seriesSys);
			graphView.addSeries(seriesDias);
			graphView.addSeries(seriesPulse);

			graphView.setShowLegend(true);

			graphView.getGraphViewStyle().setNumVerticalLabels(6);
			graphView.setLegendAlign(LegendAlign.TOP);
			graphView.setLegendWidth(130);

			layout.addView(graphView);
		}
	}
}
