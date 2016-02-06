package com.avoupavou.btcross;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class WiFiDirectActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {


    private static final String TAG="WiFiDirectActivity";
    private final int xTurn=1;
    private final int oTurn=-1;

    //Wifi related variables
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public WiFiDirectReceiver mReceiver;
    //Arrays with devices'
    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesAddress;
    private ArrayList<WifiP2pDevice> devicesList;

    Context c;

    private static FloatingActionButton fab;

    //Canvas and board
    private static LanCanvasView canvas;
    private static LanBoard board;
    //Players
    private static LanPlayer localPlayer;
    private static LanPlayer p2;
    //this activity for reference
    private static WiFiDirectActivity thisActivity;
    //ListView adapter
    private AdapterImageView hybridAdapter;
    //
    private String opponentsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_direct);
        //lock orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //set context
        c= this.getApplicationContext();
        //initialize
        devicesNames = new ArrayList<>();
        devicesAddress = new ArrayList<>();
        hybridAdapter = new AdapterImageView(this,R.layout.list_item_hybrid,R.id.list_item_device_textview,R.id.list_item_avatar_imageView,devicesNames);

        //find listView and set adapter
        ListView deviceListView = (ListView)findViewById(R.id.list_view);
        deviceListView.setAdapter(hybridAdapter);
        //get manager service and initialize channel
        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel= mManager.initialize(this, getMainLooper(),this);
        //get canvas
        canvas = (LanCanvasView) findViewById(R.id.lan_canvas);

        //on list item click
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int possition, long id) {
                if (devicesList != null) {
                    String address = devicesAddress.get(possition);
                    String name = devicesNames.get(possition);
                    Toast.makeText(c, "Connecting to " + name, Toast.LENGTH_SHORT).show();
                    connectToPeer(devicesList.get(possition));
                }
            }
        });

        //replay fab
        fab = (FloatingActionButton) findViewById(R.id.lan_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCanvasBoard();
                sendMsg("RESET");
            }
        });
        //default not visible
        fab.setVisibility(View.GONE);
        //help fab
        findViewById(R.id.helpFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start help activity
                //startActivity(new Intent());
                DialogFragment newFragment = new PopUpHelp();
                newFragment.show(thisActivity.getSupportFragmentManager(), "missiles");
            }
        });
        //keep activity for reference
        thisActivity=this;
        opponentsName="Opponent";
        }

    private static void resetCanvasBoard(){
        board = new LanBoard(localPlayer, p2);
        canvas.setBoard(board);
        board.setActivity(thisActivity);
        canvas.clearCanvas();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        registerWifiReceiver();
        mManager.discoverPeers(mChannel, new ActionListenerHandler(this, "Discover peers"));

        findViewById(R.id.helpFAB).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendMsg("LEFT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mManager.stopPeerDiscovery(mChannel, new ActionListenerHandler(this, "Stop Discovery"));
        }
        mManager.removeGroup(mChannel, new ActionListenerHandler(this, "Group removal"));
        mManager.cancelConnect(mChannel, new ActionListenerHandler(this, "Canceling connect"));
        unregisterWifiReceiver();
        mManager=null;
        Log.d(TAG, "WifiDirectActivity stopped");
    }

    @Override
    public void onChannelDisconnected() {
        Log.d(TAG, "WIFI Direct Disconnected, reinitializing");
        reinitialize();
    }

    public void reinitialize(){
        mChannel=mManager.initialize(this, getMainLooper(), this);
        if(mChannel!=null){
            Log.d(TAG,"WIFI Direct reinitialize : SUCCESS");
        }else{
            Log.d(TAG, "WIFI Direct reinitialize : FAILURE");
        }
    }

    public void peerAvailable(){
        devicesList =  mReceiver.getDeviceList();
        if(devicesList!=null) {
            //clear adapter from outdated data
            hybridAdapter.clear();
            // update the names of available devices
            calculateDevices();
            //notify adapter to change listView content
            hybridAdapter.notifyDataSetChanged();
        }
    }

    private void registerWifiReceiver() {
        mReceiver = new WiFiDirectReceiver(mManager,mChannel,this);
        mReceiver.registerReceiver();
    }

    private void unregisterWifiReceiver() {

        if(mReceiver!=null) {
            mReceiver.unregisterReceiver();
        }
        mReceiver=null;
    }

    public void connectToPeer(WifiP2pDevice device){
        if(device!=null){
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            opponentsName=device.deviceName;
            config.wps.setup= WpsInfo.PBC;
            mManager.connect(mChannel, config, new ActionListenerHandler(this, "Connection to peer"));
        }else{
            Log.d(TAG, "Can not find that device");
        }
    }

    private void calculateDevices(){
        if(devicesList!=null) {
            if(devicesNames!=null) devicesNames.clear();
            if(devicesAddress!=null) devicesAddress.clear();
            for (int i=0;i<devicesList.size();i++) {
                devicesNames.add(devicesList.get(i).deviceName);
                devicesAddress.add(devicesList.get(i).deviceAddress);
            }
        }
    }

    public void MoveToCanvas(){
        findViewById(R.id.helpFAB).setVisibility(View.GONE);
        ViewFlipper vf = (ViewFlipper) findViewById( R.id.fliper );
        vf.showNext();
        board = new LanBoard(localPlayer,p2);
        canvas.setBoard(board);
        board.setActivity(this);
    }

    public void showResetButton(String gameStatus) {
        showWinner(gameStatus);
        fab.setVisibility(View.VISIBLE);
    }

    public void showWinner(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void handleIncoming(String msg){
        int x,y;
        Log.d(TAG, "Incoming " + msg);
        if(msg.equals("RESET")){
            resetCanvasBoard();
        }else if (msg.equals("LEFT")) {
            onBackPressed();
        }
        else{
            x = Character.getNumericValue(msg.charAt(0));
            y = Character.getNumericValue(msg.charAt(1));
            Log.d(TAG, "interpretered as x = " + x + " y= " + y);
            board.incomingMove(x, y);
        }
    }

    public void sendMsg(String msg){
        mReceiver.sendMsg(msg);
        Log.d(TAG, "Sending move");
    }

    public void setPlayers() {
        p2 = new LanPlayer(opponentsName,-1);
        localPlayer = new LanPlayer(mReceiver.getThisDevice().deviceName,1);
    }
    public LanPlayer getLocalPlayer() {
        return localPlayer;
    }
    public LanPlayer getP2() {
        return p2;
    }

}
