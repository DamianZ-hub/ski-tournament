package com.ski.tournament.core;

import java.util.HashMap;

public class ScoreTable extends HashMap<Integer,Integer> {

    private static ScoreTable INSTANCE;
    private String info = "Tabelka punktowa miejsce-punkty";

    private ScoreTable() {
        put(1,50);
        put(2,46);
        put(3,43);
        put(4,40);
        put(5,37);
        put(6,34);
        put(7,32);
        put(8,30);
        put(9,28);
        put(10,26);
        put(11,24);
        put(12,22);
        put(13,20);
        put(14,18);
        put(15,16);
        put(16,15);
        put(17,14);
        put(18,13);
        put(19,12);
        put(20,11);
        put(21,10);
        put(22,9);
        put(23,8);
        put(24,7);
        put(25,6);
        put(26,5);
        put(27,4);
        put(28,3);
        put(29,2);
        put(30,1);

    }
    @Override
    public Integer get(Object key) {
        if( (Integer) key > 30) return 0;
        return super.get(key);
    }

    public static ScoreTable getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ScoreTable();
        }

        return INSTANCE;
    }

}
