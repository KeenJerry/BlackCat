package com.android.keenjackdaw.blackcat.utils;

public abstract class BlackCatRunnable implements Runnable {
    private boolean isRunning;
    private long currentTime;

    protected abstract void blackCatRun();

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

    protected void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    protected long getCurrentTime(){
        return currentTime;
    }
}
