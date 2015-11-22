package projects.my.stopwatch.activities;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;

import projects.my.stopwatch.R;
import projects.my.stopwatch.adapters.BackgroundImgAdapter;
import projects.my.stopwatch.common.ActivityUtils;
import projects.my.stopwatch.volley.QueueHolder;

@EActivity(R.layout.activity_background_img)
public class BackgroundImgActivity extends AppCompatActivity {
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

        // specify an adapter (see also next example)
        adapter = new BackgroundImgAdapter(new Bitmap[]{});
        recyclerView.setAdapter(adapter);
    }
}
