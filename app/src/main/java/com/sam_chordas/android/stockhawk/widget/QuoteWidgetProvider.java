package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockChartActivityFragment;

/**
 * This class builds the view for the widget and passes it on to the launcher
 * @author Jesse Cochran
 */
public class QuoteWidgetProvider extends AppWidgetProvider{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int temp: appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.stock_symbol, pendingIntent);

//            Intent clickIntentStockChart = new Intent(context, StockChartActivityFragment.class);
//            PendingIntent clickPendingIntent = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                clickPendingIntent = TaskStackBuilder.create(context)
//                        .addNextIntentWithParentStack(clickIntentStockChart)
//                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//            views.setOnClickPendingIntent(R.id.widget_list, clickPendingIntent);
            views.setRemoteAdapter(R.id.widget_list, new Intent(context, QuoteWidgetRemoteViewsService.class));
            appWidgetManager.updateAppWidget(temp, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(StockTaskService.ACTION_DATA_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] widgetIds  = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widget);

        }
    }
}
