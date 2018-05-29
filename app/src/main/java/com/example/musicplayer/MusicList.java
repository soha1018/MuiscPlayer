package com.example.musicplayer;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MusicList extends Activity {
	private static final long INTERVAL = 2000;
	public static  int position=0;
	static boolean tag=false;
	private String[] cols = new String[]{MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,};
	private int[] ids = new int[]{R.id.title,R.id.artist};
	private long mFirstBackKeyPressTime=-1;
	private long mLastBackKeyPressTime=-1;
	static boolean prepared=false;
	private ListView myList;
	private LinearLayout layout;
	

    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyActivityManager.getIntance().addActivity(this);
        setContentView(R.layout.music_list);
        layout=(LinearLayout)findViewById(R.id.layout);
        myList=(ListView)findViewById(R.id.myList);
        
        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        
        startManagingCursor(cursor);
        
		SimpleCursorAdapter adapter=new SimpleCursorAdapter(this, R.layout.list_item, cursor, cols, ids);
		myList.setAdapter(adapter);
		myList.setOnItemClickListener(new ListListener());
		
		findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tag=false;
				intent(position);
			}
		});
	
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	private class ListListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
	       position =arg2;
	       tag=true;
	       intent(position);
			
		}

	}

	public void intent(int position) {
		// TODO Auto-generated method stub
		Intent it=new Intent(MusicList.this,MusicMain.class);
		it.putExtra("position", position);
		it.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(it);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mFirstBackKeyPressTime==-1){
			mFirstBackKeyPressTime=System.currentTimeMillis();
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
			
		}
		else{
			mLastBackKeyPressTime=System.currentTimeMillis();
			
			if((mLastBackKeyPressTime-mFirstBackKeyPressTime)<=INTERVAL){
				prepared=false;
				if(MusicMain.mPlayer!=null)
					MusicMain.mPlayer.reset();
				MyActivityManager.getIntance().exit();
			}else{
				mFirstBackKeyPressTime=mLastBackKeyPressTime;
				Toast.makeText(getApplicationContext(),"再按一次退出程序", Toast.LENGTH_LONG).show();
			}
		}
	}
    
}
