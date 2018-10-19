package com.android.keenjackdaw.blackcat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.lang.ref.WeakReference;

public class BlackCatApplication extends Application {
    private static WeakReference<Activity> currentActivity;

    private static BlackCatApplication app;

    public static BlackCatApplication getInstance() {
        return app;
    }


    @Override
    public void onCreate () {
        super.onCreate();
        app = this;

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated (Activity activity, Bundle bundle) {
                //do nothing
            }

            @Override
            public void onActivityStarted (Activity activity) {
                //do nothing
            }

            @Override
            public void onActivityResumed (Activity activity) {
                currentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused (Activity activity) {
                //do nothing
            }

            @Override
            public void onActivityStopped (Activity activity) {
                //do nothing
            }

            @Override
            public void onActivitySaveInstanceState (Activity activity, Bundle bundle) {
                //do nothing
            }

            @Override
            public void onActivityDestroyed (Activity activity) {
                //do nothing
            }
        });
    }

    public static WeakReference<Activity> getCurrentActivity() {
        return currentActivity;
    }
}
