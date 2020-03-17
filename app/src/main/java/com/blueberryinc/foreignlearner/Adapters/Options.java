package com.blueberryinc.foreignlearner.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.blueberryinc.foreignlearner.DBHelper;
import com.blueberryinc.foreignlearner.EditWordActivity;
import com.blueberryinc.foreignlearner.R;
import com.blueberryinc.foreignlearner.WordElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Options {
    public int showWordsMatchWordsCount = 8;
    public int countSelectedWods;
    public boolean boolInfRepeat;
    public boolean boolShowLearnedWords;
    public boolean isTranscriptionShowed = false;
    public boolean[] boolCheckedTags;
    private String stringFirstToShow;
    public String[] ArrayStringOfAllTags;
    private String jsonData;
    private ArrayList<String> arStrTagList;
    public ArrayList<WordElement> mWordsToLearn;
    public WordElement[] mWordElement;
    private SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private FragmentActivity fragmentActivity;
    public String SELECTED_LANGUAGE = "JAPANESE";

    public static String SHARED_PREFS_LANGUAGE = "LANGUAGE";
    public static String ENGLISH = "ENGLISH";
    public static String JAPANESE = "JAPANESE";
    private static String TAG = "Options";
    private static final String JSONDATA = "JsonData";
    private static final String JSON_WORDS_TO_LEARN_ENGLISH = "JSON_WORDS_TO_LEARN_ENGLISH";
    private static final String JSON_WORDS_TO_LEARN_JAPANESE = "JSON_WORDS_TO_LEARN_JAPANESE";
    private static final String JSONSETTINGS = "JsonSettings";
    private static final String SHARED_PREFS = "sharedPrefs";
    public String PREVIOUS_MENU = "OPTIONS";
    public int hardWordTagCount=1;
    Context mContext;

    public Options(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        CreateSharedPrefs();
    }

    public Options(FragmentActivity fragmentActivity, Context mContext) {
        this.fragmentActivity = fragmentActivity;
        this.mContext = mContext;
        CreateSharedPrefs();
    }

    private void LoadWordsToLearn() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String jsonStr;
        if (SELECTED_LANGUAGE == "ENGLISH")
            jsonStr = sharedPreferences.getString(JSON_WORDS_TO_LEARN_ENGLISH, "None");
        else
            jsonStr = sharedPreferences.getString(JSON_WORDS_TO_LEARN_JAPANESE, "None");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray == null)
            return;
        JSONObject jsonObject;
        mWordsToLearn = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                jsonObject = (jsonArray.getJSONObject(i));
                mWordsToLearn.add(new WordElement(jsonObject.getString("Word"),
                        jsonObject.getString("Transcription"),
                        jsonObject.getJSONArray("Translation"),
                        jsonObject.getJSONArray("Tags"),
                        jsonObject.getString("Progress"),
                        jsonObject.getString("idIndex"))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void SaveWordsToLearn() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (WordElement word : mWordsToLearn) {
            JSONArray jsonArrayTranslation = new JSONArray();
            jsonObject = new JSONObject();
            for (String line : word.translation) {
                jsonArrayTranslation.put(line);
            }
            JSONArray jsonArrayTags = new JSONArray();
            for (String line : word.tags) {
                jsonArrayTags.put(line);
            }

            try {
                jsonObject.put("Word", word.word);
                jsonObject.put("Transcription", word.transcription);
                jsonObject.put("Progress", word.progressBar);
                jsonObject.put("Translation", jsonArrayTranslation);
                jsonObject.put("Tags", jsonArrayTags);
                jsonObject.put("idIndex", word.idIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }

        SharedPreferences sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        if (SELECTED_LANGUAGE == "ENGLISH")
            editor.putString(JSON_WORDS_TO_LEARN_ENGLISH, jsonArray.toString());
        else
            editor.putString(JSON_WORDS_TO_LEARN_JAPANESE, jsonArray.toString());
        editor.apply();
    }

    public void LoadWords() {
        LoadWordsFromMySqlite();
        LoadWordsToLearn();
    }

    public void Load_CURRNET_MENU() {
        PREVIOUS_MENU = sharedPreferences.getString("PREVIOUS_MENU", "DICTIONARY");
    }

    public void Load_boolInfRepeat() {
        boolInfRepeat = sharedPreferences.getBoolean("boolInfRepeat", false);
        Log.d(TAG, "Load_boolInfRepeat: " + boolInfRepeat);
    }

    public void Load_boolShowLearnedWords() {
        boolShowLearnedWords = sharedPreferences.getBoolean("boolShowLearnedWords", false);
    }

    public void Load_isTranscriptionShowed() {
        isTranscriptionShowed = sharedPreferences.getBoolean("isTranscriptionShowed", false);
    }

    public void Load_SELECTED_LANGUAGE() {
        SELECTED_LANGUAGE = sharedPreferences.getString(SHARED_PREFS_LANGUAGE, "JAPANESE");
    }

    public void Load_countSelectedWods() {
        countSelectedWods = SELECTED_LANGUAGE == ENGLISH ? sharedPreferences.getInt("countSelectedWords_ENGLISH", 0) : sharedPreferences.getInt("countSelectedWords_JAPANESE", 0);
    }

    public void Load_showWordsMatchWordsCount() {
        showWordsMatchWordsCount = sharedPreferences.getInt("showWordsMatchWordsCount", 8);
    }
    public void Load_hardWordTagCount() {
        hardWordTagCount=sharedPreferences.getInt("hardWordTagCount", 1);
    }
    public boolean isDatasetChanged() {
        return sharedPreferences.getBoolean("isDatasetChanger", false);
    }

    public void Load_boolCheckedTags() {
        try {
            JSONArray jsonArBoolChecked;
            if (SELECTED_LANGUAGE == "ENGLISH")
                jsonArBoolChecked = new JSONArray(sharedPreferences.getString("boolCheckedTags_ENGLISH", "[]"));
            else
                jsonArBoolChecked = new JSONArray(sharedPreferences.getString("boolCheckedTags_JAPANESE", "[]"));

            if (jsonArBoolChecked.length() > 0) {
                boolCheckedTags = new boolean[jsonArBoolChecked.length()];
                for (int i = 0; i < jsonArBoolChecked.length(); ++i)
                    boolCheckedTags[i] = jsonArBoolChecked.getBoolean(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void LoadJsonSettings() {
        Load_boolInfRepeat();
        Load_boolShowLearnedWords();
        Load_isTranscriptionShowed();
        Load_SELECTED_LANGUAGE();
        Load_countSelectedWods();
        Load_showWordsMatchWordsCount();
        Load_boolCheckedTags();
        Load_hardWordTagCount();
    }

    @SuppressLint("CommitPrefEdits")
    private void CreateSharedPrefs() {
        sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void SaveShowedTranscription() {
        editor.putBoolean("isTranscriptionShowed", isTranscriptionShowed);
        editor.apply();
    }

    public void Save_isDatasetChanger(boolean isChanged) {
        editor.putBoolean("isDatasetChanger", isChanged);
        editor.apply();
    }

    public void Save_boolInfRepeat() {
        editor.putBoolean("boolInfRepeat", boolInfRepeat);
        editor.apply();
    }

    public void Save_CURRENT_MENU() {
        editor.putString("PREVIOUS_MENU", PREVIOUS_MENU);
        editor.apply();
    }

    public void Save_stringFirstToShow() {
        editor.putString("stringFirstToShow", stringFirstToShow);
        editor.apply();
    }

    public void Save_boolShowLearnedWords() {
        editor.putBoolean("boolShowLearnedWords", boolShowLearnedWords);
        editor.apply();
    }

    public void Save_countSelectedWods() {
        editor.putInt(SELECTED_LANGUAGE.equals(ENGLISH) ? "countSelectedWords_ENGLISH" : "countSelectedWords_JAPANESE", countSelectedWods);
        editor.apply();
    }

    public void Save_showWordsMatchWordsCount() {
        editor.putInt("showWordsMatchWordsCount", showWordsMatchWordsCount);
        editor.apply();
    }
    public void Save_hardWordTagCount(int k) {
        hardWordTagCount+=k;
        if (hardWordTagCount<1)
            hardWordTagCount=1;
        editor.putInt("hardWordTagCount", hardWordTagCount);
        editor.apply();
    }

    public void Save_boolCheckedTags() {
        JSONArray jsonArrayBoolTag = new JSONArray();
        if (boolCheckedTags != null) {
            for (boolean bool : boolCheckedTags) {
                jsonArrayBoolTag.put(bool);
            }
            if (SELECTED_LANGUAGE.equals("ENGLISH"))
                editor.putString("boolCheckedTags_ENGLISH", jsonArrayBoolTag.toString());
            else
                editor.putString("boolCheckedTags_JAPANESE", jsonArrayBoolTag.toString());
            editor.apply();
        }
    }

    public void Save_selectedLanguage() {
        sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFS_LANGUAGE, SELECTED_LANGUAGE);
        editor.apply();
    }

    public void SaveJsonSettings() {
        Save_boolInfRepeat();
        SaveShowedTranscription();
        Save_stringFirstToShow();
        Save_boolShowLearnedWords();
        Save_countSelectedWods();
        Save_showWordsMatchWordsCount();
        Save_boolCheckedTags();
        Save_selectedLanguage();
        Save_hardWordTagCount(0);
    }


    private void LoadWordsFromMySqlite() {
        DBHelper dbHelper = new DBHelper(fragmentActivity);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor;

        if (SELECTED_LANGUAGE.equals(ENGLISH))
            cursor = database.query(DBHelper.TABLE_WORD_ENGLISH,
                    null, null, null, null, null, DBHelper.KEY_TAGS + "," + DBHelper.KEY_WORD);
        else
            cursor = database.query(DBHelper.TABLE_WORD_JAPANESE,
                    null, null, null, null, null, DBHelper.KEY_TAGS + "," + DBHelper.KEY_WORD);


        mWordElement = new WordElement[0];
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int wordIndex = cursor.getColumnIndex(DBHelper.KEY_WORD);
            int transcriptionIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSCRIPTION);
            int translationIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSLATION);
            int tagsIndex = cursor.getColumnIndex(DBHelper.KEY_TAGS);
            int progressIndex = cursor.getColumnIndex(DBHelper.KEY_PROGRESS);

            do {
                WordElement wordElement = new WordElement(
                        cursor.getString(wordIndex),
                        cursor.getString(transcriptionIndex),
                        cursor.getString(translationIndex),
                        cursor.getString(tagsIndex), cursor.getString(idIndex),
                        cursor.getInt(progressIndex));
                mWordElement = Arrays.copyOf(mWordElement, mWordElement.length + 1);
                mWordElement[mWordElement.length - 1] = wordElement;

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        CreateAllTags();
    }

    public int CountWords(ArrayList<String> mTagName) {
        int count = 0;
        mWordsToLearn = new ArrayList<>();
        for (WordElement word : mWordElement) {
            boolean alreadyContains = false;
            for (String tag : mTagName) {
                boolean isCont = false;
                for (String tags : word.tags) {
                    for(String wordTag:tags.split(";")) {
                        if (wordTag.replaceAll(" ", "").
                                equals(tag.replaceAll(" ", "")))
                            isCont = true;
                    }
                }
                if (isCont && !alreadyContains) {
                    alreadyContains = true;
                    count++;
                    mWordsToLearn.add(word);
                }
            }
        }
        countSelectedWods = count;
        Save_countSelectedWods();
        SaveWordsToLearn();
        return count;
    }

    private void CreateAllTags() {
        ArrayList<String> ListOfAllTags = new ArrayList<>();
        for (WordElement word : mWordElement) {
            for (String tags : word.tags)
                for(String tag:tags.split(";"))
                    if (!ListOfAllTags.contains(tag.trim()))
                        ListOfAllTags.add(tag.trim());

        }
        ArrayStringOfAllTags = new String[ListOfAllTags.size()];
        for (int i = 0; i < ListOfAllTags.size(); ++i)
            ArrayStringOfAllTags[i] = ListOfAllTags.get(i);

        Arrays.sort(ArrayStringOfAllTags,new AlphanumComparator());

        if (boolCheckedTags == null || boolCheckedTags.length != ArrayStringOfAllTags.length)
            boolCheckedTags = new boolean[ListOfAllTags.size()];

    }

    Toast hintWordToast;
    public void StopToastHintWord(){
        if (hintWordToast!=null)
            hintWordToast.cancel();
    }
    public void ShowToastHintWord(View view,LayoutInflater layoutInflater,String message){
        StopToastHintWord();
        View layout = layoutInflater.inflate(R.layout.toast_show_unknown_word,(ViewGroup)view.findViewById(R.id.toast_root));
        hintWordToast = new Toast(mContext);
        TextView toastText = layout.findViewById(R.id.textView_toast);
        toastText.setText(message);
        hintWordToast.setGravity(Gravity.CENTER,0,0);
        hintWordToast.setDuration(Toast.LENGTH_SHORT);
        hintWordToast.setView(layout);
        hintWordToast.show();
    }

    Toast toastMessage;

    public void delete_Toast() {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
    }

    ProgressDialog progressDialog;

    private void ShowProgressDialog() {
        progressDialog = new ProgressDialog(fragmentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        View custom_dialog_view = LayoutInflater.from(fragmentActivity)
                .inflate(
                        R.layout.custom_progress_dialog,
                        (RelativeLayout) fragmentActivity.findViewById(R.id.dialog_container)
                );
        TextView textViewCustomDialog = custom_dialog_view.findViewById(R.id.textView);
        textViewCustomDialog.setText("Пожалуйста подождите");
        progressDialog.setContentView(custom_dialog_view);

    }

    public void ChangeWordProgress(WordElement word,int progressChange) {
        DBHelper dbHelper = new DBHelper(fragmentActivity);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String TABLE = (!SELECTED_LANGUAGE.equals("JAPANESE")) ? DBHelper.TABLE_WORD_ENGLISH : DBHelper.TABLE_WORD_JAPANESE;
        Cursor cursor = database.rawQuery("select * from " + TABLE + " where " + DBHelper.KEY_ID + "='" + word.idIndex + "'", null);

        if (cursor.moveToFirst()) {
            do {
                int progressIndex = cursor.getColumnIndex(DBHelper.KEY_PROGRESS);
                int progress = Integer.parseInt(cursor.getString(progressIndex).replace("%", ""));
                progress+=progressChange;
                if (progress<0)
                    progress=0;
                else if (progress>100)
                    progress=100;
                Log.d(TAG, "ChangeWordProgress: progress="+progress+";word="+word.word);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_WORD, word.word);
                contentValues.put(DBHelper.KEY_TRANSCRIPTION, word.transcription.replace("[", "").replace("]", ""));
                contentValues.put(DBHelper.KEY_TRANSLATION, Arrays.toString(word.translation).replace("[", "").replace("]", ""));
                contentValues.put(DBHelper.KEY_TAGS, Arrays.toString(new String[]{Arrays.toString(word.tags)}).replace("[", "").replace("]", ""));
                contentValues.put(DBHelper.KEY_PROGRESS, progress);
                database.update(TABLE, contentValues, DBHelper.KEY_ID + "=" + word.idIndex, null);
                cursor.close();
                dbHelper.close();
                return;

            } while (cursor.moveToNext());
        }
    }

    public void AddHardWord(WordElement word) {

        ShowProgressDialog();

        DBHelper dbHelper = new DBHelper(fragmentActivity);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String TABLE = (!SELECTED_LANGUAGE.equals("JAPANESE")) ? DBHelper.TABLE_WORD_ENGLISH : DBHelper.TABLE_WORD_JAPANESE;
        Log.d(TAG, "AddHardWord: " + word.idIndex);
        Cursor cursor = database.rawQuery("select * from " + TABLE + " where " + DBHelper.KEY_ID + "='" + word.idIndex + "'", null);
        if (cursor.moveToFirst()) {
            do {
                int tagsIndex = cursor.getColumnIndex(DBHelper.KEY_TAGS);
                if (cursor.getString(tagsIndex).contains("Тяжелые")) {
                    if (toastMessage != null)
                        toastMessage.cancel();
                    toastMessage = Toast.makeText(mContext, "Слово уже отмечено как тяжелое", Toast.LENGTH_SHORT);
                    toastMessage.show();
                    progressDialog.dismiss();
                    progressDialog.cancel();
                    return;
                }

            } while (cursor.moveToNext());
        }


        word.tags = Arrays.copyOf(word.tags, word.tags.length + 1);
        word.tags[word.tags.length - 1] = "Тяжелые "+hardWordTagCount;
//        database.delete(
//                TABLE,
//                DBHelper.KEY_ID+"=?",
//                new String[]{String.valueOf(word.idIndex)});


        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_WORD, word.word);
        contentValues.put(DBHelper.KEY_TRANSCRIPTION, word.transcription.replace("[", "").replace("]", ""));
        contentValues.put(DBHelper.KEY_TRANSLATION, Arrays.toString(word.translation).replace("[", "").replace("]", ""));
        contentValues.put(DBHelper.KEY_TAGS, Arrays.toString(new String[]{Arrays.toString(word.tags)}).replace("[", "").replace("]", ""));
        contentValues.put(DBHelper.KEY_PROGRESS, word.progressBar);
        database.update(TABLE, contentValues, DBHelper.KEY_ID + "=" + word.idIndex, null);
        cursor.close();
        dbHelper.close();
        //database.insert(TABLE,null,contentValues);
        if (!Arrays.asList(ArrayStringOfAllTags).contains("Тяжелые")) {
            Log.d(TAG, "AddHardWord: Not Found Tag");
            ArrayStringOfAllTags = Arrays.copyOf(ArrayStringOfAllTags, ArrayStringOfAllTags.length + 1);
            boolCheckedTags = Arrays.copyOf(boolCheckedTags, boolCheckedTags.length + 1);
            ArrayStringOfAllTags[ArrayStringOfAllTags.length - 1] = "Тяжелые";
            boolCheckedTags[boolCheckedTags.length - 1] = false;
            Save_boolCheckedTags();
        }
        if (toastMessage != null)
            toastMessage.cancel();
        toastMessage = Toast.makeText(mContext, "К слову добавлен тег: \"Тяжелые "+hardWordTagCount+"\"", Toast.LENGTH_SHORT);
        toastMessage.show();
        progressDialog.dismiss();
        progressDialog.cancel();
        Log.d(TAG, "AddHardWord: Finished");
    }

}
