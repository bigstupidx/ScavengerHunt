package com.yan.sh.sh_android.engine.managers;

import android.content.Context;
import android.os.SystemClock;

import com.yan.sh.sh_android.util.SntpClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import timber.log.Timber;

/**
 * Created by yan on 10/13/17.
 */

public class TimeManager extends Manager {
    private static final ScheduledExecutorService mTimeQueue = Executors.newSingleThreadScheduledExecutor();
    private Context mContext;
    private Double mOffset;

    public TimeManager(Context context){
        this.startup();
        mContext = context;
        runTimeCheck();
    }

    public double now(){
        return nowMillis()/1000.0;
    }

    public double nowMillis(){
        return (System.currentTimeMillis() + mOffset);
    }

    //Find the offset from the current systems time and the NTP time
    public void runTimeCheck(){
        mTimeQueue.execute(new Runnable() {
            @Override
            public void run() {
                String [] pools = {"0.pool.ntp.org",
                        "0.uk.pool.ntp.org",
                        "0.us.pool.ntp.org",
                        "asia.pool.ntp.org",
                        "europe.pool.ntp.org",
                        "north-america.pool.ntp.org",
                        "south-america.pool.ntp.org",
                        "oceania.pool.ntp.org",
                        "africa.pool.ntp.org"};
                int numRes = 0;
                double avg = 0;

                for(String p : pools) {
                    SntpClient client = new SntpClient();

                    if (client.requestTime(p, 2000)) {
                        long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
                        long oldNow = System.currentTimeMillis();
                        long offset = now - oldNow;

                        avg = (avg*numRes + offset) / (numRes + 1);
                        numRes++;
                    }
                }

                mOffset = avg;
                Timber.i("Offset found : " + mOffset.toString());
            }
        });
    }

}
