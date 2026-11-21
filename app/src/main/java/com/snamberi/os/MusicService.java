package com.snamberi.os;
import android.app.*;
import android.content.*;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
public class MusicService extends Service {
 public static final String PLAY="play";
 private static final String CHANNEL="snamberi_music";
 private MediaPlayer player;
 private String title="Local music";
 @Override public void onCreate(){super.onCreate();if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CHANNEL,"Music playback",NotificationManager.IMPORTANCE_LOW);((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);}}
 @Override public int onStartCommand(Intent i,int flags,int startId){
  if(i==null)return START_NOT_STICKY;
  String action=i.getAction();
  if(PLAY.equals(action)){
   String raw=i.getStringExtra("uri");
   if(raw==null){stopSelf();return START_NOT_STICKY;}
   title=i.getStringExtra("title");if(title==null)title="Local music";
   try{
    release();
    player=new MediaPlayer();
    player.setDataSource(this,Uri.parse(raw));
    player.setOnPreparedListener(p->{p.start();notifyPlaying();});
    player.setOnCompletionListener(p->stopSelf());
    player.setOnErrorListener((p,what,extra)->{stopSelf();return true;});
    startForeground(321,notification());
    player.prepareAsync();
   }catch(Exception e){stopSelf();}
  }else if("pause".equals(action)){if(player!=null&&player.isPlaying()){player.pause();notifyPlaying();}}
  else if("resume".equals(action)){if(player!=null&&!player.isPlaying()){player.start();notifyPlaying();}}
  else if("stop".equals(action)){stopSelf();}
  return START_NOT_STICKY;
 }
 private PendingIntent command(String action,int code){return PendingIntent.getService(this,code,new Intent(this,MusicService.class).setAction(action),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);}
 private Notification notification(){
  Intent open=new Intent(this,MainActivity.class);
  PendingIntent launch=PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  boolean playing=player!=null&&player.isPlaying();
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);
  return b.setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Snamberi OS · "+title).setContentText(playing?"Playing from your phone":"Music paused").setContentIntent(launch).setOngoing(playing)
   .addAction(playing?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,playing?"Pause":"Play",command(playing?"pause":"resume",1))
   .addAction(android.R.drawable.ic_menu_close_clear_cancel,"Stop",command("stop",2)).build();
 }
 private void notifyPlaying(){((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(321,notification());}
 private void release(){if(player!=null){try{player.stop();}catch(Exception ignored){}player.release();player=null;}}
 @Override public void onDestroy(){release();super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}
