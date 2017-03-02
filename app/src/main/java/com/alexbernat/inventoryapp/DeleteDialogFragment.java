package com.alexbernat.inventoryapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Александр on 02.03.2017.
 */

public class DeleteDialogFragment extends DialogFragment {

    private Uri contentUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        contentUri = Uri.parse(args.getString(DetailActivity.KEY_CONTENT_URI));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_message).setTitle(R.string.dialog_delete_title);
        builder.setPositiveButton(R.string.dialog_delete_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int rowsDeleted = getActivity().getContentResolver().delete(contentUri, null, null);
                if (rowsDeleted != 0) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_fail_delete, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                getActivity().finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
