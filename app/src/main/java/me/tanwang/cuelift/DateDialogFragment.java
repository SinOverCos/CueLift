package me.tanwang.cuelift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateDialogFragment extends DialogFragment {

    public static final String SET_DATE = "me.tanwang.cuelift.set_date";
    private Date setDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setDate = (Date) getArguments().getSerializable(SET_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(setDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.set_date_picker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
                getArguments().putSerializable(SET_DATE, setDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.set_date)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .create();
    }

    public static DateDialogFragment newInstance(Date date) {
        Bundle dataPassed = new Bundle();
        dataPassed.putSerializable(SET_DATE, date);
        DateDialogFragment fragment = new DateDialogFragment();
        fragment.setArguments(dataPassed);
        return fragment;
    }

    private void sendResult(int result) {
        if(getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra(SET_DATE, setDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), result, intent);
    }
}
