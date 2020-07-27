package com.zongzhaopeng.guessmusic.model;

import android.widget.Button;

/**
 * Created by zhaopengzong on 2017/1/8.
 */
public class WordButton
{
    // index
    public int mIndex;
    // show or hide
    public boolean mIsvisiable;
    // string
    public String mWordString;
    // button
    public Button mViewButton;

    public WordButton()
    {
        mIsvisiable = true;
        mWordString = "";
    }
}
