package com.mycompany.servelettest;

/**
 * Created by vikas on 15/4/16.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 * <p/>
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ScreenSlidePageFragment
 */
public class ScreenSlideActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;
    private static String url_login = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/experience";
    JSONParser jparser = new JSONParser();
    JSONArray experienceArray;
    SessionManager manager = new SessionManager();
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    public void addListenerOnButton() {
        Log.e("lku7yu", "inside listener");
        final Context context = this;
        Button button1 = (Button) findViewById(R.id.btmlogin);
        Button button2 = (Button) findViewById(R.id.btmsignup);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(ScreenSlideActivity.this, MainActivity.class);
                ScreenSlideActivity.this.startActivity(intent);

            }

        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(ScreenSlideActivity.this, RegisterActivity.class);
                ScreenSlideActivity.this.startActivity(intent);

            }

        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        //  new FetchExperience().execute();
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
        addListenerOnButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

//    private class FetchExperience extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... args) {
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            Log.e("Message", "before sending request");
//            experienceArray = jparser.makeHttpRequestArray(url_login, "GET", params);
//            if (experienceArray == null) {
//                Log.e("FetchExperience", "null Array returned");
//                return "fail";
//            }
//            else {
//                Log.e("FetchExperience", "object returned");
//                Log.e("FetchExperience", experienceArray.toString());
//                return "sucess";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//
////            ViewGroup rootView = (ViewGroup) inflater
////                    .inflate(R.layout.fragment_screen_slide_page, container, false);
//
//            if(s.equals("fail")){
//                exp.setText("BE HEALTY AND BE DONOR");
//                name.setText("Team BloodHelp");
//            }
//            else{
//                JSONObject temp = null;
//                try {
//                    for(int i=0;i<experienceArray.length();i++){
//                        temp = experienceArray.getJSONObject(i);
//                        exp.setText(temp.getString("experience"));
//                        name.setText(temp.getString("name"));
//                    }
//                }
//                catch (JSONException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}
