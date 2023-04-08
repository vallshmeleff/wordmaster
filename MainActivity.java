package com.example.wordmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//-----------------------------------------------------
//
// Word Master program for effective learning of foreign words
// The bottom line shows the set of letters that make up the foreign word you are learning
// By clicking on a letter, you rearrange it to the top line, where the studied word is collected
// Top left shows the translation of the word you are composing (studying)
// The tooltip is displayed on the left side. If you click on it, it will be more contrast
// If you move the screen up, you can select other languages
// NL NEXT - select native language
// FL NEXT - select the language you are learning
// LAUNCH - start the process of learning the selected language
// NEXT - go to the next word from the dictionary
// RELOAD - repeat the studied word
//
// (c) by Valery Shmelev (Oflameron) https://www.linkedin.com/in/valery-shmelev-479206227/
//
// Other projects http://oflameron.com
//
//-----------------------------------------------------

public class MainActivity extends AppCompatActivity {
    public String word = "green,зеленый,grun,zeleny,verde,verde,verte";
    public int len = 0; // Line length word
    public int e = 0; // What is the cell (letter) in the bottom row of Click-chickpea
    public int nc = 0; // Size of array - list of dictionary files
    public String[] separated;
    public TextView[] textViewArray;
    public TextView[] textViewArrayN;
    public EditText etext;
    public int[] CharCount; // Marks letters filled at the top
    public int[] SrcCharCount; // Marks used letters on the bottom line
    public List<String> myArray;
    public int dh; // Sample TextView width - top 0th cell
    public static Context context;
    final Random random = new Random();
    public int colorCode = 0; // Tooltip background color

    private static final int STORAGE_PERMISSION_CODE = 101;
    // Storage Permissions - For API 23+, you need to request read/write permissions even if they are already in your manifest
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public String ftext=""; // Buffer for reading the dictionary file line by line
    public String everything = ""; // Read the entire dictionary file here
    public String wnative = ""; // Translation of the word being taught
    public StringBuilder sb; // File Read Buffer
    public int ke = 0; // Number of lines in buffer (in file)
    public int kw = 0; // Number of lines in buffer (in file)
    public List<String> list = new ArrayList<String>(); // Dynamic list
    Button buttonExit;
    public  int textViewCountN = 18; // How many cells in a row
    public  int textViewCount = 18; // Number of cells in rows
    public int translater = 0; // Trigger for translation highlighting (hints)
    public int nlanguage = 1; // Select native language
    public int flanguage = 0; // Select foreign language


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Horizontal app screen


        //-----------------------------------------------
        // All preparation of the selected word before placing the top row in the TextView
        //-----------------------------------------------
        TextMode();


        //-----------------------------------------------
        textViewArray = new TextView[textViewCount]; // Array initialization
        CharCount = new int[textViewCount]; // Array initialization
        SrcCharCount = new int[textViewCount]; // Array initialization

         for(int i = 0; i < textViewCount; i++) {
             CharCount[i] = 0; // Counts letters filled at the top If =0, then the letter is not set
             SrcCharCount[i] = 1; // Indicates which letters below have already been used. If =0, then the letter is not used
             textViewArray[i] = new TextView(this);
        }

        textViewArray[0] = (TextView) findViewById(R.id.e1);
        textViewArray[0].getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        textViewArray[0].requestLayout();
            // Reference Cell Width
           Rect bounds = new Rect();
           Paint textPaint = textViewArray[0].getPaint();
           String text = textViewArray[0].getText().toString();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            dh = bounds.width() + 5; // Sample Cell Width - Top Left
            //-------------------------
        textViewArray[1] = (TextView) findViewById(R.id.e2);
        textViewArray[2] = (TextView) findViewById(R.id.e3);
        textViewArray[3] = (TextView) findViewById(R.id.e4);
        textViewArray[4] = (TextView) findViewById(R.id.e5);
        textViewArray[5] = (TextView) findViewById(R.id.e6);
        textViewArray[6] = (TextView) findViewById(R.id.e7);
        textViewArray[7] = (TextView) findViewById(R.id.e8);
        textViewArray[8] = (TextView) findViewById(R.id.e9);
        textViewArray[9] = (TextView) findViewById(R.id.e0);
        textViewArray[10] = (TextView) findViewById(R.id.eA);
        textViewArray[11] = (TextView) findViewById(R.id.eB);
        textViewArray[12] = (TextView) findViewById(R.id.eC);
        textViewArray[13] = (TextView) findViewById(R.id.eD);
        textViewArray[14] = (TextView) findViewById(R.id.eE);
        textViewArray[15] = (TextView) findViewById(R.id.eF);
        textViewArray[16] = (TextView) findViewById(R.id.eG);
        textViewArray[17] = (TextView) findViewById(R.id.eH);
        //--------------------------------------------------

