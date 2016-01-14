package com.avoupavou.btcross;

/**
 * Created by Pantazis on 11/16/2015.
 */
public class Player {
    private final int xSybol=1;
    private final int oSymbol=-1;
    private String name;
    private int score;
    private int side;
    private int[] move;

    public Player(){
        name="";
        score=0;
        side=0;
        move=null;
    }

    public Player(String name,int side){
        this.name=name;
        score=0;
        this.side=side;
        move = new int[2];
    }

    public String getName(){
        return name;
    }

    public int getSide(){
        return side;
    }

    public void setSide(int side){
        this.side=side;
    }

    public boolean isMyTurn(int turn){
        return side==turn? true : false;
    }

    public void setMove(int x,int y){
        move[0]=x;
        move[1]=y;
    }

    public int[] getMove(){
        return move;
    }
 }
