package com.example.pressure;

import java.util.LinkedList;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GraphAllPeriod extends Activity {

	String stat_id;
	String count_data_string;
	GraphView graphView;
	int period = 0;

	final String LOG_TAG = "myLogs";

	MyDB db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_all_period);
		stat_id = getIntent().getStringExtra("id_stat_key_all_period");
		count_data_string = getIntent().getStringExtra("id_stat_count_all_period");

		LinearLayout layout = (LinearLayout) findViewById(R.id.graphAllPeriod);
		layout.removeView(graphView);

		graphView = new LineGraphView(this // context
				, "Pressure statistics" // heading
		);
		db = new MyDB(this);
		db.open();
		period = Integer.valueOf(count_data_string);
		if (period <= Integer.valueOf(count_data_string)) {
			LinkedList<String[]> list = new LinkedList<String[]>();
			list = db.getStat(Long.valueOf(stat_id),
					Integer.valueOf(count_data_string), period);

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