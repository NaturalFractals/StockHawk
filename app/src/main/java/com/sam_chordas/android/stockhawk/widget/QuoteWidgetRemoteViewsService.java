package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 *
 * @author Jesse Cochran
 */
public class QuoteWidgetRemoteViewsService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor;

            @Override
            public void onCreate() {
                //Nothing to do here
            }

            @Override
            public void onDataSetChanged() {
                if(cursor != null) {
                    cursor.close();
                }

                long id = Binder.clearCallingIdentity();

                cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + "= ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(id);
            }

            @Override
            public void onDestroy() {
                if(cursor != null){
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
                    return  null;
                }

                String stockSymbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
                String stockBidPrice = cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE));
                String stockPercentChange = cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_collection_item);
                views.setTextViewText(R.id.stock_symbol, stockSymbol);
                views.setTextViewText(R.id.bid_price, stockBidPrice);
                views.setTextViewText(R.id.change, stockPercentChange);

                Intent fillIntent = new Intent();
                Uri uri = QuoteProvider.Quotes.withSymbol(stockSymbol);
                fillIntent.setData(uri);

                views.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_collection_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(cursor.moveToPosition(position)) {
                    return cursor.getLong(cursor.getColumnIndex(QuoteColumns._ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
