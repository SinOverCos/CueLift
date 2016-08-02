package me.tanwang.cuelift;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

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
    }

}
