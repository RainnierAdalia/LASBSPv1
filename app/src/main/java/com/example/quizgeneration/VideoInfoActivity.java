package com.example.quizgeneration;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoInfoActivity extends AppCompatActivity {

    private PlayerView playerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);

        // Retrieve data passed from the previous activity
        String markerTitle = getIntent().getStringExtra("markerTitle");
        String videoUrl = getIntent().getStringExtra("videoUrl");

        // Check if videoUrl is not null
        if (videoUrl != null) {
            // Set up the ExoPlayer and UI components
            initializeExoPlayer(videoUrl);

            // Set up the information text
            TextView infoTextView = findViewById(R.id.infoWindowText);
            String infoText = getInfoTextForMarkerTitle(markerTitle);
            infoTextView.setText(infoText);
        } else {
            // Handle the case where videoUrl is null
            // You might want to show an error message or take appropriate action
            // For now, you can finish the activity
            finish();
        }
    }

    private void initializeExoPlayer(String videoUrl) {
        playerView = findViewById(R.id.playerView);
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Prepare the MediaItem
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);

        // Auto start playing when ready
        player.setPlayWhenReady(true);

        // Prepare the player
        player.prepare();
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


    private String getInfoTextForMarkerTitle(String markerTitle) {
        // Implement logic to map marker titles to corresponding information text
        // For simplicity, you can use a switch statement or a map
        switch (markerTitle) {
            case "Sampalok Lake":
                return getString(R.string.sampalok);

            case "Yambo Lake":

            case "Pandin Lake":
                return getString(R.string.info_pandin_and_yambo_lake);

            case "PRUDENCIA D. FULE MEMORIAL NATIONAL HIGH SCHOOL":
                return getString(R.string.prudencia);

            case "Fule Malvar Ancestral Mansion":
                return getString(R.string.white_house);

            case "Andres Bonifacio Monument, San Pablo City":
                return getString(R.string.bonifacio);

            case "Kalibato Lake":
                return getString(R.string.calibato_lake); // Add more cases as needed for each marker

            case "Cathedral Parish of Saint Paul the First Hermit":
                return getString(R.string.church);

            case "Mangga tree, San Pablo City":
                return getString(R.string.mangga);

            case "Library Hub, San Pablo City":
                return getString(R.string.libraryhub);

            case "Bunot Lake":
                return getString(R.string.bunot);

            case "Mojicap Lake":
                return getString(R.string.muhikap_lake);

            case "Palakpakin Lake":
                return getString(R.string.palakpakin);

            default:
                return "";
        }
    }
}
