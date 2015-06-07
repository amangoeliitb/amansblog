package com.blogspot.amangoeliitb.amansblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class DetailActivityFragment extends Fragment {
    public static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
            Log.d(LOG_TAG, "Share action provider is null");
    }

    String process(String date) {
        Log.v(LOG_TAG, date) ;

        String m, d, y ;
        y = date.substring(0, 4) ;
        Log.v(LOG_TAG, y) ;

        m = date.substring(5, 7) ;
        Log.v(LOG_TAG, m) ;

        d = date.substring(8, 10) ;
        int month = Integer.parseInt(m) ;
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"} ;
        Log.v(LOG_TAG, y + " " + m + " " + d) ;
        return months[month] + " " + d + ", " + y ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        Bundle extras = intent.getExtras();
        String title = extras.getString("TITLE") ;
        String content = extras.getString("CONTENT") ;
        String date = extras.getString("DATE") ;
        date = process(date) ;
        ((TextView) rootView.findViewById(R.id.post_title)).setText(title);
        ((TextView) rootView.findViewById(R.id.post_date)).setText(date);
        ((WebView) rootView.findViewById(R.id.post_content)).loadData(content, "text/html", "UTF-8");

        return rootView;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Visit www.amangoeliitb.blogspot.com for complete guidance on competitive exam preparation!");
        return shareIntent;
    }
}
