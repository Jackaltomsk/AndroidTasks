package projects.my.stopwatch.activities;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import projects.my.stopwatch.R;
import projects.my.stopwatch.common.ActivityUtils;

@EActivity(R.layout.activity_background_img)
public class BackgroundImgActivity extends AppCompatActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setToolbar(this, false);
    }*/

    @AfterViews
    public void init() {
        ActivityUtils.setToolbar(this, true);
    }
}
