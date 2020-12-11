package com.zulfikar.aaiplayer;

import androidx.annotation.Nullable;

public class Pair<FIRST extends Comparable<FIRST>, SECOND extends Comparable<SECOND>> implements Comparable<Pair<FIRST, SECOND>> {

    public static final int KEY_FIRST = 1;
    public static final int KEY_SECOND = 2;

    private FIRST first;
    private SECOND second;

    private int key;

    public Pair(FIRST first, SECOND second, int key) {
        this.first = first;
        this.second = second;
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Pair) {
            return compareTo((Pair<FIRST, SECOND>) obj) == 0;
        }
        return false;
    }

    @Override
    public int compareTo(Pair<FIRST, SECOND> o) {
        if (key == KEY_FIRST) return first.compareTo(o.first);
        return second.compareTo(o.second);
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public void setFirst(FIRST first) {
        this.first = first;
    }

    public void setSecond(SECOND second) {
        this.second = second;
    }
}
