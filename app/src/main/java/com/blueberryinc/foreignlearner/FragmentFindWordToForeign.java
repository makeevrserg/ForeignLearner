package com.blueberryinc.foreignlearner;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blueberryinc.foreignlearner.Adapters.Options;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class FragmentFindWordToForeign extends Fragment {
    Options mOptions;
    Button[] buttonAnswer;
    TextView textViewWord;
    TextView textViewWordTranscription;
    ImageView imageViewShowTranscription;
    ProgressBar progressBar;
    private Context mContext;
    boolean isTranscriptionShowed;
    boolean infRepeat;
    boolean allowedToClick = true;
    int wordCount = -1;
    int currentWordCount;
    int TRAINING_TYPE;
    public static String TAG = "TrainingFindWord";
    TextToSpeech textToSpeech;
    ImageButton imageButtonHintWord;
    boolean isAnswerCorrect;
    int colorStartIncorrect = 0xFF393e49;
    int colorEndIncorrect = 0xFFc72c41;
    ImageButton imageButtonRevertWord;
    int colorStartCorrect = 0xFF393e49;
    int colorEndCorrect = 0xFF91bd3a;

    int intCorrectBtnAnswer;
    WordElement correctWord;
    int[] randCount;
    ImageButton imageButtonAddHard;
    BottomNavigationView bottomNavigationView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_foreign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView=getActivity().findViewById(R.id.bottom_navigation);
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        mContext = getContext();
        mOptions = new Options(requireActivity(),mContext);
        mOptions.LoadJsonSettings();
        mOptions.LoadWords();
        LoadColors();
        isTranscriptionShowed = mOptions.isTranscriptionShowed;
        LoadButtons(view);
        textViewWord = view.findViewById(R.id.textViewWord);
        //SetSpeaker();
        imageViewShowTranscription = view.findViewById(R.id.imageViewShowTranscription);
        textViewWordTranscription=view.findViewById(R.id.textViewTranscription);
        imageViewShowTranscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTranscriptionShowed = !isTranscriptionShowed;
                if (!isTranscriptionShowed) {
                    textViewWordTranscription.setVisibility(TextView.INVISIBLE);
                    for (int i = 0; i < 4; ++i)
                        buttonAnswer[i].setText(mOptions.mWordsToLearn.get(randCount[i]).word);
                }
                else {
                    textViewWordTranscription.setVisibility(TextView.VISIBLE);
                    for (int i = 0; i < 4; ++i) {
                        String transcription = "";
                        if (isTranscriptionShowed)
                            if (!mOptions.mWordsToLearn.get(randCount[i]).transcription.isEmpty())
                                transcription = "[" + mOptions.mWordsToLearn.get(randCount[i]).transcription.replace("[","").replace("]","") + "]";
                        buttonAnswer[i].setText(mOptions.mWordsToLearn.get(randCount[i]).word + transcription);
                    }
                }
                mOptions.isTranscriptionShowed = isTranscriptionShowed;
                mOptions.SaveShowedTranscription();
            }
        });
        progressBar = view.findViewById(R.id.progressBar);
        infRepeat = mOptions.boolInfRepeat;
        progressBar.setMax(mOptions.mWordsToLearn.size());
        progressBar.setProgress(0);
        wordCount = mOptions.mWordsToLearn.size();

        imageButtonAddHard = view.findViewById(R.id.imageButtonAddToHard);
        imageButtonHintWord = view.findViewById(R.id.imageButtonHintWord);
        imageButtonHintWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptions.ChangeWordProgress(correctWord,-2);
                mOptions.ShowToastHintWord(view,getLayoutInflater(), correctWord.word);
            }
        });
        imageButtonRevertWord = view.findViewById(R.id.imageButtonRevertWord);
        imageButtonRevertWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWordCount!=0){
                    currentWordCount--;
                    allowedToClick = false;
                    onRevertWordAnimation();
                }else
                    Toast.makeText(mContext, "Это первое слово", Toast.LENGTH_SHORT).show();
            }
        });

        currentWordCount = 0;
        Collections.shuffle(mOptions.mWordsToLearn);
        UpdateTask();


    }


    private void onRevertWordAnimation(){
        allowedToClick=true;
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_in);
        if (isTranscriptionShowed) {
            textViewWordTranscription.startAnimation(animation);
        }
        textViewWord.startAnimation(animation);
        for (int i = 0; i < 4; i++)
            buttonAnswer[i].startAnimation(animation);
        imageButtonAddHard.startAnimation(animation);
        imageButtonRevertWord.startAnimation(animation);
        imageViewShowTranscription.startAnimation(animation);
        UpdateTask();
    }



    public void UpdateTask() {
        progressBar.setProgress(currentWordCount);
        isAnswerCorrect = true;
        intCorrectBtnAnswer = (int) (Math.random() * 4);
        correctWord = mOptions.mWordsToLearn.get(currentWordCount);
        textViewWord.setText(Arrays.toString(correctWord.translation).replace("[","").replace("]",""));

        imageButtonAddHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+correctWord.idIndex+";"+correctWord.word);
                mOptions.AddHardWord(correctWord);
            }
        });
        textViewWordTranscription.setText("");
        randCount = new int[4];
        ArrayList<Integer> a = new ArrayList<>(mOptions.mWordsToLearn.size());
        for (int i = 0; i < mOptions.mWordsToLearn.size(); i++)
            a.add(i);
        Collections.shuffle(a);
        boolean hasCorrectAnswer = false;
        for (int i = 0; i < 4; ++i) {
            if (a.get(i) == currentWordCount) {
                intCorrectBtnAnswer = i;
                hasCorrectAnswer = true;
            }
            randCount[i] = a.get(i);
        }

        if (!hasCorrectAnswer)
            randCount[intCorrectBtnAnswer] = currentWordCount;


        for (int i = 0; i < 4; i++) {
            buttonAnswer[i].setBackgroundColor(colorStartCorrect);


            String transcription = "";
            if (isTranscriptionShowed)
                if (!mOptions.mWordsToLearn.get(randCount[i]).transcription.isEmpty())
                    transcription = "[" + mOptions.mWordsToLearn.get(randCount[i]).transcription.replace("[","").replace("]","") + "]";

            buttonAnswer[i].setText(mOptions.mWordsToLearn.get(randCount[i]).word + transcription);


            buttonAnswer[i].setEllipsize(TextUtils.TruncateAt.MARQUEE);
            buttonAnswer[i].setSingleLine(true);
            buttonAnswer[i].setMarqueeRepeatLimit(-1);
            buttonAnswer[i].setSelected(true);
            final int currentBtn = i;
            buttonAnswer[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!allowedToClick)
                        return;
                    if (currentBtn == intCorrectBtnAnswer) {
                        if (isAnswerCorrect) {
                            currentWordCount++;
                        }
                        if (currentWordCount >= wordCount) {
                            if (!infRepeat) {

                                onCorrectAnswerAnimation(view);
                                onPostDelayedAnimation(view, false);
                                onCompleteAlert();

                                return;
                            } else {
                                Collections.shuffle(mOptions.mWordsToLearn);
                                currentWordCount = 0;
                                onCorrectAnswerAnimation(view);
                                onPostDelayedAnimation(view, true);
                            }

                        }

                        mOptions.ChangeWordProgress(correctWord,+1);

                        onCorrectAnswerAnimation(view);


                        allowedToClick = false;
                        mOptions.StopToastHintWord();
                        onPostDelayedAnimation(view, true);


                    } else {
                        isAnswerCorrect = false;
                        mOptions.ChangeWordProgress(correctWord,-5);
                        onIncorrectAnswerAnimation(view);
                    }
                }
            });
        }
    }

    private void LoadButtons(View view) {
        buttonAnswer = new Button[4];
        buttonAnswer[0] = view.findViewById(R.id.buttonAnswer1);
        buttonAnswer[1] = view.findViewById(R.id.buttonAnswer2);
        buttonAnswer[2] = view.findViewById(R.id.buttonAnswer3);
        buttonAnswer[3] = view.findViewById(R.id.buttonAnswer4);
        for (int i = 0; i < 4; ++i) {
            final int finalI = i;
            buttonAnswer[i].setTextSize(20);
            buttonAnswer[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new androidx.appcompat.app.AlertDialog.Builder(mContext)
                            .setTitle("Выбранное слово")
                            .setMessage(buttonAnswer[finalI].getText())
                            .create().show();
                    return false;
                }
            });
        }
    }

    private void SetSpeaker() {
        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.JAPAN);
                } else {
                    Toast.makeText(mContext, "Synth ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textToSpeech.isSpeaking())
                    textToSpeech.speak(textViewWord.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private void LoadColors() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorStartIncorrect = typedValue.data;
        colorStartCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorcorrect, typedValue, true);
        colorEndCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorincorrect, typedValue, true);
        colorEndIncorrect = typedValue.data;

    }

    private void onPostDelayedAnimation(View view, final boolean goUpdate) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                allowedToClick = true;
                if (goUpdate)
                    UpdateTask();
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_in);
                if (isTranscriptionShowed) {
                    textViewWordTranscription.startAnimation(animation);
                }
                textViewWord.startAnimation(animation);
                for (int i = 0; i < 4; i++)
                    buttonAnswer[i].startAnimation(animation);

            }
        }, 300);
    }


    private void onIncorrectAnswerAnimation(View view) {




        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorStartIncorrect, colorEndIncorrect);
        colorAnim.setDuration(600);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }


    private void onCorrectAnswerAnimation(View view) {

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorStartCorrect, colorEndCorrect);
        colorAnim.setDuration(400);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_out);
        if (isTranscriptionShowed)
            textViewWordTranscription.startAnimation(animation);
        textViewWord.startAnimation(animation);
        for (int i = 0; i < 4; i++)
            buttonAnswer[i].startAnimation(animation);

        imageButtonAddHard.startAnimation(animation);
        imageButtonRevertWord.startAnimation(animation);
        imageViewShowTranscription.startAnimation(animation);
        imageButtonHintWord.startAnimation(animation);
    }

    private void onCompleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Начать тренировку заново?")
                .setCancelable(false)
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().getFragmentManager().popBackStack();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentWordCount = 0;
                        progressBar.setProgress(0);

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
