package com.example.musicbox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InputDialogFragment extends DialogFragment {

    private InputDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.input_dialog, null,false);
        final EditText input = view.findViewById(R.id.input);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onInputDialogPositiveClick(InputDialogFragment.this, input.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setListener(InputDialogListener listener) {
        this.listener = listener;
    }

    public interface InputDialogListener {
        void onInputDialogPositiveClick(DialogFragment dialog, String input);
    }
}
