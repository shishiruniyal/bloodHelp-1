package com.mycompany.servelettest;

/**
 * Created by vikas on 15/4/16.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 * <p>
 * <p>This class is used by the and {@link
 * ScreenSlideActivity} samples.</p>
 */

/*{@link CardFlipActivity} */
public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
//    TextView exp ;
//    TextView name ;
//     String N="rocket";
//     String E="ssssssas";
    static int i = 0;
    TextView t;
    String messages[] = {"\"BloodHelp saving life through providing platform to find blood donors.\"\n----Team BloodHelp", "\"Be a donor be champion.\"\n-----Team BloodHelp", "\"If u wanna help other person who need blood, this app will help.. \""};
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    public ScreenSlidePageFragment() {
    }


//    public   void set(String exp,String name){
//        this.E = exp;
//        this.N = name;
//        Log.e("check",E);
//        Log.e("check",N);
//        Log.e("Check","******************************");
//    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);
        t = (TextView) rootView.findViewById(R.id.exp);
        t.setText(messages[i]);
        i++;
        if (i == 3) {
            i = 0;
        }
//        exp = (TextView)rootView.findViewById(R.id.exp);
//         name = (TextView)rootView.findViewById(R.id.name);
//        exp.setText(E);
//        name.setText(N);
        // Set the title view to show the page number.
//        ((TextView) rootView.findViewById(android.R.id.text1)).tsetTex(
        //  getString(R.string.title_template_step, mPageNumber + 1));

        return rootView;
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
