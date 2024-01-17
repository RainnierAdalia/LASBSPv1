package com.example.quizgeneration;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class SanPabloIntroActivity extends AppCompatActivity {

    private PlayerView playerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pablo_intro);

        playerView = findViewById(R.id.playerView);

        // Set the video URL
        String videoUrl = "https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FSPCIntroJan8.mp4?alt=media&token=10f50bbf-9b27-40f9-8d30-8cee6428f24d";

        // Initialize ExoPlayer
        initializePlayer(videoUrl);
    }

    private void initializePlayer(String videoUrl) {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Prepare the MediaSource
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        MediaSource mediaSource = buildMediaSource(mediaItem);
        player.setMediaSource(mediaSource);

        // Auto start playing when ready
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(MediaItem mediaItem) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this);
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
