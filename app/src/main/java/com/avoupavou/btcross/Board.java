package com.avoupavou.btcross;

import android.graphics.Path;

import static com.avoupavou.btcross.Board.WinPosition.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Pantazis on 11/16/2015.
 */

public class Board {
    public enum WinPosition {
        line1,line2,line3,
        row1,row2,row3,
        crossMain,crossSecond,tie
    }



    private final int xTurn=1;
    private final int oTurn=-1;
    WinPosition winPosition;

    //board
    private int[][] board;
    //turn 1=x -1=O 0=null
    private int turn;
    //path to be paint for the grid and the move
    private Path gridPath;
    private Path movePathP1;
    private Path movePathP2;
    private Path strikeThrough;
    //canvas dimensions
    private int height;
    private int width;
    private Player p1;
    private Player p2;
    private Player localPlayer;
    private Player winner;
    private boolean GameOver;

    private BoardActivity mActivity;



    public Board(Player p1,Player p2){
        gridPath = new Path();
        movePathP1 = new Path();
        movePathP2 = new Path();
        strikeThrough = new Path();
        winPosition = tie;
        board = new int[3][3];
        this.p1=p1;
        this.p2=p2;
        this.localPlayer=p1;
        turn=xTurn;
        GameOver=false;
        winner=null;

        //initialize board
        for(int i=0;i<3;i++) {
            for (int j = 0; j < 3; j++)
                board[i][j] = 0;
        }
    }

   private void drawX(float x,float y,Player p){
        float dimX=(width/3)*(float)0.90;
        float dimY=(height/3)*(float)0.60;
        x=x+(width/3-dimX)/2;
        y=y+(height/3-dimY)/2;
       if(p==p1) {
           movePathP1.moveTo(x, y);
           movePathP1.lineTo(x + dimX, y + dimY);
           movePathP1.moveTo(x, y + dimY);
           movePathP1.lineTo(x + dimX, y);
       }else if(p==p2){
           movePathP2.lineTo(x + dimX, y + dimY);
           movePathP2.moveTo(x, y + dimY);
           movePathP2.lineTo(x + dimX, y);
       }
   }

   private void drawO(float x,float y,Player p ){
        float dimX=width/3;
        float dimY=height/3;
        float r=dimX<=dimY?(dimX*(float)0.95)/2:(dimY*(float)0.95)/2;
        float xc,yc;
        float centerx=x+width/6;
        float centery=y+height/6;
       if(p==p1) {
           movePathP1.moveTo(r * (float) cos((float) 1 / 100) + centerx, centery);
           for (int i = 0; i < 720; i++) {
               xc = r * (float) cos((float) i / 100) + centerx;
               yc = r * (float) sin((float) i / 100) + centery;
               movePathP1.lineTo(xc, yc);
           }
       }else if(p==p2){
           movePathP2.moveTo(r * (float) cos((float) 1 / 100) + centerx, centery);
           for (int i = 0; i < 720; i++) {
               xc = r * (float) cos((float) i / 100) + centerx;
               yc = r * (float) sin((float) i / 100) + centery;
               movePathP2.lineTo(xc, yc);
           }
       }


    }
    public void drawMove(int x,int y,Player p){
        if(x<=2&&x>=0&&y<=2&&y>=0) {
            if (board[x][y] == 0) {
                board[x][y] = turn;
                if(isGameWon(x,y,turn)||isGameTie()){
                    gameOver();
                }
                if (turn == oTurn) {
                    drawO(x * (width / 3), y * (height / 3),p);
                    turn = xTurn;
                } else {
                    drawX(x * (width / 3), y * (height / 3),p);
                    turn = oTurn;
                }
            }
       }
    }

