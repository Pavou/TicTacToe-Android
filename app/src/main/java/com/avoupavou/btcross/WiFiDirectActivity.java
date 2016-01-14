package com.avoupavou.btcross;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class WiFiDirectActivity extends Activity implements WifiP2pManager.ChannelListener{


    private static final String TAG="WiFiDirectActivity";
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public WiFiDirectReceiver mReceiver;
    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesAddress;
    private ArrayList<WifiP2pDevice> devicesList;
    private ArrayAdapter<String> deviceNamesAdapter;
    Context c;

    private static FloatingActionButton fab;

    //game logic
    private final int xTurn=1;
    private final int oTurn=-1;
    private LanCanvasView canvas;
    private LanPlayer localPlayer;
    private static LanBoard board;
    private WiFiDirectActivity thisActivity;
    private LanPlayer p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wi_fi_direct);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        c= this.getApplicationContext();
        devicesNames = new ArrayList<>();
        devicesAddress = new ArrayList<>();


        deviceNamesAdapter = new ArrayAdapter<String>(this,R.layout.list_item_device,R.id.list_item_device_textview,devicesNames);
        final ListView deviceListView = (ListView)findViewById(R.id.device_listview);
        deviceListView.setAdapter(deviceNamesAdapter);

        //on list item click
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int possition, long id) {
                if(devicesList!=null) {
                    String address = devicesAddress.get(possition);
                    String name = devicesNames.get(possition);
                    Toast.makeText(c, "Connecting to " + name, Toast.LENGTH_SHORT).show();
                    connectToPeer(devicesList.get(possition));
                }
            }
        });

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel= mManager.initialize(this, getMainLooper(),this);


        canvas = (LanCanvasView) findViewById(R.id.lan_canvas);

        fab = (FloatingActionButton) findViewById(R.id.lan_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board = new LanBoard(localPlayer, p2);
                canvas.setBoard(board);
                board.setActivity(thisActivity);
                canvas.clearCanvas();
                fab.setVisibility(View.GONE);
            }
        });
        fab.setVisibility(View.GONE);
        thisActivity=this;
    }


    public LanPlayer getLocalPlayer() {
        return localPlayer;
    }
    public LanPlayer getP2() {
        return p2;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mManager.removeGroup(mChannel, new ActionListenerHandler(this, "Group removal"));
        unregisterWifiReceiver();
        registerWifiReceiver();
        mManager.discoverPeers(mChannel, new ActionListenerHandler(this, "Discover peers"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendMsg(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mManager.stopPeerDiscovery(mChannel, new ActionListenerHandler(this, "Group removal"));
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
            deviceNamesAdapter.clear();
            // update the names of available devices
            calculateDevices();
            //notify adapter to change listView content
            deviceNamesAdapter.notifyDataSetChanged();
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

    static public void handleIncoming(String msg){
        int x,y;
        Log.d(TAG,"Incoming "+msg);
        x = Character.getNumericValue(msg.charAt(0));
        y = Character.getNumericValue(msg.charAt(1));
        Log.d(TAG,"interpretered as x = "+x+" y= "+y);
        board.incomingMove(x, y);
    }

    public void sendMsg(String msg){
        mReceiver.sendMsg(msg);
        Log.d(TAG, "sending move");
    }

    public void setPlayers() {
        p2 = new LanPlayer("Gay",-1);
        localPlayer = new LanPlayer(mReceiver.getThisDevice().deviceName,1);
    }

}
