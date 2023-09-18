package com.example.plug_in_tester;


import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.mediarouter.media.MediaRouter;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

public class HelloWorld extends ContentProvider {
    public static String sayHello() {
        return "Hello World From JNI";
    }

    public String sayHi() {
        return "Hi World From back";
    }


    public static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.AppGlobals")
                .getMethod("getInitialApplication").invoke(null, (Object[]) null);
    }

    public String launch() throws Exception {
        MediaRouter.RouteInfo routeInfo = null;
//        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        ComponentName componentInfo = taskInfo.get(0).topActivity;
//        Log.d(WebServiceHelper.TAG, "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName()+"   Package Name :  "+componentInfo.getPackageName());


        Application app = getApplicationUsingReflection();
        if (app.getApplicationContext() == null) {
            return "banana context missing";
        }
        List<MediaRouter.RouteInfo> routeList;
        MediaRouter mMediaRouter = MediaRouter.getInstance(app.getApplicationContext());
        try {
            routeList = mMediaRouter.getRoutes();
        } catch (Exception e) {
            return "listing routes failed";
        }

        for (int n = 1; n < routeList.size(); n++) {
            MediaRouter.RouteInfo route = routeList.get(n);
            if (!route.getName().equals("Phone") && route.getId().contains("Cast")) {
                routeInfo = route;
                //seq[n-1] = route.getName();
            }
        }
        if (routeInfo != null) {
            return "route was null";
        }
        CastDevice device = CastDevice.getFromBundle(new Bundle());
        if (device != null) {
            return "device is null";
        }
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(device, new Cast.Listener());
        try {
            GoogleApiClient mApiClient = new GoogleApiClient.Builder(app.getApplicationContext())
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .build();

            launchApplication(mApiClient);
        } catch (Exception e) {
            return e.toString();
        }
        return "success";

    }

    private void launchApplication(GoogleApiClient mApiClient) {
        Cast.CastApi.launchApplication(mApiClient, "", false);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
