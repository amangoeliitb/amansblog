package com.blogspot.amangoeliitb.amansblog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {
    private SwipeRefreshLayout swipeContainer;

    public class individualpost {
        String title ;
        String contents ;
        String date ;
        public individualpost(String title, String contents, String date) {
            this.title = title ;
            this.contents = contents ;
            this.date = date ;
        }
    }

    private ArrayAdapter <String> listViewAdapter;
    public ArrayList <String> contentsoftheposts ;
    public ArrayList <String> titlesoftheposts ;
    public ArrayList <String> dateoftheposts ;
    private static final String TAG = "MyActivity";
    DBHelper mydb ;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Visit www.amangoeliitb.blogspot.com for complete guidance on competitive exam preparation!");
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        titlesoftheposts = new ArrayList<String>();
        contentsoftheposts = new ArrayList<String>() ;
        dateoftheposts = new ArrayList<String>() ;

        mydb = new DBHelper(getActivity()) ;
        int rows = mydb.numberOfRows();
        if(rows > 0){
            Log.v(TAG, "Database already exists") ;
            titlesoftheposts = mydb.getAllTitles();
            contentsoftheposts = mydb.getAllContents();
            dateoftheposts = mydb.getAlldates();
            for(int i = 0 ; i < titlesoftheposts.size() ; i++) {
                Log.v(titlesoftheposts.get(i), dateoftheposts.get(i));
            }
        }
        else {
            Log.v(TAG, "Creating the database") ;
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("http://www.amangoeliitb.blogspot.com/feeds/posts/default");
        }
        listViewAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        titlesoftheposts);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = titlesoftheposts.get(position);
                String content_post = contentsoftheposts.get(position);
                String date_post = dateoftheposts.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TITLE", forecast);
                extras.putString("CONTENT", content_post);
                extras.putString("DATE", date_post.substring(0, 10));
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG, "Swiped!") ;
                FetchWeatherTask weatherTask = new FetchWeatherTask();
                weatherTask.execute("http://www.amangoeliitb.blogspot.com/feeds/posts/default");
                swipeContainer.setRefreshing(false);
            }
        });

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        private String[] getWeatherDataFromJson(String forecastJsonStr)
                throws XmlPullParserException, IOException {

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setInput(new ByteArrayInputStream(forecastJsonStr.getBytes(StandardCharsets.UTF_8)), null);

            int event ;
            String text = null ;

            ArrayList <String> listoftitles = new ArrayList<String>() ;
            titlesoftheposts.clear() ;
            contentsoftheposts.clear();
            dateoftheposts.clear();

            try {
                event = myparser.getEventType();
                boolean titlefound = false ;

                while(event != XmlPullParser.END_DOCUMENT) {
                    String name = myparser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG :
                            break ;

                        case XmlPullParser.TEXT :
                            text = myparser.getText();
                            break;

                        case XmlPullParser.END_TAG :
                            if(name.equals("content")) {
                                contentsoftheposts.add(text) ;
                            }
                            else if(name.equals("title")) {
                                if(titlefound) {
                                    titlesoftheposts.add(text) ;
                                    listoftitles.add(text);
                                }
                                else
                                    titlefound = true ;
                            }
                            else if(name.equals("published")) {
                                dateoftheposts.add(text) ;
                            }
                            break ;
                    }
                    event = myparser.next();
                }
                mydb.clearDatabase() ;
                Log.v(LOG_TAG, "Nothing in database, fetching from internet");
                for(int i = 0 ; i < contentsoftheposts.size() ; i++) {
                    mydb.insertPost(titlesoftheposts.get(i), contentsoftheposts.get(i), dateoftheposts.get(i)) ;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            String[] resultStrs = new String[listoftitles.size()];
            resultStrs = listoftitles.toArray(resultStrs);

            return resultStrs;

        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {
                URL url = new URL(params[0]);

                Log.v(LOG_TAG, "Built URI " + params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                listViewAdapter.clear();
                for(String dayForecastStr : result) {
                    listViewAdapter.add(dayForecastStr);
                }
            }
        }
    }
}