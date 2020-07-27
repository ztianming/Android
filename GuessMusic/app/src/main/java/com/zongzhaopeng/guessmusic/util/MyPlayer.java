package com.zongzhaopeng.guessmusic.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * music player
 * Created by zhaopengzong on 2017/2/18.
 */
public class MyPlayer
{
    // play song
    private static MediaPlayer mMusicMediaPlayer;

    // play song
    public static void playSong(Context context, String fileName)
    {
        if (mMusicMediaPlayer == null)
        {
            mMusicMediaPlayer = new MediaPlayer();
        }

        // enforce reset
        mMusicMediaPlayer.reset();

        // load music file
        AssetManager assetManager = context.getAssets();
        //MyLog.d("Myplayer","load");
        try
        {
            //MyLog.d("Myplayer",fileName);
            AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
            // set data source
            //MyLog.d("Myplayer","set");
            mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());

            //MyLog.d("Myplayer","prepare");
            mMusicMediaPlayer.prepare();
            // play
            mMusicMediaPlayer.start();
            //MyLog.d("Myplayer", "play ");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // stop song
    public static void stopSong(Context context)
    {
        if (mMusicMediaPlayer != null)
        {
            mMusicMediaPlayer.stop();
        }
    }

    // sound effect file name
    private final static String[] SOUND_NAMES =
            { "enter.mp3", "cancel.mp3", "coin.mp3" };
    // sound effect
    private static MediaPlayer[] mSoundMediaPlayer = new MediaPlayer[SOUND_NAMES.length];

    // sound index
    public final static int INDEX_SOUND_ENTER  = 0;
    public final static int INDEX_SOUND_CANCEL = 1;
    public final static int INDEX_SOUND_COIN   = 2;

    // play sound , when click button
    public static void playSound(Context context, int index)
    {
        // load sound
        AssetManager assetManager = context.getAssets();

        if (mSoundMediaPlayer[index] == null)
        {
            mSoundMediaPlayer[index] = new MediaPlayer();
            try
            {
                AssetFileDescriptor fileDescriptor = assetManager.openFd(SOUND_NAMES[index]);
                mSoundMediaPlayer[index].setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());

                mSoundMediaPlayer[index].prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // play
        mSoundMediaPlayer[index].start();
        MyLog.d("myPlaySound",index+"");
    }


}
