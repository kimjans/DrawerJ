package com.example.stest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	
	private int NUM_PAGES =3;
	
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mViewPager = (ViewPager) findViewById(R.id.ViewPager_information_content_view);
		mViewPager.setAdapter(    new pagerAdapter(    getSupportFragmentManager()   )      );
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public final static int FRAGMENT_PAGE1 = 0;
	public final static int FRAGMENT_PAGE2 =1;
	public final static int FRAGMENT_PAGE3 = 2;
	
	private class pagerAdapter extends FragmentPagerAdapter{
		
		pagerAdapter( android.support.v4.app.FragmentManager fm){
			super(fm);
		}
		@Override
		public Fragment getItem(int position){
			
			switch(position){
				case FRAGMENT_PAGE1 :
						return new page1Activity();
				case FRAGMENT_PAGE2 :
					return new page2Activity();
				case FRAGMENT_PAGE3 :
					return new page3Activity();
				default :
						return null;
			}
			
		}
		
		@Override
		public int getCount(){
			return NUM_PAGES;
		}
		
		
	}
}
