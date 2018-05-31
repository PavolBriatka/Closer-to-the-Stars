package com.example.pavol.closertostars;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by pavol on 26/11/2017.
 */

public class VideoActivity extends YouTubeBaseActivity {
    YouTubePlayerView ytPlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.video_layout);
        ytPlayerView = (YouTubePlayerView)findViewById(R.id.youtube_view);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                String videoSrc = DetailActivity.getVideoKey();
                youTubePlayer.loadVideo(videoSrc);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        ytPlayerView.initialize(PlayerConfig.API_KEY, onInitializedListener);
    }

}
