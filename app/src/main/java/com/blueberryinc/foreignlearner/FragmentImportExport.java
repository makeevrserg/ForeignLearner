package com.blueberryinc.foreignlearner;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blueberryinc.foreignlearner.Adapters.Options;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FragmentImportExport extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Настройка словаря");
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import_export, container, false);
    }

    Spinner spinnerImport;
    private TextView textViewImportWords;
    FragmentActivity mActivityContext;
    private String selectedFile = "";
    Toolbar toolbar;
    private ProgressDialog progressDialog;
    TextView textViewCustomDialog;
    Button exportWords;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = getActivity().findViewById(R.id.toolbar);
        spinnerImport = view.findViewById(R.id.spinner);
        textViewImportWords = view.findViewById(R.id.textView);
        Button buttonTestDocument = view.findViewById(R.id.buttonTestDocument);
        exportWords=view.findViewById(R.id.buttonExportWords);
        exportWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowProgressDialog();
                setExportWords();
            }
        });
        mActivityContext = getActivity();
        buttonTestDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowProgressDialog();
                Create_Test_Document();
            }
        });
        ApplyFilesData();
    }

    private String[] GetReadableFiles() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/ForeignLearner");
        ArrayList<String> fileNames = new ArrayList<>();
        int size = 0;
        for (final File f : folder.listFiles()) {
            if (f.isFile()) {
                if (f.getName().matches(".*\\.xlsx")) {
                    size++;
                    fileNames.add(f.getName());
                }
            }
        }
        String[] array = new String[size];
        return fileNames.toArray(array);
    }


    private void ApplyFilesData() {
        String[] filesList = GetReadableFiles();
        selectedFile = filesList[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, filesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImport.setAdapter(adapter);
        spinnerImport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        textViewImportWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFile.contains(".xlsx")) {
                    XLSToSqlite(DBHelper.TABLE_WORD_JAPANESE, "JAPANESE");
                }
            }
        });
    }


    private void ShowProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        View custom_dialog_view = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.custom_progress_dialog,
                        (RelativeLayout) getView().findViewById(R.id.dialog_container)
                );
        textViewCustomDialog = custom_dialog_view.findViewById(R.id.textView);
        progressDialog.setContentView(custom_dialog_view);

    }

    private void XLSToSqlite(final String TABLE, final String LANGUAGE) {
        ShowProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                DBHelper dbHelper = new DBHelper(getContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCustomDialog.setText("Идет очистка таблиц");
                    }
                });
                database.delete(TABLE, null, null);
                ContentValues contentValues = new ContentValues();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCustomDialog.setText("Идет чтение таблиц");
                    }
                });
                File inputFile = new File(Environment.getExternalStorageDirectory() + "/ForeignLearner", selectedFile);
                XSSFWorkbook workbook = null;

                try {
                    workbook = new XSSFWorkbook(new FileInputStream(inputFile));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                XSSFSheet myExcelSheet = workbook.getSheet(LANGUAGE);
                final int rows = myExcelSheet.getPhysicalNumberOfRows();
                for (int i = 2; i < rows; ++i) {
                    final int finalI = i;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewCustomDialog.setText("Идет загрузка слов\n" + finalI + "/" + rows);
                        }
                    });
                    Row cRow = myExcelSheet.getRow(i);
                    String progress = (cRow.getCell(0, Row.RETURN_BLANK_AS_NULL) == null) ? "" : cRow.getCell(0).getStringCellValue();
                    //Log.d("FragmentImportExport", "run: "+progress);
                    String tagsAr = (cRow.getCell(1, Row.RETURN_BLANK_AS_NULL) == null) ? "" : cRow.getCell(1, Row.RETURN_BLANK_AS_NULL).getStringCellValue();
                    String word = (cRow.getCell(2, Row.RETURN_BLANK_AS_NULL) == null) ? "" : cRow.getCell(2).getStringCellValue();
                    String transcription = (cRow.getCell(3, Row.RETURN_BLANK_AS_NULL) == null) ? "" : cRow.getCell(3, Row.RETURN_BLANK_AS_NULL).getStringCellValue();
                    String translationAr = (cRow.getCell(4, Row.RETURN_BLANK_AS_NULL) == null) ? "" : cRow.getCell(4).getStringCellValue();


                    if (word == "")
                        continue;
                    contentValues.put(DBHelper.KEY_WORD, word);
                    contentValues.put(DBHelper.KEY_TRANSCRIPTION, transcription);
                    contentValues.put(DBHelper.KEY_TRANSLATION, translationAr);
                    contentValues.put(DBHelper.KEY_TAGS, tagsAr);
                    contentValues.put(DBHelper.KEY_PROGRESS, progress.replace("%",""));
                    database.insert(TABLE, null, contentValues);
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                dbHelper.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                        if (LANGUAGE != "ENGLISH")
                            XLSToSqlite(DBHelper.TABLE_WORD_ENGLISH, "ENGLISH");

                    }
                });
            }
        }).start();
    }



    private void setExportWords() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XSSFWorkbook workbook = new XSSFWorkbook();
                workbook.createSheet("JAPANESE");
                workbook.createSheet("ENGLISH");

                DBHelper dbHelper = new DBHelper(mActivityContext);

                ExportWords(dbHelper,workbook,DBHelper.TABLE_WORD_JAPANESE, Options.JAPANESE);
                ExportWords(dbHelper,workbook,DBHelper.TABLE_WORD_ENGLISH, Options.ENGLISH);

                dbHelper.close();
                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "ForeignLearner");
                boolean success = folder.mkdirs();
                if (!success){
                    Toast.makeText(getContext(), "Произошла ошибка при создании файла", Toast.LENGTH_SHORT).show();
                    return;
                }

                Looper.prepare();
                try {
                    final String path = Environment.getExternalStorageDirectory() + File.separator + "ForeignLearner" + File.separator + "ForeignLearnerl1.xlsx";
                    FileOutputStream fileOut = new FileOutputStream(path);


                    workbook.write(fileOut);
                    fileOut.close();
                    mActivityContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Файл создан" + path, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            progressDialog.cancel();


                        }
                    });
                    workbook.close();
                    fileOut.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    mActivityContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Произошла ошибка при создании файла" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            progressDialog.cancel();

                        }
                    });

                }
            }
        }).start();


    }

    private void ExportWords(DBHelper dbHelper,XSSFWorkbook workbook,String TABLE,String SHEET){
        XSSFSheet myExcelSheet = workbook.getSheet(SHEET);
        myExcelSheet.createRow(1);
        Row row = myExcelSheet.getRow(1);
        for (int i = 0; i < 6; i++)
            row.createCell(i);
        row.getCell(0).setCellValue("Прогресс");
        row.getCell(1).setCellValue("Теги");
        row.getCell(2).setCellValue("Иностранное слово");
        row.getCell(3).setCellValue("Транскрипция");
        row.getCell(4).setCellValue("Перевод");


        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(TABLE,
                null, null, null, null, null, DBHelper.KEY_TAGS + "," + DBHelper.KEY_WORD);

        int rowCount =2;
        if (cursor.moveToFirst()) {

            //int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int wordIndex = cursor.getColumnIndex(DBHelper.KEY_WORD);
            int transcriptionIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSCRIPTION);
            int translationIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSLATION);
            int tagsIndex = cursor.getColumnIndex(DBHelper.KEY_TAGS);
            int progressIndex = cursor.getColumnIndex(DBHelper.KEY_PROGRESS);

            do {
                myExcelSheet.createRow(rowCount);
                row = myExcelSheet.getRow(rowCount);
                for (int i = 0; i < 6; i++)
                    row.createCell(i);
                row.getCell(0).setCellValue(cursor.getString(progressIndex));
                row.getCell(1).setCellValue(cursor.getString(tagsIndex));
                row.getCell(2).setCellValue(cursor.getString(wordIndex));
                row.getCell(3).setCellValue(cursor.getString(transcriptionIndex));
                row.getCell(4).setCellValue(cursor.getString(translationIndex));

                rowCount++;

            } while (cursor.moveToNext());
        }
        cursor.close();

    }
    private void Create_Test_Document() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                XSSFWorkbook workbook = new XSSFWorkbook();


                workbook.createSheet("JAPANESE");
                workbook.createSheet("ENGLISH");

                XSSFSheet myExcelSheet = workbook.getSheet("JAPANESE");
                myExcelSheet.createRow(1);
                myExcelSheet.createRow(2);
                Row row = myExcelSheet.getRow(1);
                CreateDeclaration(row);
                row = myExcelSheet.getRow(2);
                CreateJapaneseWords(row);
                for(int i =0;i<6;++i)
                    myExcelSheet.setColumnWidth(5,15);

                myExcelSheet = workbook.getSheet("ENGLISH");
                myExcelSheet.createRow(1);
                myExcelSheet.createRow(2);
                row = myExcelSheet.getRow(1);
                CreateDeclaration(row);
                row = myExcelSheet.getRow(2);
                CreateEnglishWords(row);
                for(int i =0;i<6;++i)
                    myExcelSheet.setColumnWidth(5,30);

                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "ForeignLearner");
                folder.mkdirs();

                Looper.prepare();
                try {
                    final String path = Environment.getExternalStorageDirectory() + File.separator + "ForeignLearner" + File.separator + "DocumentForImport.xlsx";
                    FileOutputStream fileOut = new FileOutputStream(path);


                    workbook.write(fileOut);
                    fileOut.close();
                    mActivityContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Файл создан" + path, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            progressDialog.cancel();

                        }
                    });
                    workbook.close();
                    fileOut.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    mActivityContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Произошла ошибка при создании файла" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            progressDialog.cancel();

                        }
                    });

                }

            }
        }).start();




    }

    private void CreateDeclaration(Row row) {
        for (int i = 0; i < 6; i++)
            row.createCell(i);
        row.getCell(0).setCellValue("Прогресс");
        row.getCell(1).setCellValue("Теги");
        row.getCell(2).setCellValue("Иностранное слово");
        row.getCell(3).setCellValue("Транскрипция");
        row.getCell(4).setCellValue("Перевод");
        row.getCell(5).setCellValue("Если есть несколько тегов на одно слово, то они должны писаться через разделитель \";\".\n" +
                "Это относится и к словам. Транскрипция и само слово пишется без разделителя \";\".\n" +
                "Проценты можно писать и без \"%\"\n" +
                "Слова считываются начиная со второй строки.\n" +
                "В словаре должно быть два листа с названием JAPANESE и ENGLISH, в противном случае первый лист будет считаться за японский, а второй - за английский");

    }

    private void CreateJapaneseWords(Row row) {
        for (int i = 0; i < 5; i++)
            row.createCell(i);
        row.getCell(0).setCellValue("0%");
        row.getCell(1).setCellValue("Тег 1;Тег 2;тег3;Мой тег 4;");
        row.getCell(2).setCellValue("位");
        row.getCell(3).setCellValue("くらい");
        row.getCell(4).setCellValue("Ранг;Звание; Чин; положение");
    }

    private void CreateEnglishWords(Row row) {
        for (int i = 0; i < 5; i++)
            row.createCell(i);
        row.getCell(0).setCellValue("0%");
        row.getCell(1).setCellValue("Тег 1;Тег 2;тег3;Мой тег 4;");
        row.getCell(2).setCellValue("Abdicate");
        row.getCell(3).setCellValue("[ˈæbdɪkeɪt]");
        row.getCell(4).setCellValue("Отрекаться;Отказываться; Отречься; Отречься от престола; Отказаться");
    }
}
