package com.example.pressure;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.util.Log;

public class Graph extends Activity {

	String stat_id;
	String[] statPulse, statSys, statDias, statDate;
	String count_data_string;

	MyDB db;

	final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		stat_id = getIntent().getStringExtra("id_stat_key");
		count_data_string = getIntent().getStringExtra("id_stat_count");

		Log.d(LOG_TAG, "stat_id= " + stat_id);
		db = new MyDB(this);
		db.open();

		statPulse = db.getCurrentStatPulse(Long.valueOf(stat_id),
				Integer.valueOf(count_data_string));
		statSys = db.getCurrentStatSys(Long.valueOf(stat_id),
				Integer.valueOf(count_data_string));
		statDias = db.getCurrentStatDias(Long.valueOf(stat_id),
				Integer.valueOf(count_data_string));
		statDate = db.getCurrentStatDate(Long.valueOf(stat_id),
				Integer.valueOf(count_data_string));

		Log.d(LOG_TAG, "COUNT2= " + count_data_string);

		Log.d(LOG_TAG, "DATE= " + statDate[0]);
		int count = Integer.valueOf(count_data_string);
		GraphViewData[] dataPulse = new GraphViewData[count];
		GraphViewData[] dataSys = new GraphViewData[count];
		GraphViewData[] dataDias = new GraphViewData[count];

		for (int i = 0; i < count; i++) {
			dataPulse[i] = new GraphViewData(i, Double.valueOf(statPulse[i]));
			dataSys[i] = new GraphViewData(i, Double.valueOf(statSys[i]));
			dataDias[i] = new GraphViewData(i, Double.valueOf(statDias[i]));
		}

		GraphViewSeriesStyle stylePulse = new GraphViewSeriesStyle();
		stylePulse.color = Color.rgb(90, 250, 0);
		GraphViewSeries seriesPulse = new GraphViewSeries("Pulse", stylePulse,
				dataPulse);

		GraphViewSeriesStyle styleSys = new GraphViewSeriesStyle();
		styleSys.color = Color.rgb(200, 50, 0);
		GraphViewSeries seriesSys = new GraphViewSeries("Sys. pressure",
				styleSys, dataSys);

		GraphViewSeriesStyle styleDias = new GraphViewSeriesStyle();
		styleDias.color = Color.rgb(300, 50, 160);
		GraphViewSeries seriesDias = new GraphViewSeries("Dias. pressure",
				styleDias, dataDias);

		GraphView graphView = new LineGraphView(this // context
				, "Pressure statistics" // heading
		);

		graphView.addSeries(seriesSys);
		graphView.addSeries(seriesDias);
		graphView.addSeries(seriesPulse);

		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setShowLegend(true);

		graphView.setViewPort(Double.valueOf(count_data_string) - 8, 7);
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.getGraphViewStyle().setNumVerticalLabels(8);
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(240);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
	}

	protected void onDestroy() {
		super.onDestroy();
		// закрываем подключение при выходе
		db.close();
	}
}
