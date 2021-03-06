package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import java.text.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {


  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    final QuoteTaskService quoteTaskService = new QuoteTaskService( this );
    Bundle args = new Bundle();
    if (intent.getStringExtra( getString(R.string.tag_key) ).equals( getString(R.string.add_param) ) ){
      args.putString(getString(R.string.symbol_key), intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    quoteTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
  }
}
