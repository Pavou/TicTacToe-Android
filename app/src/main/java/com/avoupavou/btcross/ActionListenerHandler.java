package com.avoupavou.btcross;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Pantazis on 11/29/2015.
 */
public class ActionListenerHandler implements WifiP2pManager.ActionListener {

    private Activity mActivity;
    private String message;
    public ActionListenerHandler(Activity mActivity,String msg){
        this.mActivity=mActivity;
        this.message=msg;
    }

    @Override
    public void onSuccess() {
        Log.d("_", message + " SUCCESS");
    }

    @Override
    public void onFailure(int i) {
        Log.d("_",message+ " FAILURE");
    }
}
