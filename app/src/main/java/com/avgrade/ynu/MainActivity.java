package com.avgrade.ynu;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    public static boolean isNumeric(String s)
    {
        return DOUBLE_PATTERN.matcher(s).matches();
    }
    private EditText[] scoreEdits = new EditText[100];
    private EditText[] creditEdits = new EditText[100];
    private Button btn_del,btn_spec,btn_exe;
    private TextView valid_data, valid_grade, valid_credit,valid_credit_num, valid_data_num,valid_grade_num;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private void ChangeListener() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        for (int i = 0; i < 100; i++) {
            final int index = i;

            if (scoreEdits[i] != null) {
                scoreEdits[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String score_temp = scoreEdits[index].getText().toString();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("score_" + (index + 1), score_temp);
                        editor.apply();
                    }
                });
            }

            if (creditEdits[i] != null) {
                creditEdits[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String credit_temp = creditEdits[index].getText().toString();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("credit_" + (index + 1), credit_temp);
                        editor.apply();
                    }
                });
            }
        }
    }

    private void set_del() {
        for (int i = 0; i < 100; i++) {
            if (scoreEdits[i] != null) {
                scoreEdits[i].setText("");
            }
            if (creditEdits[i] != null) {
                creditEdits[i].setText("");
            }
        }
    }

    private void set_ini() {
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String[] score_disp = new String[100];
        String[] credit_disp = new String[100];

        for (int i = 0; i < 100; i++) {
            score_disp[i] = sharedpreferences.getString("score_" + (i + 1), "");
            credit_disp[i] = sharedpreferences.getString("credit_" + (i + 1), "");

            if (scoreEdits[i] != null) {
                scoreEdits[i].setText(score_disp[i]);
            }
            if (creditEdits[i] != null) {
                creditEdits[i].setText(credit_disp[i]);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_del = findViewById(getResources().getIdentifier("btn_del", "id", getPackageName()));
        btn_spec = findViewById(getResources().getIdentifier("btn_spec", "id", getPackageName()));
        btn_exe = findViewById(getResources().getIdentifier("btn_exe", "id", getPackageName()));

        for (int i = 0; i < 100; i++) {
            String scoreIdName = "score_" + (i + 1);
            String creditIdName = "credit_" + (i + 1);
            int scoreId = getResources().getIdentifier(scoreIdName, "id", getPackageName());
            int creditId = getResources().getIdentifier(creditIdName, "id", getPackageName());
            scoreEdits[i] = findViewById(scoreId);
            creditEdits[i] = findViewById(creditId);
        }

        set_ini();
        ChangeListener();

        btn_del.setOnClickListener(new View.OnClickListener() {
            final static int COUNTS = 3;
            final static long DURATION = 1500;
            long[] mHits = new long[COUNTS];

            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    mHits = new long[COUNTS];
                    set_del();
                    TextView valid_credit_num = findViewById(R.id.valid_credit_num);
                    TextView valid_data_num = findViewById(R.id.valid_data_num);
                    TextView valid_grade_num = findViewById(R.id.valid_grade_num);
                    valid_credit_num.setText("");
                    valid_data_num.setText("");
                    valid_grade_num.setText("");
                }
            }
        });

        btn_exe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double totalCredit = 0;
                int totalCourses = 0;

                for (int i = 0; i < 100; i++) {
                    String scoreStr = scoreEdits[i].getText().toString();
                    String creditStr = creditEdits[i].getText().toString();

                    if (isNumeric(scoreStr) && isNumeric(creditStr)) {
                        double credit = Double.parseDouble(creditStr);
                        totalCredit += credit;
                        totalCourses++;
                    }
                }

                TextView valid_credit_num = findViewById(R.id.valid_credit_num);
                TextView valid_data_num = findViewById(R.id.valid_data_num);

                valid_credit_num.setText(String.format("%.2f", totalCredit));
                valid_data_num.setText(String.valueOf(totalCourses));
                double totalWeightedScore = 0;
                double totalWeight = 0;

                for (int i = 0; i < 100; i++) {
                    String scoreStr = scoreEdits[i].getText().toString();
                    String creditStr = creditEdits[i].getText().toString();

                    if (isNumeric(scoreStr) && isNumeric(creditStr)) {
                        double score = Double.parseDouble(scoreStr);
                        double credit = Double.parseDouble(creditStr);

                        totalWeightedScore += score * credit;
                        totalWeight += credit;
                    }
                }

                TextView valid_grade_num = findViewById(R.id.valid_grade_num);

                if (totalWeight != 0) {
                    double weightedAverageScore = totalWeightedScore / totalWeight;
                    valid_grade_num.setText(String.format("%.4f", weightedAverageScore));
                } else {
                    valid_grade_num.setText("Error: Total weight cannot be zero.");
                }
            }
        });

        btn_spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double totalCredit = 0;
                int totalCourses = 0;

                for (int i = 0; i < 100; i++) {
                    String scoreStr = scoreEdits[i].getText().toString();
                    String creditStr = creditEdits[i].getText().toString();

                    if (isNumeric(scoreStr) && isNumeric(creditStr)) {
                        double credit = Double.parseDouble(creditStr);
                        totalCredit += credit;
                        totalCourses++;
                    }
                }

                TextView valid_credit_num = findViewById(R.id.valid_credit_num);
                TextView valid_data_num = findViewById(R.id.valid_data_num);

                valid_credit_num.setText(String.format("%.2f", totalCredit));
                valid_data_num.setText(String.valueOf(totalCourses));
            }
        });

    }

}