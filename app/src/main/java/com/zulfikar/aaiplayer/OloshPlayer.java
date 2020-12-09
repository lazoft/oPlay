package com.zulfikar.aaiplayer;

import android.content.Context;
import android.os.Looper;

import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Clock;

public class OloshPlayer extends SimpleExoPlayer {
    public OloshPlayer(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, AnalyticsCollector analyticsCollector, Clock clock, Looper looper) {
        super(context, renderersFactory, trackSelector, loadControl, bandwidthMeter, analyticsCollector, clock, looper);
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        super.setPlayWhenReady(playWhenReady);

    }


}