        textViewArrayN = new TextView[textViewCount];
        for(int i = 0; i < textViewCountN; i++) {
            textViewArrayN[i] = new TextView(this);
            textViewArray[i].getLayoutParams().width = dh; // Make the width of the top cells according to the sample
            //-------------------------
        }
        textViewArrayN[0] = (TextView) findViewById(R.id.en1);
        textViewArrayN[1] = (TextView) findViewById(R.id.en2);
        textViewArrayN[2] = (TextView) findViewById(R.id.en3);
        textViewArrayN[3] = (TextView) findViewById(R.id.en4);
        textViewArrayN[4] = (TextView) findViewById(R.id.en5);
        textViewArrayN[5] = (TextView) findViewById(R.id.en6);
        textViewArrayN[6] = (TextView) findViewById(R.id.en7);
        textViewArrayN[7] = (TextView) findViewById(R.id.en8);
        textViewArrayN[8] = (TextView) findViewById(R.id.en9);
        textViewArrayN[9] = (TextView) findViewById(R.id.en0);
        textViewArrayN[10] = (TextView) findViewById(R.id.enA);
        textViewArrayN[11] = (TextView) findViewById(R.id.enB);
        textViewArrayN[12] = (TextView) findViewById(R.id.enC);
        textViewArrayN[13] = (TextView) findViewById(R.id.enD);
        textViewArrayN[14] = (TextView) findViewById(R.id.enE);
        textViewArrayN[15] = (TextView) findViewById(R.id.enF);
        textViewArrayN[16] = (TextView) findViewById(R.id.enG);
        textViewArrayN[17] = (TextView) findViewById(R.id.enH);
        for(int i = 0; i < textViewCountN; i++) {
            textViewArrayN[i].getLayoutParams().width = dh; // Make the width of the bottom cells according to the sample
            textViewArray[i].setText("");
            textViewArrayN[i].setText("");
        }

        etext = (EditText) findViewById(R.id.edt_send_message);

        RNDString(); // Pick a random word from the selected dictionary file

       separated = word.split(","); // Separate phrases by language
        // English,Russian,Deutsche,Czech,Portugal,Spanish,France
        Log.d("== Split Words ==", "== == == ==  separated[0] =="+ separated[0]); // English
        Log.d("== Split Words ==", "== == == ==  separated[1] =="+ separated[1]); // Russian
        Log.d("== Split Words ==", "== == == ==  separated[2] =="+ separated[2]); // Deutsche
        Log.d("== Split Words ==", "== == == ==  separated[3] =="+ separated[3]); // Czech
        Log.d("== Split Words ==", "== == == ==  separated[4] =="+ separated[4]); // Portugal
        Log.d("== Split Words ==", "== == == ==  separated[5] =="+ separated[5]); // Spanish
        Log.d("== Split Words ==", "== == == ==  separated[6] =="+ separated[6]); // France

        wnative = separated[nlanguage]; // Word translation in EditText (Eng at startup)
        TextView ntText = (TextView) findViewById(R.id.nativetext); // Hint text (translation)
        ntText.setText(separated[flanguage]); // Clue - foreign language


                    etext.setText(wnative, TextView.BufferType.EDITABLE);

        len = separated[flanguage].length(); // Для foreign language
        if (len == 0) {
            Log.d("== Split Words ==", "== == NO WORDS == ==");
        }  else {
            //--------------------------------------------------
            // myArray = new ArrayList<String>(len);
            Shuffle(); // Shuffle letters in a foreign word and write in the bottom line
            //--------------------------------------------------
        }


        TextView NtextView = (TextView) findViewById(R.id.native_language);
        NtextView.measure(0, 0);       //must call measure!
        int e = NtextView.getMeasuredWidth();  //get width
        NtextView.setWidth(e); // Programmatically set the width
        nlanguage = 3; // Go to Czech language (if needed)
        NtextView.setText("Czech");

