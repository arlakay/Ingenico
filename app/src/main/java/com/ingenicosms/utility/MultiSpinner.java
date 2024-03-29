package com.ingenicosms.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ingenicosms.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by ILM on 8/10/2016.
 */
public class MultiSpinner extends Spinner implements OnMultiChoiceClickListener, OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private String defaultText = "Select Spareparts Items";
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append("; \n");
            }
        }

        String spinnerText = "";
        spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2) {
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = "";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                new String[]{spinnerText});
        setAdapter(adapter);
        if (selected.length > 0) {
            listener.onItemsSelected(selected);
        }

    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Spareparts");
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    /**
     * Sets items to this spinner.
     *
     * @param items    A TreeMap where the keys are the values to display in the spinner
     *                 and the value the initial selected state of the key.
     * @param listener A MultiSpinnerListener.
     */
    public void setItems(TreeMap<String, Boolean> items,
                         MultiSpinnerListener listener) {
        this.items = new ArrayList<>(items.keySet());
        this.listener = listener;

        List<Boolean> values = new ArrayList<>(items.values());
        selected = new boolean[values.size()];
        for (int i = 0; i < items.size(); i++) {
            selected[i] = values.get(i);
        }

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, new String[]{defaultText});
        setAdapter(adapter);

        // Set Spinner Text
        onCancel(null);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }

}
