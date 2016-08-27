package me.tanwang.cuelift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

public class RepDialogFragment extends DialogFragment {

    private static final String TAG = "RepDialogFragment";

    public static final String SET_REPS = "me.tanwang.cuelift.set_reps";
    private int setReps;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setReps = getArguments().getInt(SET_REPS, -1);
        if (setReps == -1) {
            //Log.e(TAG, "No reps supplied! Setting to 0");
            setReps = 0;
        }

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reps, null);
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.set_reps_picker);
        numberPicker.setMinValue(0);
        numberPicker.setValue(setReps);
        numberPicker.setMaxValue(30);
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setReps = newVal;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.set_reps)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .create();
    }

    public static RepDialogFragment newInstance(int setReps) {
        Bundle dataPassed = new Bundle();
        dataPassed.putInt(SET_REPS, setReps);
        RepDialogFragment fragment = new RepDialogFragment();
        fragment.setArguments(dataPassed);
        return fragment;
    }

    private void sendResult(int result) {
        if(getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra(SET_REPS, setReps);
        getTargetFragment().onActivityResult(getTargetRequestCode(), result, intent);
    }
}
