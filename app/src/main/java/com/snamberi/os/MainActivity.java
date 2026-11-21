package com.snamberi.os;
import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.webkit.*;
import android.widget.Toast;
import android.view.View;
import java.net.URLEncoder;

public class MainActivity extends Activity {
 WebView web;
 private static final int PICK_AUDIO=310;
 private ValueCallback<Uri[]> uploadCallback;
 @Override public void onCreate(Bundle b){
  super.onCreate(b);
  getWindow().setStatusBarColor(0xff0b1020);
  getWindow().setNavigationBarColor(0xff0b1020);
  if(Build.VERSION.SDK_INT>=33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=android.content.pm.PackageManager.PERMISSION_GRANTED)
   requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},7);
  ReminderReceiver.channel(this);
  web=new WebView(this);
  web.getSettings().setJavaScriptEnabled(true);
  web.getSettings().setDomStorageEnabled(true);
  web.setWebViewClient(new WebViewClient());
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams params){
    if(uploadCallback!=null)uploadCallback.onReceiveValue(null);
    uploadCallback=cb;
    try{startActivityForResult(params.createIntent(),PICK_AUDIO);return true;}
    catch(Exception e){uploadCallback=null;return false;}
   }
  });
  web.addJavascriptInterface(new Bridge(),"AndroidReminders");
  setContentView(web);
  web.loadUrl("file:///android_asset/index.html");
 }
 @Override protected void onActivityResult(int req,int result,Intent data){
  super.onActivityResult(req,result,data);
  if(req==PICK_AUDIO&&uploadCallback!=null){
   uploadCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result,data));
   uploadCallback=null;
  }
 }
 class Bridge {
  @JavascriptInterface public void openExternal(String url){runOnUiThread(()->{try{Uri u=Uri.parse(url);if(!"https".equals(u.getScheme())||!"www.instagram.com".equals(u.getHost()))return;startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception e){android.util.Log.e("Snamberi","Instagram",e);}});}
  @JavascriptInterface public void openChatGPT(String prompt){runOnUiThread(()->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://chatgpt.com/?q="+URLEncoder.encode(prompt,"UTF-8"))));}catch(Exception e){android.util.Log.e("Snamberi","ChatGPT",e);}});}
  @JavascriptInterface public void sync(String json){getSharedPreferences("reminders",0).edit().putString("schedule",json).apply();ReminderReceiver.scheduleAll(MainActivity.this,json);}
  @JavascriptInterface public void pinApp(){runOnUiThread(()->{try{startLockTask();}catch(Exception e){Toast.makeText(MainActivity.this,"Enable screen pinning in Android Settings",Toast.LENGTH_LONG).show();}});}
  @JavascriptInterface public void unpinApp(){runOnUiThread(()->{try{stopLockTask();}catch(Exception ignored){}});}
  @JavascriptInterface public void playMusic(String uri,String title){runOnUiThread(()->{
   try{
    Uri u=Uri.parse(uri);
    if(!"content".equals(u.getScheme()))return;
    Intent i=new Intent(MainActivity.this,MusicService.class).setAction(MusicService.PLAY);
    i.putExtra("uri",uri);i.putExtra("title",title);
    if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);
   }catch(Exception e){Toast.makeText(MainActivity.this,"Cannot play this audio file",Toast.LENGTH_SHORT).show();}
  });}
  @JavascriptInterface public void musicCommand(String command){runOnUiThread(()->{
   if(!command.equals("pause")&&!command.equals("resume")&&!command.equals("stop"))return;
   Intent i=new Intent(MainActivity.this,MusicService.class).setAction(command);
   try{startService(i);}catch(Exception ignored){}
  });}
 }
 @Override public void onBackPressed(){
  web.evaluateJavascript("(function(){document.querySelector('[data-tab=\\\"Dashboard\\\"]')?.click();return true})()",null);
 }
}
