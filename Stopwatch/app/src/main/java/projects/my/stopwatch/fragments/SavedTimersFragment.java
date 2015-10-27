package projects.my.stopwatch.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import projects.my.stopwatch.R;

/**
 * Фрагмент, отображаюющий список сохраненных таймеров.
 */
public class SavedTimersFragment extends DialogFragment {
    private final static String TIME_LIST = "TIME_LIST";
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private ListView list;

    // TODO: Rename and change types of parameters
    public static SavedTimersFragment newInstance(String param1, String param2) {
        SavedTimersFragment fragment = new SavedTimersFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedTimersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createListAdapter(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_timers_fragment, container);
        return view;
    }

    public void setAdapterContents(String[] values) {
        adapter.clear();
        adapter.addAll(values);
    }

    private void createListAdapter(Bundle savedInstanceState) {
        if (savedInstanceState == null) listItems = new ArrayList<>();
        else listItems = savedInstanceState.getStringArrayList(TIME_LIST);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,
                R.id.textItem, listItems);
        list.setAdapter(adapter);
    }
}
