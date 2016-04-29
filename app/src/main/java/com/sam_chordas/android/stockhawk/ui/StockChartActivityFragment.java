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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
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
    private static final int CURSOR_LOADER_ID = 0;;
    private Bundle args = new Bundle();

    public StockChartActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_chart, container, false);
        mLineChartView = (LineChartView) view.findViewById(R.id.linechart);
        mLineSet = new LineSet();
        formatLineSet();
        Intent intent = getActivity().getIntent();
        args.putString("symbol", intent.getStringExtra("symbol"));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                QuoteProvider.Quotes.CONTENT_URI,
                new String[] {QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{args.getString("symbol")},
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

    /**
     * Uses cursor to add data points to the LineSet. The LineSet is then added to the line chart
     * view and the line chart is displayed.
     */
    private void createLineChart() {
        float max = Integer.MIN_VALUE;
        float min = Integer.MAX_VALUE;
        mCursor.moveToFirst();
        for(int i = 0; i < mCursor.getCount(); i++) {
            float bidPrice = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            mLineSet.addPoint("", bidPrice);
            if(bidPrice < min) {
                min = bidPrice;
            }
            if(bidPrice > max) {
                max = bidPrice;
            }
            mCursor.moveToNext();
        }

        mLineChartView.addData(mLineSet);
        mLineChartView.setAxisBorderValues((int) min - 10, (int) max + 10);
        mLineChartView.show();

    }

    /**
     * Formats the layout, color, line, etc. of the line chart.
     */
    private void formatLineSet() {
        mLineSet.setColor(Color.parseColor("#758cbb"))
                .setDotsColor(Color.parseColor("#623abc"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f})
                .beginAt(0);
    }

}
