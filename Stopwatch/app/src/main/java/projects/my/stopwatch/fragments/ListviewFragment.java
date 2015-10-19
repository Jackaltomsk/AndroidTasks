package projects.my.stopwatch.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import projects.my.stopwatch.R;

public class ListviewFragment extends Fragment {
    private final static String TIME_LIST = "TIME_LIST";
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private ListView list;

    private OnListActionListener listener;

    public interface OnListActionListener {
        public String getItemText();
        public boolean canAddItemText();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listview_fragment, container, false);
        Button addItem = (Button) view.findViewById(R.id.button_add_time);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener.canAddItemText()) {
                    CharSequence text = listener.getItemText();
                    listItems.add(text.toString());
                    adapter.notifyDataSetChanged();
                    list.setSelection(adapter.getCount() - 1);
                }
            }
        });
        list = (ListView) view.findViewById(R.id.time_listView);
        createListAdapter(savedInstanceState);
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

    private void createListAdapter(Bundle savedInstanceState) {
        if (savedInstanceState == null) listItems = new ArrayList<>();
        else listItems = savedInstanceState.getStringArrayList(TIME_LIST);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,
                R.id.textItem, listItems);
        list.setAdapter(adapter);
    }

}
