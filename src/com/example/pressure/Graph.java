package com.example.pressure;

import java.util.LinkedList;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.TabHost;

import android.graphics.Color;
import android.util.Log;
import android.widget.Button;

public class Graph extends Activity {

	String stat_id, rotation;
	String count_data_string;

	MyDB db;

	int period = 0;

	Button btnWeek, btnMonth, btn3Month, btnAll;

	String[] savedValues = new String[2];

	final String LOG_TAG = "myLogs";

	int number_of_elements;

	TabHost tabs;

	TabHost.TabSpec spec;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		savedValues = LoadPreferences();

		stat_id = savedValues[0];
		rotation = savedValues[1];

		if (Integer.valueOf(rotation) == 1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				&& (Integer.valueOf(rotation) == 0)) {
			finish();
		}

		tabs = (TabHost) findViewById(R.id.tabhost);

		tabs.setup();

		setTab("tag1", R.id.graphWeek, "week");
		setTab("tag2", R.id.graphMonth, "month");
		setTab("tag3", R.id.graph3Month, "3 month");
		setTab("tag4", R.id.graphAllPeriod, "all period");

		Log.d(LOG_TAG, "stat_idssss = " + stat_id);
		LinearLayout layoutWeek = (LinearLayout) findViewById(R.id.graphWeek);
		LinearLayout layoutMonth = (LinearLayout) findViewById(R.id.graphMonth);
		LinearLayout layout3Month = (LinearLayout) findViewById(R.id.graph3Month);
		LinearLayout layoutAllPeriod = (LinearLayout) findViewById(R.id.graphAllPeriod);

		GraphView graphViewWeek = new LineGraphView(this, "Pressure statistics");
		GraphView graphViewMonth = new LineGraphView(this,
				"Pressure statistics");
		GraphView graphView3Month = new LineGraphView(this,
				"Pressure statistics");
		GraphView graphViewAllPeriod = new LineGraphView(this,
				"Pressure statistics");

		db = new MyDB(this);
		db.open();

		number_of_elements = db.getCountElementsStat();
		createGraph(7, layoutWeek, graphViewWeek);
		createGraph(30, layoutMonth, graphViewMonth);
		createGraph(90, layout3Month, graphView3Month);
		createGraph(number_of_elements, layoutAllPeriod, graphViewAllPeriod);
	}

	private String[] LoadPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String[] data = new String[2];
		data[0] = sharedPreferences.getString("idName", "");
		data[1] = sharedPreferences.getString("rotation", "0");
		return data;
	}

	protected void onDestroy() {
		// закрываем подключение при выходе
		db.close();
		super.onDestroy();
	}

	void setTab(String tag, int id, String periodName) {
		spec = tabs.newTabSpec(tag);
		spec.setContent(id);
		spec.setIndicator(periodName);
		tabs.addTab(spec);
	}

	public void createGraph(int period, LinearLayout layout, GraphView graphView) {
		if (period <= number_of_elements) {
			LinkedList<String[]> list = new LinkedList<String[]>();
			list = db.getStat(Long.valueOf(stat_id), period);
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
			GraphViewSeries seriesPulse = new GraphViewSeries("Pulse",
					stylePulse, dataPulse);

			GraphViewSeriesStyle styleSys = new GraphViewSeriesStyle();
			styleSys.color = Color.rgb(200, 50, 0);
			GraphViewSeries seriesSys = new GraphViewSeries("Sys.", styleSys,
					dataSys);

			GraphViewSeriesStyle styleDias = new GraphViewSeriesStyle();
			styleDias.color = Color.rgb(500, 500, 0);
			GraphViewSeries seriesDias = new GraphViewSeries("Dias.",
					styleDias, dataDias);

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