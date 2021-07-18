package com.thenextlevel.wallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thenextlevel.wallpaper.Adapter.WallpaperAdapter;
import com.thenextlevel.wallpaper.Model.WallpaperModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RecyclerView recyclerView;
    WallpaperAdapter wallpaperAdapter;
    List<WallpaperModel> wallpaperModelList;

    int length;
    int pageNumber = 1;
    int pageNumberForSearch = 1;
    int currentItems, totalItems, scrollOutItems;
    Boolean isClickOnSearchButton = false;

    String url;
    String urlForSearch;
    String searchWord;
    Boolean isScrolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpaperModelList);

        recyclerView.setAdapter(wallpaperAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isClickOnSearchButton && isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    urlForSearch = "https://api.pexels.com/v1/search/?page=" + pageNumberForSearch + "&per_page=80&query=" + searchWord;
                    fetchWallpaper(urlForSearch);
                }

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    Log.w(TAG, "onScrolled: 123" );
                    isScrolling = false;
                    url = "https://api.pexels.com/v1/curated/?page=" + pageNumber + "&per_page=80";
                    fetchWallpaper(url);
                    
                }

            }
        });
        url = "https://api.pexels.com/v1/curated/?page=" + pageNumber + "&per_page=80";
        fetchWallpaper(url);

    }

    public void fetchWallpaper(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            Log.w(TAG, "onResponse: "+pageNumber );
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");

                            length = jsonArray.length();

                            for (int i = 0; i < length; i++) {

                                JSONObject object = jsonArray.getJSONObject(i);

                                int id = object.getInt("id");

                                JSONObject objectImages = object.getJSONObject("src");

                                String originalUrl = objectImages.getString("original");
                                String mediumUrl = objectImages.getString("medium");

                                WallpaperModel wallpaperModel = new WallpaperModel(id, originalUrl, mediumUrl);
                                wallpaperModelList.add(wallpaperModel);
                            }

                            wallpaperAdapter.notifyDataSetChanged();
                            if (isClickOnSearchButton) {
                                pageNumberForSearch++;
                            } else {
                                pageNumber++;
                                Log.w(TAG, "onResponse: page number" );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> param = new HashMap<>();
                param.put("Authorization", "563492ad6f91700001000001e684d6a44a36491997f21b174182737d");
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);

        isClickOnSearchButton = true;

        Log.w(TAG, "onCreateOptionsMenu: click");

        MenuItem menuItem = menu.findItem(R.id.search_view);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchWord = searchView.getQuery().toString().toLowerCase();
                urlForSearch = "https://api.pexels.com/v1/search/?page=" + pageNumberForSearch + "&per_page=80&query=" + searchWord;
                wallpaperModelList.clear();
                fetchWallpaper(urlForSearch);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText searchEditText = new EditText(this);
        searchEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        alert.setTitle("Search here");
        alert.setView(searchEditText);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                searchWord = searchEditText.getText().toString().toLowerCase();
                urlForSearch = "https://api.pexels.com/v1/search/?page=" + pageNumberForSearch + "&per_page=80&query=" + searchWord;
                wallpaperModelList.clear();
                fetchWallpaper(urlForSearch);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.show();

        return super.onOptionsItemSelected(item);
    }
}
