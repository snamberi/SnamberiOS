package com.snamberi.os;
import android.content.*;
public class BootReceiver extends BroadcastReceiver { @Override public void onReceive(Context c,Intent i){String json=c.getSharedPreferences("reminders",0).getString("schedule",null);if(json!=null)ReminderReceiver.scheduleAll(c,json);} }
