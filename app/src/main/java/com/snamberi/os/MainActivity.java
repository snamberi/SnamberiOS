package com.snamberi.os;
import android.app.*;import android.os.*;import android.content.*;import android.webkit.*;import org.json.*;
public class MainActivity extends Activity {
 WebView web;
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(0xff0b1020);getWindow().setNavigationBarColor(0xff0b1020);
  if(android.os.Build.VERSION.SDK_INT>=33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=android.content.pm.PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},7);
  ReminderReceiver.channel(this);web=new WebView(this);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.setWebViewClient(new WebViewClient());web.addJavascriptInterface(new Bridge(),"AndroidReminders");setContentView(web);web.loadUrl("file:///android_asset/index.html");}
 class Bridge { @JavascriptInterface public void openExternal(String url){runOnUiThread(()->{try{android.net.Uri u=android.net.Uri.parse(url);if(!"https".equals(u.getScheme())||!"www.instagram.com".equals(u.getHost()))return;startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception e){android.util.Log.e("Snamberi","Cannot open Instagram",e);}});} @JavascriptInterface public void openChatGPT(String prompt){runOnUiThread(()->{try{String url="https://chatgpt.com/?q="+java.net.URLEncoder.encode(prompt,"UTF-8");startActivity(new Intent(Intent.ACTION_VIEW,android.net.Uri.parse(url)));}catch(Exception e){android.util.Log.e("Snamberi","Cannot open ChatGPT",e);}});} @JavascriptInterface public void sync(String json){getSharedPreferences("reminders",0).edit().putString("schedule",json).apply();ReminderReceiver.scheduleAll(MainActivity.this,json);} }
 @Override public void onBackPressed(){web.evaluateJavascript("(function(){document.querySelector('[data-tab=\"Dashboard\"]')?.click();return true})()",null);}
}
