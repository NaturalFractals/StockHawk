package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.content.CursorLoader;
import android.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockChartActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private LineChartView mLineChartView;
    private Cursor mCursor;
    private LineSet mLineSet;

    public StockChartActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_chart, container, false);
        mLineChartView = (LineChartView) view.findViewById(R.id.linechart);
        mLineSet = new LineSet();
        formatLineSet();
        Bundle args = new Bundle();
        Intent intent = getActivity().getIntent();
        args.putString(getResources().getString(R.string.symbol), intent.getStringExtra(getResources().getString(R.string.symbol)));
        getLoaderManager().initLoader(0, args, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                QuoteProvider.Quotes.CONTENT_URI,
                new String[] {QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " =?",
                new String[]{args.getString(getResources().getString(R.string.symbol))},
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        createLineChart();
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }



    private void createLineChart() {
        mCursor.moveToFirst();
        for(int i = 0; i < mCursor.getCount(); i++) {
            float bidPrice = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            mLineSet.addPoint("" + i, bidPrice);
            mCursor.moveToNext();
        }

        mLineChartView.addData(mLineSet);
        mLineChartView.show();

    }

    private void formatLineSet() {
        mLineSet.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#461bba"))
                .setDotsColor(Color.parseColor("#623abc"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f})
                .beginAt(0);
    }

    private void setLineChartView() {

    }



}
