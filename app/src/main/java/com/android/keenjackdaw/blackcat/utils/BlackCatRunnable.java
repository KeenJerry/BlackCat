package com.android.keenjackdaw.blackcat.utils;

public abstract class BlackCatRunnable implements Runnable {
    private boolean isRunning;
    private long currentTime;

    protected abstract void blackCatRun();

    protected abstract void draw();
    protected abstract void detect();
    @Override
    public void run(){
        blackCatRun();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getCurrentTime(){
        return currentTime;
    }
}