   public boolean isGameWon(int lastX, int lastY,int trn){
       if(board[lastX][0]==trn&&board[lastX][1]==trn&&board[lastX][2]==trn) {
           winner=p1.isMyTurn(trn)?p1:p2;
           if(lastX==0){
               this.winPosition = row1;
           }else if(lastX==1){
               this.winPosition = row2;
           }else{
               this.winPosition = row3;
           }
           return true;
       }
       if(board[0][lastY]==trn&&board[1][lastY]==trn&&board[2][lastY]==trn) {
           winner=p1.isMyTurn(trn)?p1:p2;
           if(lastY==0){
               this.winPosition = line1;
           }else if(lastY==1){
               this.winPosition = line2;
           }else{
               this.winPosition = line3;
           }
           return true;
       }
       if(lastX==lastY){
           if(board[0][0]==trn&&board[1][1]==trn&&board[2][2]==trn) {
               winner=p1.isMyTurn(trn)?p1:p2;
               this.winPosition = crossMain;
               return true;
           }
       }
       if(lastX+lastY==2){
           if(board[0][2]==trn&&board[1][1]==trn&&board[2][0]==trn) {
               winner=p1.isMyTurn(trn)?p1:p2;
               this.winPosition = crossSecond;
               return true;
           }
       }
        return false;
   }

    public boolean isGameTie(){
        for(int i =0 ;i<3;i++){
            for(int j =0 ;j<3;j++){
                if(board[i][j]==0){
                    return false;
                }
            }
        }
        return true;
    }

    public  boolean isGameOver(){
        return GameOver;
    }

   public void drawBoard(){
        gridPath.reset();
        gridPath.moveTo(width / 3, 0);
        gridPath.lineTo(width / 3, height);
        gridPath.moveTo(2 * width / 3, 0);
        gridPath.lineTo(2 * width / 3, height);
        gridPath.moveTo(0, height / 3);
        gridPath.lineTo(width, height / 3);
        gridPath.moveTo(0, 2 * height / 3);
        gridPath.lineTo(width, 2 * height / 3);
   }

    public void drawStrikeThrough(){
        strikeThrough.reset();
        switch (this.winPosition) {
            case line1:
                strikeThrough.moveTo(0,height/6);
                strikeThrough.lineTo(width,height/6);
                break;

            case line2:
                strikeThrough.moveTo(0 , height/2);
                strikeThrough.lineTo(width , height/2);
                break;

            case line3:
                strikeThrough.moveTo(0 , 5*height/6);
                strikeThrough.lineTo(width , 5*height/6);
                break;

            case row1:
                strikeThrough.moveTo(width/6 , 0);
                strikeThrough.lineTo(width/6 , height);
                break;

            case row2:
                strikeThrough.moveTo(width/2 , 0);
                strikeThrough.lineTo(width/2 , height);
                System.out.println("Row 2");
                break;

            case row3:
                strikeThrough.moveTo(5*width/6 , 0);
                strikeThrough.lineTo(5*width/6 , height);
                break;

            case crossMain:
                strikeThrough.moveTo(0 , 0);
                strikeThrough.lineTo(width , height);
                break;

            case crossSecond:
                strikeThrough.moveTo(width, 0);
                strikeThrough.lineTo(0 , height);
                break;
            default:
        }
    }
    public Path getGridPath(){
        drawBoard();
        return gridPath;
    }
    public Path getMovePathP1(){
        return movePathP1;
    }
    public Path getMovePathP2(){
        return movePathP2;
    }
    public Path getStrikeThrough(){
        return strikeThrough;
    }




    public String getGameStatus(){
        if(isGameOver()){
            if(winner!=null)
                return winner.getName()+" winns!";
            else{
                return "Game is a tie!";
            }
        }
        else{
            return "No winner yet";
        }
    }

    public void clickOnCanvas(int x,int y) {
        if (!isGameOver()) {
            if (localPlayer.isMyTurn(turn)) {
                localPlayer.setMove((int)(x/(width/3)), (int)(y/(height / 3)));
                drawMove(localPlayer.getMove()[0],localPlayer.getMove()[1],localPlayer);
            } else {
                //debug only
                p2.setMove((int)(x/(width/3)), (int)(y/(height / 3)));
                drawMove(p2.getMove()[0],p2 .getMove()[1],p2);
            }
        }
    }

    public void setSize(int width,int height){
        this.width=width;
        this.height=height;
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    public void gameOver() {
        if (!isGameOver()) {
            GameOver = true;
            drawStrikeThrough();
            mActivity.showResetButton(getGameStatus());
        }
    }

    public void setActivity(BoardActivity m){
        mActivity=m;
    }
}
