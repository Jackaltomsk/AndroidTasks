package projects.my.stopwatch.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.Random;

import projects.my.stopwatch.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        GridLayout grid = (GridLayout) findViewById(R.id.color_grid);
        fillGridWithColors(grid);
    }

    /**
     * Реализует обработку клика по цвету.
     * @param view Вью кнопки.
     * @throws NullPointerException Исключение в случае, если переданное вью равно null.
     */
    private void handleImageViewClick(View view) throws NullPointerException {
        Button btn = (Button) view;
        if (btn == null) throw new NullPointerException("Вью ячейки должна быть кнопкой.");

        int color = ((ColorDrawable) btn.getBackground()).getColor();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("color", color);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Реализует заполнение гридп кнопками-цветами.
     * @param grid Грид.
     */
    private void fillGridWithColors(GridLayout grid) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleImageViewClick(v);
            }
        };

        int elementsCount = grid.getColumnCount() * grid.getRowCount();
        for (int element = 0; element < elementsCount; element++ ) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            Button view = new Button(this);
            view.setOnClickListener(listener);
            view.setBackgroundColor(color);
            grid.addView(view);
        }
    }
}