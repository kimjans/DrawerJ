package com.example.stest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity{

	
	private int NUM_PAGES =3;
	
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mViewPager = (ViewPager) findViewById(R.id.ViewPager_information_content_view);
		//mViewPager.setAdapter(    new pagerAdapter(    getSupportFragmentManager()   )      );
		
		InfoDrawer j = (InfoDrawer) findViewById(R.id.jansDrawer);
		j.updateOffset(58, 130, 56);
	
		mViewPager.setAdapter(new PagerAdapterClass( getApplicationContext() ));
		mViewPager.setOffscreenPageLimit(3);//버벅거리지 않게 3개 페이지 미리로드.
		
		Button b = (Button) findViewById( R.id.button );
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				InfoDrawer j = (InfoDrawer) findViewById(R.id.jansDrawer);
				
				j.changeSliding(new LinearLayout( getApplicationContext()), 35, 150, 100);
				
			}
		});
		
		
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
	
	/**
     * PagerAdapter 
     */
    private class PagerAdapterClass extends PagerAdapter{
         
        private LayoutInflater mInflater;
        private boolean[] isInflate = new boolean[]{false,false,false};
 
        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
            
        }
         
        @Override
        public int getCount() {
            return 3;
        }
 
        @Override
        public Object instantiateItem(View pager, int position) {
        	
            View v = null;
//            if( isInflate[position]){
//            	return v;
//            }
            if(position==0){
                v = mInflater.inflate(R.layout.drawer_page1, null);
                isInflate[position] = true;
            }
            else if(position==1){
                v = mInflater.inflate(R.layout.drawer_page2, null);
                isInflate[position] = true;
            }else{
                v = mInflater.inflate(R.layout.drawer_page3, null);
                isInflate[position] = true;
            }
             
            ((ViewPager)pager).addView(v, 0);
             
            return v; 
        }
 
        @Override
        public void destroyItem(View pager, int position, Object view) {
            //((ViewPager)pager).removeView((View)view);
        }
         
        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj; 
        }
    }
	
}
