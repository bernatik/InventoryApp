package com.alexbernat.inventoryapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Александр on 03.03.2017.
 */

public class OrderDialogFragment extends DialogFragment {

    private String productName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        productName = args.getString(DetailActivity.KEY_CONTENT_URI);
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_order_message).setTitle(R.string.dialog_order_title);
        builder.setPositiveButton(R.string.dialog_order_phone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_fail_order, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_order_mail, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailBody = getString(R.string.order_body, productName);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_subject) + productName);
                intent.putExtra(Intent.EXTRA_TEXT, emailBody);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_fail_order, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
