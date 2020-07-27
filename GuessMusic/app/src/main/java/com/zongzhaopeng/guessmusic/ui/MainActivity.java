package com.zongzhaopeng.guessmusic.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zongzhaopeng.guessmusic.R;
import com.zongzhaopeng.guessmusic.data.Constant;
import com.zongzhaopeng.guessmusic.model.IAlertDialogButtonListener;
import com.zongzhaopeng.guessmusic.model.IWordButtonClickListener;
import com.zongzhaopeng.guessmusic.model.Song;
import com.zongzhaopeng.guessmusic.model.WordButton;
import com.zongzhaopeng.guessmusic.myui.MyGridView;
import com.zongzhaopeng.guessmusic.util.MyLog;
import com.zongzhaopeng.guessmusic.util.MyPlayer;
import com.zongzhaopeng.guessmusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements IWordButtonClickListener
{
    // disk controller
    private ImageView mViewPan;
    // driving lever
    private ImageView mViewPanBar;

    // disk animation
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;

    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;


    // Play
    private ImageButton mBtnPlayStart;

    // animation is running
    private boolean mIsRunning = false;

    // word container
    private ArrayList<WordButton> mAllWords;
    // word selected
    private ArrayList<WordButton> mWordsSelected;

    private MyGridView mMyGridView;

    // word selected container
    private LinearLayout mViewWordsContainer;

    // current song
    private Song mCurrentSong;
    // current stage index
    private int mCurrentStageIndex = -1;

    // set TAG
    public final static String TAG = "MainActivity";

    /******************************
     * set answer status
     ******************************/
    // right
    public final static int STATUS_ANSWER_RIGHT = 1;
    // wrong
    public final static int STATUS_ANSWER_WRONG = 2;
    // incomplete
    public final static int STATUS_ANSWER_LACK = 3;

    // word blink time
    public final static int BLINK_TIMES = 6;

    // next level view
    private View mPassView;

    // current coin number
    private int mCurrentCoins = Constant.TOTAL_COINS;

    // coin view
    private TextView mViewCurrentCoins;

    // delete button
    private ImageButton mDeleteWord;
    // tip button
    private ImageButton mTipAnswer;

    /*********************************
     * answer right interface
     *********************************/
    // current level index
    private TextView mCurrentLevel;
    // current song name
    private TextView mCurrentSongName;
    private ImageButton ibNext;
    private ImageButton ibShare;
    // current level index , in mainactivity
    private TextView mCurrentStage;

    public Animation getmPanAnim() {
        return mPanAnim;
    }

    /*************************************
     * custom dialog
     ************************************/
    // dialog id
    public final static int ID_DIALOG_DELETE_WORD = 1;
    public final static int ID_DIALOG_TIP_ANSWER = 2;
    public final static int ID_DIALOG_LACK_COINS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        mViewPan = (ImageView) findViewById(R.id.ivDisk);
        mViewPanBar = (ImageView) findViewById(R.id.ivLever);

        // initial animation
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        // set animation
        mPanAnim.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                // 开启拨杆退出动画
                mViewPanBar.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                // start disk animation
                mViewPan.startAnimation(mPanAnim);

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                // 整套动画播放完毕
                Log.i("test", "222222222");
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // play
        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                handlePlayButton();
                //Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_LONG).show();
            }
        });

        // initial word container
        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

        // initial current Stage data
        initCurrentStageData();

        // register listener
        mMyGridView.registerOnWordButtonClick(this);

        // current coin view
        mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");

        // call delete word event
        handleDeleteWord();
        // call tip answer event
        handleTipAnswer();


    }

    // start play music
    private void handlePlayButton()
    {
        if (mViewPanBar != null)
        {
            if (!mIsRunning)
            {
                mIsRunning = true;
                // 开始拨杆进入动画
                mViewPanBar.startAnimation(mBarInAnim);
                mBtnPlayStart.setVisibility(View.INVISIBLE);

                // play music
                MyPlayer.playSong(MainActivity.this,
                        mCurrentSong.getSongFileName());
                //Toast.makeText(this,mCurrentSong.getSongFileName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // exit or pause
    @Override
    public void onPause()
    {
        // stop animation
        mViewPan.clearAnimation();

        // stop music
        MyPlayer.stopSong(MainActivity.this);

        super.onPause();
    }

    // initial current stage data
    private void initCurrentStageData()
    {
        // get current stage song information
        mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

        // get data
        mAllWords = initAllWord();
        // set data
        mMyGridView.updateData(mAllWords);

        // initial word selected
        mWordsSelected = initWordSelected();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140,140);

        // clear the previous level answer
        mViewWordsContainer.removeAllViews();
        // add the new answer
        for (int i=0; i < mWordsSelected.size(); i++)
        {
            mViewWordsContainer.addView(
                    mWordsSelected.get(i).mViewButton,params);
        }

        // show the current level index
        mCurrentStage = (TextView) findViewById(R.id.text_current_stage);
        if (mCurrentStage != null)
        {
            mCurrentStage.setText((mCurrentStageIndex + 1) + "");
        }

        // auto play music
        handlePlayButton();
    }

    // initial all words
    private ArrayList<WordButton> initAllWord()
    {
        // get all words
        String[] words = generateWords();

        ArrayList<WordButton> data = new ArrayList<WordButton>();
        for (int i  =0; i< MyGridView.COUNT_WORDS;i++)
        {
            WordButton button = new WordButton();

            // set words
            button.mWordString = words[i];
            data.add(button);
        }
        return data;
    }

    // initial words which selected
    private ArrayList<WordButton> initWordSelected()
    {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        // set the number of select
        for (int i=0; i <mCurrentSong.getNameLength(); i++)
        {
            View view = Util.getView(MainActivity.this, R.layout.ui_gridview_item);

            final WordButton holder = new WordButton();
            holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setText("");
            holder.mIsvisiable = false;
            holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            holder.mViewButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    clearTheAnswer(holder);
                }
            });

            // add
            data.add(holder);
        }
        return data;
    }

    // implement interface
    @Override
    public void onWordButtonClick(WordButton wordButton)
    {
        // Toast.makeText(this, wordButton.mIndex+" ", Toast.LENGTH_SHORT).show();
        setSelectWord(wordButton);

        // get answer status
        int checkResult = checkTheAnswer();
        //Toast.makeText(this, "test: "+checkResult+" ",Toast.LENGTH_LONG).show();

        // check answer
        if (checkResult == STATUS_ANSWER_RIGHT)
        {
            //Toast.makeText(this, "right",Toast.LENGTH_SHORT).show();
            // next level and get reward
            handlePassEvent();
        }
        else if (checkResult == STATUS_ANSWER_WRONG)
        {
            //Toast.makeText(this, "wrong",Toast.LENGTH_SHORT).show();
            // wrong tip:word blink
            blinkTheWords();
        }
        else if (checkResult == STATUS_ANSWER_LACK)
        {
            //Toast.makeText(this, "lack",Toast.LENGTH_SHORT).show();
            // set the text color : white
            for (int i=0; i < mWordsSelected.size(); i++)
            {
                mWordsSelected.get(i).mViewButton.setTextColor(Color.WHITE);
            }
        }

    }

    // song information
    private Song loadStageSongInfo(int stageIndex)
    {
        Song song = new Song();

        String[] stage = Constant.SONG_INFO[stageIndex];
        song.setSongFileName(stage[Constant.INDEX_FILE_NAME]);
        song.setSongName(stage[Constant.INDEX_SONG_NAME]);

        return song;
    }

    // create random chinese
    private char getRandomChar()
    {
        String str = "";
        int heightPos;
        int lowPos;

        Random random = new Random();

        // height byte : 0xB0~ 0xF7
        heightPos = (176 + Math.abs(random.nextInt(39)));
        // low byte : 0xA1 ~ 0xFE
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(heightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try
        {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return str.charAt(0);
    }

    // create all word
    private String[] generateWords()
    {
        Random random = new Random();

        String[] words = new String[MyGridView.COUNT_WORDS];
        // storage song name
        for (int i=0; i<mCurrentSong.getNameLength();i++)
        {
            words[i] = mCurrentSong.getNameCharacters()[i] + "";
        }
        for (int i = mCurrentSong.getNameLength();
             i<MyGridView.COUNT_WORDS;i++)
        {
            words[i] = getRandomChar() + "";
        }
        // change the order of words
        // random choose one swap with first... cycle

        for (int i = MyGridView.COUNT_WORDS-1; i >=0; i--)
        {
            int index = random.nextInt(i + 1);
            String buf = words[index];
            words[index] = words[i];
            words[i] = buf;
        }
        return words;
    }

    // set select word
    private void setSelectWord(WordButton wordButton)
    {
        for (int i=0; i<mWordsSelected.size();i++)
        {
            if (mWordsSelected.get(i).mWordString.length() == 0)
            {
                // set the answer box context and visible
                mWordsSelected.get(i).mViewButton.setText(wordButton.mWordString);
                mWordsSelected.get(i).mIsvisiable = true;
                mWordsSelected.get(i).mWordString = wordButton.mWordString;
                // record index
                mWordsSelected.get(i).mIndex = wordButton.mIndex;

                // log
                MyLog.d(TAG, mWordsSelected.get(i).mIndex+"");
                // set the selected box
                setButtonVisible(wordButton, View.INVISIBLE);
                break;
            }
        }
    }

    // set the selected box
    private void setButtonVisible(WordButton button, int visibility)
    {
        button.mViewButton.setVisibility(visibility);
        button.mIsvisiable = (visibility == View.VISIBLE)?true:false;

        // log
        MyLog.d(TAG, button.mIsvisiable+"");
    }

    // clear answer
    private void clearTheAnswer(WordButton wordButton)
    {
        wordButton.mViewButton.setText("");
        wordButton.mWordString="";
        wordButton.mIsvisiable = false;

        // set the selected box visible
        setButtonVisible(mAllWords.get(wordButton.mIndex), View.VISIBLE);
    }

    // check answer
    private int checkTheAnswer()
    {
        // check length
        for (int i=0; i < mWordsSelected.size(); i++)
        {
            // null
            if (mWordsSelected.get(i).mWordString.length() == 0)
            {
                return STATUS_ANSWER_LACK;
            }
        }

        // answer is complete, check right or false
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < mWordsSelected.size(); i++)
        {
            sb.append(mWordsSelected.get(i).mWordString);
        }
        return (sb.toString().equals(mCurrentSong.getSongName()))?
                STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
    }

    // word blink
    private void blinkTheWords()
    {
        // timer
        TimerTask task = new TimerTask()
        {
            // change word or not
            boolean mChange = false;
            int mBlinkTimes = 0;
            @Override
            public void run()
            {
                // execute in ui thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (++mBlinkTimes > BLINK_TIMES)
                        {
                            return;
                        }
                        // blink
                        for (int i=0; i< mWordsSelected.size(); i++)
                        {
                            mWordsSelected.get(i).mViewButton.setTextColor(
                                    mChange ? Color.RED : Color.WHITE);
                        }
                        mChange = !mChange;
                    }
                });
            }
        };

        // start task
        Timer timer = new Timer();
        timer.schedule(task, 1, 150);
    }

    // next level event
    private void handlePassEvent()
    {
        // show next level 
        mPassView = findViewById(R.id.pass_view);
        // set visibility
        mPassView.setVisibility(View.VISIBLE);

        // stop animation
        mViewPan.clearAnimation();
        // stop music
        MyPlayer.stopSong(MainActivity.this);

        // play sound of coin
        MyPlayer.playSound(MainActivity.this, MyPlayer.INDEX_SOUND_COIN);

        // current level index
        mCurrentLevel = (TextView) findViewById(R.id.txt_current_level);
        if (mCurrentLevel != null)
        {
            mCurrentLevel.setText((mCurrentStageIndex + 1) + "");
        }

        // show song name
        mCurrentSongName = (TextView) findViewById(R.id.txt_current_song_name);
        if (mCurrentSongName != null)
        {
            mCurrentSongName.setText(mCurrentSong.getSongName());
        }

        // next level
        ibNext = (ImageButton) findViewById(R.id.ib_next);
        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pass all level
                if (judgePassed())
                {
                    // jump into all pass interface
                    Util.startActivity(MainActivity.this, AllPassView.class);
                }
                else
                {
                    // start new level
                    mPassView.setVisibility(View.GONE);

                    // load level data
                    initCurrentStageData();
                }
            }
        });

    }

    // check the number of coins
    // true: enough, false: lack
    private boolean CheckCoins(int data)
    {
        // enough or not
        if (mCurrentCoins + data >= 0)
        {
            mCurrentCoins += data;

            mViewCurrentCoins.setText(mCurrentCoins + "");

            return true;
        }
        else
        {
            return false;
        }
    }

    // get data from the config.xml
    // delete word coin
    private int getDeleteWordCoins()
    {
        return this.getResources().getInteger(R.integer.pay_delete_word);
    }
    // tip answer coin
    private int getTipAnswerCoins()
    {
        return this.getResources().getInteger(R.integer.pay_tip_answer);
    }

    // delete word which is waiting select event
    private void handleDeleteWord()
    {
        mDeleteWord = (ImageButton) findViewById(R.id.btn_delete_word);
        mDeleteWord.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //deleteOneWord();
                // show dialog to confirm to delete word
                showCofirmDialog(ID_DIALOG_DELETE_WORD);
            }
        });
    }

    // tip answer
    private void handleTipAnswer()
    {
        mTipAnswer = (ImageButton) findViewById(R.id.btn_tip_answer);
        mTipAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //tipAnswer();
                // show dialog to confirm tip answer
                showCofirmDialog(ID_DIALOG_TIP_ANSWER);
            }
        });
    }

    // delete one word
    private void deleteOneWord()
    {
        // reduce coins
        if (!CheckCoins(-getDeleteWordCoins()))
        {
            // lack coins, show dialog
            showCofirmDialog(ID_DIALOG_LACK_COINS);
            return;
        }

        // set the word button to invisible
        setButtonVisible(findNotAnswerWord(), View.INVISIBLE);
    }

    // find a word is not answer and is visible
    private WordButton findNotAnswerWord()
    {
        Random random = new Random();
        WordButton buf = null;

        while(true)
        {
            int index = random.nextInt(MyGridView.COUNT_WORDS);

            buf = mAllWords.get(index);

            if (buf.mIsvisiable && !isTheAnswerWord(buf))
            {
                return buf;
            }
        }
    }

    // check a word is answer or not
    private boolean isTheAnswerWord(WordButton word)
    {
        boolean result = false;

        for (int i=0; i< mCurrentSong.getNameLength(); i++)
        {
            if (word.mWordString.equals("" + mCurrentSong.getNameCharacters()[i]))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    // tip answer: choose an answer
    private void tipAnswer()
    {
        boolean tipWord = false;
        for (int i =0; i< mWordsSelected.size(); i++)
        {
            if (mWordsSelected.get(i).mWordString.length() == 0)
            {
                // according to the current answer to choice a word to fill
                onWordButtonClick(findIsAnswerWord(i));

                tipWord = true;
                // reduce coins
                if (!CheckCoins(-getTipAnswerCoins()))
                {
                    // lack coins, show dialog
                    showCofirmDialog(ID_DIALOG_LACK_COINS);
                    return;
                }
                break;
            }
        }

        // not blank to fill
        if (!tipWord)
        {
            // blink the word
            blinkTheWords();
        }
    }

    // find a answer word
    // index: the location to fill the word
    private WordButton findIsAnswerWord(int index)
    {
        WordButton buf = null;

        for (int i=0; i< MyGridView.COUNT_WORDS; i++)
        {
            buf = mAllWords.get(i);

            if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index]))
            {
                return buf;
            }
        }

        return null;
    }


    // judge pass or not, if pass the least level
    private boolean judgePassed()
    {
        return (mCurrentStageIndex == Constant.SONG_INFO.length - 1);
    }

    /**************************************
     * custom dialog event listener
     **************************************/
    // delete one word
    private IAlertDialogButtonListener mBtnOkDeleteWord =
            new IAlertDialogButtonListener() {
        @Override
        public void onClick()
        {
            // run event
            deleteOneWord();
        }
    };
    // tip answer
    private IAlertDialogButtonListener mBtnOkTipAnswer =
            new IAlertDialogButtonListener() {
                @Override
                public void onClick()
                {
                    // run event
                    tipAnswer();
                }
            };
    // lack coins
    private IAlertDialogButtonListener mBtnOkLackCoins =
            new IAlertDialogButtonListener()
            {
                @Override
                public void onClick()
                {
                    // run event
                }
            };

    // show confirm dialog
    private void showCofirmDialog(int id)
    {
        switch (id)
        {
            case ID_DIALOG_DELETE_WORD:
                Util.showDialog(MainActivity.this,
                        "确认花掉"+ getDeleteWordCoins() +"个金币去掉一个错误答案？",
                        mBtnOkDeleteWord);
                break;
            case ID_DIALOG_TIP_ANSWER:
                Util.showDialog(MainActivity.this,
                        "确认花掉"+ getTipAnswerCoins() +"个金币获得一个文字提示？",
                        mBtnOkTipAnswer);
                break;
            case ID_DIALOG_LACK_COINS:
                Util.showDialog(MainActivity.this,
                        "金币不足，去商店补充？",
                        mBtnOkLackCoins);
                break;
        }
    }

}
