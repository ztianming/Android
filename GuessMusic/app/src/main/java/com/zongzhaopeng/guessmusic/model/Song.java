package com.zongzhaopeng.guessmusic.model;

/**
 * Created by zhaopengzong on 2017/1/8.
 */
public class Song
{
    // song name
    private String mSongName;
    // file name
    private String mSongFileName;
    // length of song name
    private int mNameLength;

    // single word array
    public char[] getNameCharacters()
    {
        return mSongName.toCharArray();
    }
    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String SongName)
    {
        this.mSongName = SongName;
        this.mNameLength = SongName.length();
    }

    public String getSongFileName() {
        return mSongFileName;
    }

    public void setSongFileName(String SongFileName) {
        this.mSongFileName = SongFileName;
    }

    public int getNameLength() {
        return mNameLength;
    }

    public void setNameLength(int NameLength) {
        this.mNameLength = NameLength;
    }
}
