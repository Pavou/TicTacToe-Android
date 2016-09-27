package com.avoupavou.btcross;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Pantazis on 2/3/2016.
 */
public class PopUpHelp extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Click on Device Name to connect and start game with the player\n If no device appears you are alone")
                .setPositiveButton("Thanks bro", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                })
                .setNegativeButton("I got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                   }
                });
        return builder.create();
    }
}