        TextView FtextView = (TextView) findViewById(R.id.foreign_language);
        FtextView.measure(0, 0);       //must call measure!
        e = FtextView.getMeasuredWidth();  //get width
        FtextView.setWidth(e);
        nlanguage = 0; // Go to English
        FtextView.setText("English");

    } // OnCreate



    //--------------------------------------------------
    // Reading a file line by line
    //--------------------------------------------------
    @SuppressLint("SuspiciousIndentation")
    public StringBuilder readFileAsString(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        ke = 0; // Number of lines in buffer (in file)
        try {
            sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();

                        list.add(line);
                ke++; // Counting the number of lines
                kw = ke; // Number of lines in buffer (in file)
            }

           everything = sb.toString(); // Complete dictionary file
            int lk = everything.length(); // How many lines from the file were read

        } finally {
            br.close();
        }
        return sb; // Complete dictionary file
    }


    //--------------------------------------------------
    // Pick a random string from a dictionary file read
    //--------------------------------------------------
    public String RNDString() {
        String eseparated[] = word.split("\n"); // Split phrases by lines
        int selenght = eseparated.length - 2; // Number of lines in the dictionary file, except for the bottom two lines
        word = eseparated[random.nextInt(selenght)]; // Pick a random line

        return null;
    }




    //--------------------------------------------------
    // Handling letter presses on the bottom line
    //--------------------------------------------------
    // @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.en1: // The leftmost cell in the bottom row is clicked
                SetChar(0); // Rearrange the letter from the bottom cell to the free top
                SrcCharCount[0] = 1; // The letter at position =0 in the bottom line is used
                break;
            case R.id.en2: // The second cell in the bottom row is clicked
                SetChar(1); // Rearrange the letter from the bottom cell to the free top
                SrcCharCount[1] = 1; // The letter at position =1 in the bottom line is used
                break;
            case R.id.en3: 
                SetChar(2); 
                SrcCharCount[2] = 1;
                break;
            case R.id.en4: 
                SetChar(3); 
                SrcCharCount[3] = 1; 
                break;
            case R.id.en5: 
                SetChar(4); 
                SrcCharCount[4] = 1; 
                break;
            case R.id.en6: 
                SetChar(5); 
                SrcCharCount[5] = 1; 
                break;
            case R.id.en7:
                SetChar(6);
                SrcCharCount[6] = 1; 
                break;
            case R.id.en8:
                SetChar(7); 
                SrcCharCount[7] = 1; 
                break;
            case R.id.en9: 
                SetChar(8); 
                SrcCharCount[8] = 1; 
                break;
            case R.id.en0: 
                SetChar(9); 
                SrcCharCount[9] = 1; 
                break;
            case R.id.enA: 
                SetChar(10); 
                SrcCharCount[10] = 1; 
                break;
            case R.id.enB: 
                SetChar(11); 
                SrcCharCount[11] = 1; 
                break;
            case R.id.enC: 
                SetChar(12); 
                SrcCharCount[12] = 1; 
                break;
            case R.id.enD: 
                SetChar(13); 
                SrcCharCount[13] = 1; 
                break;
            case R.id.enE: 
                SetChar(14); 
                SrcCharCount[14] = 1; 
                break;
            case R.id.enF:
                SetChar(15); 
                SrcCharCount[15] = 1; 
                break;
            case R.id.enG: 
                SetChar(16); 
                SrcCharCount[16] = 1; 
                break;
            case R.id.enH: 
                SetChar(17); 
                SrcCharCount[17] = 1; 
                break;
        }
    }

    //--------------------------------------------------
    // Put a letter on the top line
    //--------------------------------------------------
    public  int SetChar(int n) {
        for (int i = 0; i < 18; i++) {
            if (SrcCharCount[n] == 0) { // Positions on the bottom row
                if (CharCount[i] == 0) { // Positions in the top row
                    textViewArray[i].setText("%"); // Debugging Option
                    textViewArray[i].setText(textViewArrayN[n].getText());
                    textViewArrayN[n].setText(".");
                    CharCount[i] = 1;
                    i = 18;
                } else {
 
                } // if (CharCount[i] == 0)
            }

        }
        return n;

         }



    //--------------------------------------------------
    // Shuffle letters in a string
    //--------------------------------------------------
    public String Shuffle() { // Shuffle letters in a string
        List<String> myArray = new ArrayList<String>(len);
        for (int i = 0; i < len; i++) {
            myArray.add(separated[flanguage].substring(i, i + 1));
        }
        Collections.shuffle(myArray); // Shuffle myArray
        for(int i = 0; i < len; i++){ // Pick a letter from a word
            String substr=myArray.get(i);
            textViewArrayN[i].setText(substr); // Put a letter on the bottom line
            SrcCharCount[i] = 0; // =0 means there is a letter in this position in the BOTTOM line
        }

        return null;
    }


    //==========================================================
    //   Handling words to fit over TextView in top row
    //==========================================================
    public String TextMode() {

        File directory = new File(getApplicationInfo().dataDir.toString() + "/files");
        File[] files = directory.listFiles();

        nc = files.length; // Dictionary file list length
        //--------------------------
        // Read random dictionary file #nc in ftext
        //--------------------------
        try {
            ftext = readFileAsString(files[random.nextInt(nc)].toString()).toString(); // Method for reading a file
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        //--------------------------
        // 
        word = ftext;

        return null;

    } // TextMode



    //==========================================================
    //   Learn the word again (Reload)
    //==========================================================
    public void   ReloadWord(View v) {
        int i = 0;
        for(i = 0; i < textViewCountN; i++) {
            textViewArray[i].setText(""); // Erase text from top line
            textViewArrayN[i].setText(""); // Erase text from bottom line
            CharCount[i] = 0; // Counts letters filled at the top If =0, then the letter is not set
            SrcCharCount[i] = 1; // Indicates which letters below have already been used. If =0, then the letter is not used
        }


        len = separated[flanguage].length(); // For foreign language
        if (len == 0) {
            Log.d("== Split Words ==", "== == NO WORDS == ==");
        }  else {
            //--------------------------------------------------
           Shuffle(); // Shuffle letters in a foreign word and write in the bottom line
            //--------------------------------------------------
        }




    }

    //==========================================================
    // Load next word
    //==========================================================
    public void NextWord(View v) {

        //-----------------------------------------------
        // Clear top row
        //-----------------------------------------------
        for(int i = 0; i < textViewCount; i++) {
            textViewArray[i].setText(" "); // Put a space in the TOP line - clear the line
            textViewArrayN[i].setText(" "); // Put a space in the BOTTOM line - clear the line
            CharCount[i] = 0; // Counts letters filled at the top If =0, then the letter is not set
            SrcCharCount[i] = 1; // Indicates which letters below have already been used. If =0, then the letter is not used
        }
        //-----------------------------------------------
	// All the preparation of the selected word before placing the top line in the TextView
        // As in OnCreate        
	//-----------------------------------------------
        TextMode();
        //-----------------------------------------------

        //-----------------------------------------------
        RNDString(); // Pick a random word from the selected dictionary file
        //-----------------------------------------------

        separated = word.split(","); // Separate phrases by language
        // English,Russian,Deutsche,Czech,Portugal,Spanish,France
        Log.d("== Split Words ==", "== == == ==  separated[0] =="+ separated[0]); // English
        Log.d("== Split Words ==", "== == == ==  separated[1] =="+ separated[1]); // Russian
        Log.d("== Split Words ==", "== == == ==  separated[2] =="+ separated[2]); // Deutsche
        Log.d("== Split Words ==", "== == == ==  separated[3] =="+ separated[3]); // Czech
        Log.d("== Split Words ==", "== == == ==  separated[4] =="+ separated[4]); // Portugal
        Log.d("== Split Words ==", "== == == ==  separated[5] =="+ separated[5]); // Spanish
        Log.d("== Split Words ==", "== == == ==  separated[6] =="+ separated[6]); // France

        wnative = separated[nlanguage]; // Word translation in EditText

        TextView ntText = (TextView) findViewById(R.id.nativetext); // Hint text (translation)
        ntText.setText(separated[flanguage]); // Hint - translation (foreign language)

        etext.setText(wnative, TextView.BufferType.EDITABLE);

        len = separated[flanguage].length(); // Foreign word - foreign language
        if (len == 0) {
            Log.d("== Split Words ==", "== == NO WORDS == ==");
        }  else {
            // If the foreign word is not zero length
            //--------------------------------------------------
            Shuffle(); // Shuffle letters in a foreign word and write in the bottom line
            //--------------------------------------------------
        }




    }

    //==========================================================
    //   Changing the tooltip background
    //==========================================================
    public void Translator(View v) {
        TextView ntText = (TextView) findViewById(R.id.nativetext); // Hint text (translation)

        if (translater == 0) {
            translater = 1;
            // Remember original tooltip background color
            colorCode = ((ColorDrawable) ntText.getBackground()).getColor();

            ntText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }  else {
            translater = 0;
            ntText.setBackgroundColor(colorCode);
        }

    }

    //==========================================================
    //   Native Language Select. Button NextNL
    //==========================================================
    public void NextNL(View v) {
        TextView NtextView = (TextView) findViewById(R.id.native_language);

        if (nlanguage == 0) { // Native language English?
            NtextView.setText("Russian");
            nlanguage++; // Skip to next mother tongue Russian
        } else {
            if (nlanguage == 1) { // Native language Russian?
                NtextView.setText("Deutsche");
                nlanguage++; // Skip to next mother tongue Deutsche
            } else {
                if (nlanguage == 2) { // Native language Deutsche?
                    NtextView.setText("Czech");
                    nlanguage++; // Skip to next mother tongue Czech
                } else {
                    if (nlanguage == 3) { // Native language Czech?
                        NtextView.setText("Portugal");
                        nlanguage++; // Skip to next mother tongue Portugal
                    } else {
                        if (nlanguage == 4) { // Native language Portugal?
                            NtextView.setText("Spanish");
                            nlanguage++; // Skip to next mother tongue Spanish
                        } else {
                            if (nlanguage == 5) { // Native language Spanish?
                                NtextView.setText("France");
                                nlanguage++; // Skip to next mother tongue France
                            } else {
                                if (nlanguage == 6) { // Native language France?
                                    NtextView.setText("English");
                                    nlanguage = 0; // Skip to next mother tongue =7 English
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }




    //==========================================================
    //   Foreign Language Select. Button NextFL
    //==========================================================
    public void NextFL(View v) {
        TextView FtextView = (TextView) findViewById(R.id.foreign_language);

        if (flanguage == 0) { // Foreign Language English?
            FtextView.setText("Russian");
            flanguage++; // Skip to next foreign Language Russian
        } else {
            if (flanguage == 1) { // Foreign Language Russian?
                FtextView.setText("Deutsche");
                flanguage++; // Skip to next foreign Language Deutsche
            } else {
                if (flanguage == 2) { // Foreign Language Deutsche?
                    FtextView.setText("Czech");
                    flanguage++; // Skip to next foreign Language Czech
                } else {
                    if (flanguage == 3) { // Foreign Language Czech?
                        FtextView.setText("Portugal");
                        flanguage++; // Skip to next foreign Language Portugal
                    } else {
                        if (flanguage == 4) { // Foreign Language Portugal?
                            FtextView.setText("Spanish");
                            flanguage++; // Skip to next foreign Language Spanish
                        } else {
                            if (flanguage == 5) { // Foreign Language Spanish?
                                FtextView.setText("France");
                                flanguage++; // Skip to next foreign Language France
                            } else {
                                if (flanguage == 6) { // Foreign Language France?
                                    FtextView.setText("English");
                                    flanguage = 0; // Skip to next foreign Language =7 English
                                }

                            }

                        }

                    }

                }

            }

        }

    }


    //==========================================================
    // Languages selected. Download words
    //==========================================================
    public void LoadLanguage(View v) {
        //-----------------------------------------------
        for(int i = 0; i < textViewCount; i++) {
            textViewArray[i].setText(" "); 
            textViewArrayN[i].setText(" "); 
            CharCount[i] = 0; 
            SrcCharCount[i] = 1; 
        }
        //-----------------------------------------------
        TextMode();
        //-----------------------------------------------
        RNDString(); // Pick a random word from the selected dictionary file
        //-----------------------------------------------
        separated = word.split(","); 
        // English,Russian,Deutsche,Czech,Portugal,Spanish,France
        Log.d("== Split Words ==", "== == == ==  separated[0] =="+ separated[0]); // English
        Log.d("== Split Words ==", "== == == ==  separated[1] =="+ separated[1]); // Russian
        Log.d("== Split Words ==", "== == == ==  separated[2] =="+ separated[2]); // Deutsche
        Log.d("== Split Words ==", "== == == ==  separated[3] =="+ separated[3]); // Czech
        Log.d("== Split Words ==", "== == == ==  separated[4] =="+ separated[4]); // Portugal
        Log.d("== Split Words ==", "== == == ==  separated[5] =="+ separated[5]); // Spanish
        Log.d("== Split Words ==", "== == == ==  separated[6] =="+ separated[6]); // France

        wnative = separated[nlanguage]; 
        TextView ntText = (TextView) findViewById(R.id.nativetext); 
        ntText.setText(separated[flanguage]);
        etext.setText(wnative, TextView.BufferType.EDITABLE);
        len = separated[flanguage].length(); 
        if (len == 0) {
        }  else {
            Shuffle(); 
            //--------------------------------------------------
        }

    }





    //==========================================================
    //   Exit the application. Button Exit
    //==========================================================
    public void Exit(View arg0) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

    }
    // (c) by Valery Shmelev https://github.com/vallshmeleff
    // http://oflameron.com
    
} // MainActivity