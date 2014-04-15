package com.example.pressure;

import java.util.LinkedList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class GraphPeriod extends Activity {

	String stat_id;
	String count_data_string_all, count_data_string_week,
			count_data_string_month, count_data_string_3month;
	GraphView graphView;
	String period;

	final String LOG_TAG = "myLogs";

	LinearLayout layout;

	MyDB db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_all_period);
//		stat_id = getIntent().getStringExtra("id_stat_key_all_period");
//		period = getIntent().getStringExtra(
//				"id_stat_count_all_period");
//		period = getIntent().getStringExtra("id_stat_week_amount");
//		period = getIntent().getStringExtra("id_stat_month_amount");
//		period = getIntent().getStringExtra("id_stat_3month_amount");

//		layout = (LinearLayout) findViewById(R.id.graphAllPeriod);
//		layout.removeView(graphView);
//
//		graphView = new LineGraphView(this // context
//				, "Pressure statistics" // heading
//		);
//		db = new MyDB(this);
//		db.open();
//		
//		int time_peroid = Integer.valueOf(period);
//		
//		Log.d(LOG_TAG, "PERIOD = " + time_peroid);
		
//		int week = Integer.valueOf(count_data_string_week);
//		int month = Integer.valueOf(count_data_string_month);
//		int month_3 = Integer.valueOf(count_data_string_3month);
//		int all = Integer.valueOf(count_data_string_all);
		
//		period = Integer.valueOf(count_data_string_all);
//		switch (time_peroid) {
//		case 7:
//			createGraph(7);
//			break;
//		case 30:
//			createGraph(30);
//			break;
//		case 89:
//			createGraph(89);
//			break;
//		case all:
//			createGraph(all);
//			break;
//		}
	}

	public void createGraph(int period) {
		if (period <= Integer.valueOf(90)) {
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
			stylePulse.color = Color.rgb(90, 250, 0);
			GraphViewSeries seriesPulse = new GraphViewSeries("Pulse",
					stylePulse, dataPulse);

			GraphViewSeriesStyle styleSys = new GraphViewSeriesStyle();
			styleSys.color = Color.rgb(200, 50, 0);
			GraphViewSeries seriesSys = new GraphViewSeries("Sys.", styleSys,
					dataSys);

			GraphViewSeriesStyle styleDias = new GraphViewSeriesStyle();
			styleDias.color = Color.rgb(300, 50, 160);
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
		} else
			notEnoughRecords();
	}

	protected void onDestroy() {
		// закрываем подключение при выходе
		db.close();
		super.onDestroy();
	}

	void notEnoughRecords() {
		Toast.makeText(this, R.string.not_enough_records, Toast.LENGTH_SHORT)
				.show();
	}
}