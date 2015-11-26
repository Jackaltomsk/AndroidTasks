package projects.my.stopwatch.fragments;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import projects.my.stopwatch.R;

/**
 * Фрагмент для показа изображения.
 */
@EFragment(R.layout.fragment_img)
public class FragmentImg extends DialogFragment {

    @ViewById(R.id.fragment_bckg_img_view)
    ImageView imageView;

    @ViewById(R.id.button_set_bckg_image)
    Button btn;

    @Click(R.id.button_set_bckg_image)
    public void onSetClick() {
        Toast.makeText(getActivity(), "Нажата кнопка установки фона", Toast.LENGTH_SHORT).show();
    }

    public void setImage(Bitmap img) {
        imageView.setImageBitmap(img);
    }

    public boolean isReady() {
        return imageView != null;
    }
}
