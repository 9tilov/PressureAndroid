package com.example.pressure;

import java.util.LinkedList;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.view.KeyEvent;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class Graph extends Activity implements OnClickListener {

	String stat_id;
	String count_data_string;

	MyDB db;

	GraphView graphView;

	int period = 0;

	Button btnWeek, btnMonth, btn3Month, btnAll;

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

		createGraph(Integer.valueOf(count_data_string));

		btnWeek = (Button) findViewById(R.id.btnWeek);
		btnMonth = (Button) findViewById(R.id.btnMonth);
		btn3Month = (Button) findViewById(R.id.btn3Month);
		btnAll = (Button) findViewById(R.id.btnAll);

		btnWeek.setOnClickListener(this);
		btnMonth.setOnClickListener(this);
		btn3Month.setOnClickListener(this);
		btnAll.setOnClickListener(this);

	}

	protected void onDestroy() {
		// закрываем подключение при выходе
		db.close();
		super.onDestroy();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnWeek:
			period = 7;
			if (period <= Integer.valueOf(count_data_string))
				createGraph(period);
			else
				createGraph(Integer.valueOf(count_data_string));
			break;
		case R.id.btnMonth:
			period = 30;
			if (period <= Integer.valueOf(count_data_string))
				createGraph(period);
			else
				createGraph(Integer.valueOf(count_data_string));
			break;
		case R.id.btn3Month:
			period = 90;
			if (period <= Integer.valueOf(count_data_string))
				createGraph(period);
			else
				createGraph(Integer.valueOf(count_data_string));
			break;
		case R.id.btnAll:
			createGraph(Integer.valueOf(count_data_string));
			break;
		}
	}

	public void createGraph(int period) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.removeView(graphView);

		graphView = new LineGraphView(this // context
				, "Pressure statistics" // heading
		);

		LinkedList<String[]> list = new LinkedList<String[]>();
		list = db.getStat(Long.valueOf(stat_id),
				Integer.valueOf(count_data_string), period);

		int count = Integer.valueOf(period);
		GraphViewData[] dataPulse = new GraphViewData[count];
		GraphViewData[] dataSys = new GraphViewData[count];
		GraphViewData[] dataDias = new GraphViewData[count];

		for (int i = 0; i < count; ++i) {
			dataPulse[i] = new GraphViewData(i + 1,
					Double.valueOf(list.get(0)[i]));
			dataSys[i] = new GraphViewData(i + 1,
					Double.valueOf(list.get(1)[i]));
			dataDias[i] = new GraphViewData(i + 1,
					Double.valueOf(list.get(2)[i]));
		}

		GraphViewSeriesStyle stylePulse = new GraphViewSeriesStyle();
		stylePulse.color = Color.rgb(90, 250, 0);
		GraphViewSeries seriesPulse = new GraphViewSeries("Pulse", stylePulse,
				dataPulse);

		GraphViewSeriesStyle styleSys = new GraphViewSeriesStyle();
		styleSys.color = Color.rgb(200, 50, 0);
		GraphViewSeries seriesSys = new GraphViewSeries("Sys.", styleSys,
				dataSys);

		GraphViewSeriesStyle styleDias = new GraphViewSeriesStyle();
		styleDias.color = Color.rgb(300, 50, 160);
		GraphViewSeries seriesDias = new GraphViewSeries("Dias.", styleDias,
				dataDias);

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
