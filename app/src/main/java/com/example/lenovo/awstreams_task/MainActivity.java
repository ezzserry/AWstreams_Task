package com.example.lenovo.awstreams_task;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    VideosRecyclerAdapter adapter;
    EditText etSearch;
    RecyclerView recyclerView;
    ProgressDialog loading = null;
    private ArrayList<VideoItem> videoItemArrayList;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        loading = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setMessage("Please Wait");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        etSearch = (EditText) findViewById(R.id.search);
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    // check for Internet status
                    if (isInternetPresent) {

                        if (!etSearch.getText().toString().isEmpty()) {
                            String query = etSearch.getText().toString();
                            loadVideos(query);
                            return true;
                        } else
                            Snackbar.make(recyclerView, "enter your keyword ", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                    } else
                        Snackbar.make(recyclerView, "No internet connection ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }
                return false;
            }
        });
    }

    private void loadVideos(String query) {
        loading.show();

        String url = AppContants.SEARCH_SERVICE;
        url = String.format(url, query);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            videoItemArrayList = new ArrayList<>();
                            JSONArray itemsArray = response.getJSONArray("items");
                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject object = (JSONObject) itemsArray.get(i);
                                JSONObject snippetObject = object.getJSONObject("snippet");
                                VideoItem videoItem = new VideoItem();
                                videoItem.setTitle(snippetObject.getString("title"));
                                String thumbnailURL = snippetObject.getJSONObject("thumbnails").getJSONObject("default").getString("url");
                                videoItem.setThumbnail(thumbnailURL);
                                videoItemArrayList.add(videoItem);

                            }
                            loading.dismiss();
                            adapter = new VideosRecyclerAdapter(MainActivity.this, videoItemArrayList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Snackbar.make(recyclerView, "Error ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        loading.dismiss();
                    }
                }
        ) {

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(postRequest);

    }
}
