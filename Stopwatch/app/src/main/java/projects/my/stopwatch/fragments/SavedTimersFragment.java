package projects.my.stopwatch.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

import projects.my.stopwatch.R;

/**
 * Фрагмент, отображаюющий список сохраненных таймеров.
 */
public class SavedTimersFragment extends DialogFragment {

    private final static String TIME_LIST = "TIME_LIST";
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private ListView list;

    public SavedTimersFragment() {
        listItems = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,
                R.id.textItem, listItems);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_timers_fragment, container);
        list = (ListView) view.findViewById(R.id.time_listView);
        createListAdapter(savedInstanceState);
        Dialog dialog = getDialog();
        dialog.setTitle(R.string.saved_timer_dialog_title);
        // Устанавливаем анимацию.
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(TIME_LIST, listItems);
    }

    public void setAdapterContents(Collection<String> values) {
        listItems.clear();
        listItems.addAll(values);

        //adapter.clear();
        //adapter.addAll(values);
    }

    private void createListAdapter(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setAdapterContents(savedInstanceState.getStringArrayList(TIME_LIST));
        }

        /*adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,
                R.id.textItem, listItems);*/
        list.setAdapter(adapter);
    }
}
