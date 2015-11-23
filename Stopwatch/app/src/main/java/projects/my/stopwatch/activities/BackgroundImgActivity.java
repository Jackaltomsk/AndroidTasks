package projects.my.stopwatch.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import projects.my.stopwatch.R;
import projects.my.stopwatch.adapters.BackgroundImgAdapter;
import projects.my.stopwatch.common.ActivityUtils;
import projects.my.stopwatch.volley.AuthJsonRequest;
import projects.my.stopwatch.volley.Constants;
import projects.my.stopwatch.volley.QueueHolder;

@EActivity(R.layout.activity_background_img)
public class BackgroundImgActivity extends AppCompatActivity {
    private static final String TAG = BackgroundImgActivity.class.getSimpleName();

    private static final int PORT_SPAN = 2;
    private static final int LAND_SPAN = 4;

    @ViewById(R.id.recycler_view_img)
    public RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Bean
    QueueHolder queueHolder;

    @AfterViews
    public void init() {
        ActivityUtils.setToolbar(this, true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setVerticalScrollBarEnabled(true);

        boolean isPortrait = getResources().getConfiguration().
                orientation == Configuration.ORIENTATION_PORTRAIT;
        layoutManager = new GridLayoutManager(this, isPortrait ? PORT_SPAN : LAND_SPAN);
        recyclerView.setLayoutManager(layoutManager);


        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(this, "Нет подключения к Internet", Toast.LENGTH_SHORT).show();
        }
        else {
            queueHolder.addToRequestQueue(new AuthJsonRequest(Constants.UMGUR_BASE + "gallery.json",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray a = response.names();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String data = new String(error.networkResponse.data);
                    Log.e(TAG, "Ошибка получения дпнных.");
                }
            }, Constants.CLIENT_ID + " " + Constants.IMGUR_APP_ID));
        }

        // specify an adapter (see also next example)
        adapter = new BackgroundImgAdapter(new Bitmap[]{});
        recyclerView.setAdapter(adapter);
    }
}
