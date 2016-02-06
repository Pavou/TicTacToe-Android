package com.avoupavou.btcross;

import android.util.Log;

import java.io.PrintWriter;

/**
 * Created by Pantazis on 12/23/2015.
 */
public class Sender implements Runnable {

    private String message;
    private PrintWriter writer;

    public Sender(String msg,PrintWriter wrtr){
        message=msg;
        writer=wrtr;
    }
    @Override
    public void run() {
        if(writer!=null) {
            writer.println(message);
        }else{
            Log.d("Sender", "Writer null");
        }
    }
}
