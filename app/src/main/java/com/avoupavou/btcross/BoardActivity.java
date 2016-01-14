package com.avoupavou.btcross;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BoardActivity extends Activity{
    private final String LOG_TAG="BoardActivity";

    private final int xTurn=1;
    private final int oTurn=-1;

    private CanvasView canvas;
    private Board board;
    private Player p1,p2;
    private BoardActivity thisActivity;


    private static FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        thisActivity=this;
        Intent gameIntent = getIntent();
        canvas = (CanvasView) findViewById(R.id.my_canvas);
        p1 = new Player(gameIntent.getStringExtra("P1"), xTurn);
        p2 = new Player(gameIntent.getStringExtra("P2"), oTurn);
        board = new Board(p1, p2);
        canvas.setBoard(board);
        board.setActivity(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board = new Board(p1, p2);
                canvas.setBoard(board);
                board.setActivity(thisActivity);
                canvas.clearCanvas();
                fab.setVisibility(View.GONE);
            }
        });
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        canvas.invalidate();
        HideSystemUI();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        HideSystemUI();
    }


    public void HideSystemUI(){
        int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public void showResetButton(String msg){
        showWinner(msg);
        fab.setVisibility(View.VISIBLE);
    }

    public void showWinner(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
