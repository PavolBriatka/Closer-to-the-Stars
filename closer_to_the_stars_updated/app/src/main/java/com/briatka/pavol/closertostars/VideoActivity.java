package com.briatka.pavol.closertostars;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pavol on 26/11/2017.
 */

public class VideoActivity extends YouTubeBaseActivity {
    @BindView(R.id.youtube_view)
    YouTubePlayerView ytPlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.video_layout);
        ButterKnife.bind(this);
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
