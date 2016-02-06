package com.avoupavou.btcross;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Pantazis on 12/23/2015.
 */
public class Game extends AsyncTask<String,String,String> {
    private static final int TIME_OUT =500;
    private final String LOG_TAG="GameClass";
    private final int SERVER_PORT=8888;

    private boolean isServer;
    private InetAddress serverAddress;
    private InputStream inputStream;
    private BufferedReader reader;
    private PrintWriter writer;
    private ServerSocket server;
    private Socket socket;
    private Context context;
    private String message;
    private WiFiDirectActivity mActivity;

    private LanPlayer localPlayer;

    @Override
    protected String doInBackground(String... params) {
        String msg="";
        if(isServer){
            try {
                server = new ServerSocket(SERVER_PORT);
                socket=server.accept();
                server.close();
                inputStream = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                writer = new PrintWriter(socket.getOutputStream(), true);
                Log.d(LOG_TAG, "Server created on: " + SERVER_PORT);

                while(socket != null){
                    msg=reader.readLine();
                    if(msg==null||isCancelled()) break;
                    Log.d(LOG_TAG,"Reading msg: "+ msg);
                    publishProgress(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            try {
                Thread.sleep(1);
                socket = new Socket();
                socket.bind(null);
                socket.connect(new InetSocketAddress(serverAddress, SERVER_PORT), TIME_OUT);
                Log.d(LOG_TAG, "Connected to: " + serverAddress + " on port: " + SERVER_PORT);
                inputStream = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                writer = new PrintWriter(socket.getOutputStream(), true);
                while(true){
                    msg=reader.readLine();
                    if(msg==null||isCancelled()) break;
                    Log.d(LOG_TAG,"Reading msg: "+ msg);
                    publishProgress(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(LOG_TAG,"Progress update");
        mActivity.handleIncoming(values[0]);
    }

    public Game(WiFiDirectActivity activity){
        this.context=activity.getApplicationContext();
        isServer=true;
        Log.d(LOG_TAG, "Game created on server side");
        localPlayer = new LanPlayer("Server",-1);
    }

    public Game(WiFiDirectActivity activity,InetAddress address){
        this.context=activity.getApplicationContext();
        this.serverAddress=address;
        isServer=false;
        Log.d(LOG_TAG,"Game created on client side");
        localPlayer = new LanPlayer("Client",1);
    }

    public void sendMsg(String msg) {
        message = msg;
        new Thread(new Sender(msg,writer)).run();
    }
    public void run() {
        writer.println(message);
    }
}
