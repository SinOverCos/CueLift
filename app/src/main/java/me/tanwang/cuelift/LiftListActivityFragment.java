package me.tanwang.cuelift;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LiftListActivityFragment extends ListFragment {

    private ArrayList<Lift> lifts;

    @Override
    public void onStart() {
        super.onStart();
        TextView emptyListTextView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.view_empty_lift_list, getListView(), false);
        ((ViewGroup) getListView().getParent()).addView(emptyListTextView);
        getListView().setEmptyView(emptyListTextView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifts = new ArrayList<Lift>();
        LiftAdapter liftAdapter = new LiftAdapter(lifts);
        setListAdapter(liftAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    private class LiftAdapter extends ArrayAdapter<Lift> {

        public LiftAdapter(ArrayList<Lift> lifts) {
            super(getActivity(), 0, lifts); // 0 means no pre-defined layout
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if no view, inflate one
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_lift, null);
            }

            Lift lift = getItem(position);

            TextView textView = (TextView) convertView.findViewById(R.id.textview);
            textView.setText(lift.toString());

            return convertView;
        }

    }
}
