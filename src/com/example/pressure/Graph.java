package com.example.pressure;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.util.Log;

public class Graph extends Activity {

	String stat_id;
	String[] statPulse, statSys, statDias;
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
		Log.d(LOG_TAG, "COUNT2= " + count_data_string);
		int count = Integer.valueOf(count_data_string);

		// init example series data
		GraphViewData[] dataPulse = new GraphViewData[count];
		for (int i = 0; i < count; i++) {
			dataPulse[i] = new GraphViewData(Double.valueOf(i),
					Double.valueOf(statPulse[i]));
		}

		GraphViewSeriesStyle stylePulse = new GraphViewSeriesStyle();
		stylePulse.color = Color.rgb(90, 250, 0);
		GraphViewSeries seriesPulse = new GraphViewSeries("Pulse", stylePulse,
				dataPulse);

		// init example series data
		GraphViewData[] dataSys = new GraphViewData[count];
		for (int i = 0; i < count; i++) {
			dataSys[i] = new GraphViewData(Double.valueOf(i),
					Double.valueOf(statSys[i]));
		}

		GraphViewSeriesStyle styleSys = new GraphViewSeriesStyle();
		styleSys.color = Color.rgb(200, 50, 0);
		GraphViewSeries seriesSys = new GraphViewSeries("Sys", styleSys,
				dataSys);

		// init example series data
		GraphViewData[] dataDias = new GraphViewData[count];
		for (int i = 0; i < count; i++) {
			dataDias[i] = new GraphViewData(Double.valueOf(i),
					Double.valueOf(statDias[i]));
		}

		GraphViewSeriesStyle styleDias = new GraphViewSeriesStyle();
		styleDias.color = Color.rgb(300, 50, 110);
		GraphViewSeries seriesDias = new GraphViewSeries("Dias", styleDias,
				dataDias);

		GraphView graphView = new LineGraphView(this // context
				, "Pressure statistics" // heading
		);

		
		graphView.addSeries(seriesSys);
		graphView.addSeries(seriesDias);
		graphView.addSeries(seriesPulse);
		// graphView.addSeries(); // data
		graphView.setScalable(true);
		// optional - legend
		graphView.setShowLegend(true);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
	}

	protected void onDestroy() {
		super.onDestroy();
		// закрываем подключение при выходе
		db.close();
	}
}
