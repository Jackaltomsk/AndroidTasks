package projects.my.stopwatch.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import projects.my.stopwatch.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ListviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TIME_LIST = "TIME_LIST";
    private ArrayList<String> listItems;
    private ArrayAdapter adapter;
    private ListView list;

    private OnListActionListener listener;

    public ListviewFragment() {
        // Required empty public constructor
    }

    public interface OnListActionListener {
        public String getItemText();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createListAdapter(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listview_fragment, container, false);
        list = (ListView) view.findViewById(R.id.time_listView);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnListActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(TIME_LIST, listItems);
    }

    public void handleAddtimeClick(View view) {
        CharSequence text = listener.getItemText();
        listItems.add(text.toString());
        adapter.notifyDataSetChanged();
        list.setSelection(adapter.getCount() - 1);
    }

    private void createListAdapter(Bundle savedInstanceState) {
        if (savedInstanceState == null) listItems = new ArrayList<>();
        else listItems = savedInstanceState.getStringArrayList(TIME_LIST);

        adapter = new ArrayAdapter<>(this, R.layout.listview_item, R.id.textItem, listItems);
        list.setAdapter(adapter);
    }

}
