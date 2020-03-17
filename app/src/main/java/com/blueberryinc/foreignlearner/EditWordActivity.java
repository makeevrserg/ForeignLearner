package com.blueberryinc.foreignlearner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blueberryinc.foreignlearner.DBHelper;
import com.blueberryinc.foreignlearner.Adapters.ChoosedTagItemAdapter;
import com.blueberryinc.foreignlearner.DBHelper;
import com.blueberryinc.foreignlearner.Adapters.Options;
import com.blueberryinc.foreignlearner.Adapters.WordEditInputElementAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class EditWordActivity extends AppCompatActivity {

    ImageView imageViewClose;
    Button buttonSave;
    EditText editTextWord;
    ImageView imageViewSpeakWord;
    ArrayList<EditText> editTextTranscription;
    ArrayList<EditText> editTextsTranslation;
    RecyclerView recyclerViewSelectedTags;
    TextView textViewAddTranslation;
    TextToSpeech textToSpeech;
    ImageView imageViewSelectTags;
    TextView textViewAddTranscription;
    ArrayList<EditText> editTextsTags;
    Options mOptions;
    RecyclerView recyclerViewEditTextTranscription;
    RecyclerView recyclerViewEditTextTranslation;
    int POSITION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        POSITION = intent.getIntExtra("position", -1);
        setContentView(R.layout.activity_edit_word);
        mOptions = new Options(this);
        mOptions.LoadJsonSettings();
        mOptions.LoadWords();
        recyclerViewEditTextTranscription = findViewById(R.id.recycler_viewEditTextTranscription);
        recyclerViewEditTextTranslation = findViewById(R.id.recycler_viewEditTextTranslation);
        imageViewClose = findViewById(R.id.imageViewClose);

        buttonSave = findViewById(R.id.buttonChangeWord);

        editTextWord = findViewById(R.id.editTextWord);

        imageViewSpeakWord = findViewById(R.id.imageViewSpeakWord);

        editTextTranscription = new ArrayList<>();
        //editTextTranscription.add(new EditText(this));

        recyclerViewSelectedTags = findViewById(R.id.recyclerViewTagsSelected_word_edit);

        imageViewSelectTags = findViewById(R.id.imageButtonTagSelect_word_edit);

        textViewAddTranscription = findViewById(R.id.textViewAddTranscription);

        editTextsTags = new ArrayList<>();


        textViewAddTranslation = findViewById(R.id.textViewAddTranslation);


        editTextsTranslation = new ArrayList<>();
        editTextsTranslation.add(new EditText(this));

        stringsOfTags = Arrays.copyOf(mOptions.ArrayStringOfAllTags,mOptions.ArrayStringOfAllTags.length);
        boolSelectedTags = new boolean[stringsOfTags.length];

        mTagName = new ArrayList<>();
        Log.d(TAG, "onCreate: "+POSITION);
        if (POSITION!=-1){
            WordElement word = mOptions.mWordElement[POSITION];
            editTextsTranslation = new ArrayList<>();
            editTextTranscription.add(new EditText(this));
            editTextTranscription.get(0).setText(word.transcription.trim());
            editTextWord.setText(word.word);
            for(String translation:word.translation){
                EditText editText = new EditText(this);
                editText.setText(translation.trim());
                editTextsTranslation.add(editText);
            }
            for(int i =0;i<stringsOfTags.length;++i){
                for(String tag:word.tags){
                    if (stringsOfTags[i].replaceAll(" ","").equals(tag.replaceAll(" ",""))) {
                        boolSelectedTags[i] = true;
                        AddToMTag(i,boolSelectedTags[i]);
                    }
                }
            }

        }


        initRecyclerViewOption(recyclerViewEditTextTranscription,"Транскрипция",editTextTranscription);
        initRecyclerViewOption(recyclerViewEditTextTranslation,"Перевод",editTextsTranslation);
        initRecyclerViewSelectedTagsOption();
    }



    private void AddToMTag(int i,boolean b){
        boolSelectedTags[i]=b;
        Log.d(TAG, "mBuilder: Started");
        if (!b)
            for(int j = 0; j< mTagName.size(); ++j)
                if (mTagName.get(j).contains(stringsOfTags[i]))
                    mTagName.remove(j);

        if (boolSelectedTags[i]){
            if (!mTagName.contains(mOptions.ArrayStringOfAllTags[i]))
                mTagName.add(mOptions.ArrayStringOfAllTags[i]);
        }
    }


    private static final String TAG = "EditWordActivity";
    public void onClickSelectTags(View view) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Выберите теги");
        Log.d(TAG, "onClick: Started");
        mBuilder.setMultiChoiceItems(stringsOfTags, boolSelectedTags, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                AddToMTag(i,b);
                adapter.notifyDataSetChanged();
            }

        });
        mBuilder.setCancelable(true);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        Log.d(TAG, "mBuilder: Ended");
        mBuilder.show();
        Log.d(TAG, "mBuilder: Showed");
    }


    ArrayList<String> mTagName;
    ChoosedTagItemAdapter adapter;
    @SuppressLint("SetTextI18n")
    private void initRecyclerViewSelectedTagsOption() {
        Log.d(TAG, "initRecyclerView:started");


        GridLayoutManager gridLayoutManager= new GridLayoutManager(EditWordActivity.this, 6,GridLayoutManager.VERTICAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (position % 5) {
                    // first two items span 3 columns each
                    case 0:
                    case 1:
                        return 3;
                    // next 3 items span 2 columns each
                    case 2:
                    case 3:
                    case 4:
                        return 2;
                }
                return (position%3==0?2:1);
            }
        });

        recyclerViewSelectedTags.setLayoutManager(gridLayoutManager);
        adapter = new ChoosedTagItemAdapter(mTagName);
        recyclerViewSelectedTags.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String tagName=adapter.getTagAt(viewHolder.getAdapterPosition());
                if (stringsOfTags !=null)
                    for(int i = 0; i<stringsOfTags.length; ++i){
                        if (stringsOfTags[i].contains(tagName)){
                            boolSelectedTags[i]=false;
                        }
                    }
                mTagName.remove(tagName);
                adapter.notifyDataSetChanged();


            }
        }).attachToRecyclerView(recyclerViewSelectedTags);
    }


    WordEditInputElementAdapter adapterTranslation;
    WordEditInputElementAdapter adapterTranscription;
    @SuppressLint("SetTextI18n")
    private void initRecyclerViewOption(RecyclerView recyclerView, final String hintText, final ArrayList<EditText> editTexts) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (hintText.equals("Перевод")){
            adapterTranslation = new WordEditInputElementAdapter(hintText,editTexts);
            recyclerView.setAdapter(adapterTranslation);
        } else{
            adapterTranscription = new WordEditInputElementAdapter(hintText,editTexts);
            recyclerView.setAdapter(adapterTranscription);
        }
        recyclerView.setItemViewCacheSize(editTexts.size());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (editTexts.size()<2) {
                    if (hintText.equals("Перевод"))
                        adapterTranslation.notifyDataSetChanged();
                    else
                        adapterTranscription.notifyDataSetChanged();
                    return;
                }

                editTexts.remove(viewHolder.getAdapterPosition());
                if (hintText.equals("Перевод"))
                    adapterTranslation.notifyDataSetChanged();
                else
                    adapterTranscription.notifyDataSetChanged();


            }
        }).attachToRecyclerView(recyclerView);
    }

    boolean [] boolSelectedTags;
    String [] stringsOfTags;
    public void onClickAddTag(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите новый тег");
        final EditText editTextInput = new EditText(this);
        builder.setView(editTextInput);
        builder.setCancelable(true);
        builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog.Builder builderConf = new AlertDialog.Builder(EditWordActivity.this);
                builderConf.setTitle("Подтвердите создание нового тега");
                builderConf.setMessage(editTextInput.getText().toString());
                builderConf.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOptions.ArrayStringOfAllTags = Arrays.copyOf(mOptions.ArrayStringOfAllTags,mOptions.ArrayStringOfAllTags.length+1);
                        mOptions.ArrayStringOfAllTags[mOptions.ArrayStringOfAllTags.length-1] = editTextInput.getText().toString();
                        Toast.makeText(EditWordActivity.this, editTextInput.getText().toString()+" создан", Toast.LENGTH_SHORT).show();
                        boolSelectedTags=Arrays.copyOf(boolSelectedTags,boolSelectedTags.length+1);
                        boolSelectedTags[boolSelectedTags.length-1]=true;
                        stringsOfTags=Arrays.copyOf(mOptions.ArrayStringOfAllTags,mOptions.ArrayStringOfAllTags.length);
                        Log.d(TAG, "onClick: "+ Arrays.toString(mTagName.toArray()));
                        mTagName.add(editTextInput.getText().toString());
                        adapter.notifyDataSetChanged();
                        mOptions.SaveJsonSettings();
                    }
                });
                builderConf.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                final AlertDialog adConf=builderConf.create();
                adConf.show();
            }
        });
        final AlertDialog ad=builder.create();
        ad.show();

    }


    public void onClickSpeakWord(View view) {
        if (!textToSpeech.isSpeaking())
            textToSpeech.speak(editTextWord.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
    }

    public void onClickCloseEditor(View view) {
    }

    public void onClickSaveWord(View view) {

        String word=editTextWord.getText().toString();
        String []translations= new String[editTextsTranslation.size()];
        for(int i =0;i<translations.length;++i){
            View mView = recyclerViewEditTextTranslation.getChildAt(i);
            EditText nameEditText = (EditText) mView.findViewById(R.id.editTextInputElementAdapter);
            String name = nameEditText.getText().toString();
            translations[i]=name;
        }
        String []transcriptions= new String[editTextTranscription.size()];
        for(int i =0;i<transcriptions.length;++i){
            View mView = recyclerViewEditTextTranscription.getChildAt(i);
            EditText nameEditText = (EditText) mView.findViewById(R.id.editTextInputElementAdapter);
            String name = nameEditText.getText().toString();
            transcriptions[i]=name;
        }
        int countTags=0;
        ArrayList<String> tags = new ArrayList<>();
        for(int i =0;i<boolSelectedTags.length;++i) {
            if (boolSelectedTags[i])
                tags.add(stringsOfTags[i]);
        }

        Log.d(TAG, "onClickSaveWord: Word="+word);
        Log.d(TAG, "onClickSaveWord: Transcription="+ Arrays.toString(transcriptions));
        Log.d(TAG, "onClickSaveWord: Translations="+ Arrays.toString(translations));
        Log.d(TAG, "onClickSaveWord: Tags="+tags.toString());

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

//        String translation="[";
//        String tagsToPut="[";
//        for(int k =0;k<translations.length;++k)
//            if (k!=translations.length-1)
//                translation+=("\""+translations[k]+"\",");
//            else
//                translation+=("\""+translations[k]+"\"]");


        if (tags.size()==0)
            tags.add("Untagged");
//        for(int k =0;k<tags.size();++k)
//            if (k!=tags.size()-1)
//                tagsToPut+=("\""+tags.get(k)+"\",");
//            else
//                tagsToPut+=("\""+tags.get(k)+"\"]");

        contentValues.put(DBHelper.KEY_WORD,word);
        contentValues.put(DBHelper.KEY_TRANSCRIPTION, Arrays.toString(transcriptions).replace("[","").replace("]",""));
        contentValues.put(DBHelper.KEY_TRANSLATION, Arrays.toString(translations).replace("[","").replace("]",""));
        contentValues.put(DBHelper.KEY_TAGS, Arrays.toString(tags.toArray()).replace("[","").replace("]",""));
        contentValues.put(DBHelper.KEY_PROGRESS, "0");

        Cursor cursor;

        String TABLE=(!mOptions.SELECTED_LANGUAGE.equals("JAPANESE"))?DBHelper.TABLE_WORD_ENGLISH:DBHelper.TABLE_WORD_JAPANESE;

        //cursor = database.query(TABLE, null,null,null,null,null,DBHelper.KEY_TAGS);

        cursor = database.query(TABLE,null,null,null,null,null,DBHelper.KEY_TAGS);

        if (POSITION!=-1){
            database.delete(
                    TABLE,
                    DBHelper.KEY_ID+"=?",
                    new String[]{String.valueOf(mOptions.mWordElement[POSITION].idIndex)});
        }
        database.insert(TABLE,null,contentValues);


//       cursor= database.rawQuery("SELECT "+
//               DBHelper.KEY_ID+","+
//                DBHelper.KEY_WORD+","+
//                DBHelper.KEY_TRANSCRIPTION+","+
//                DBHelper.KEY_TRANSLATION+","
//                +DBHelper.KEY_TAGS+","+
//                DBHelper.KEY_PROGRESS+
//                " FROM "+TABLE+" ORDER BY "+DBHelper.KEY_TAGS+","+DBHelper.KEY_WORD+" COLLATE UNICODE",null);


        //cursor = database.rawQuery("SELECT * from "+TABLE+" ORDER BY "+DBHelper.KEY_TAGS+ " ASC",null);

        cursor.close();
        dbHelper.close();
        Toast.makeText(this, "Слово добавлено", Toast.LENGTH_SHORT).show();
        mOptions.Save_isDatasetChanger(true);
    }

    private void AddEditText(String hint,ArrayList<EditText> editTextArrayList,LinearLayout linearLayout){

    }

    public void onClickAddTranscription(View view) {
        editTextTranscription.add(new EditText(this));
        adapterTranscription.notifyDataSetChanged();
    }

    public void onClickAddTranslation(View view) {
        editTextsTranslation.add(new EditText(this));
        adapterTranslation.notifyDataSetChanged();
    }
}
