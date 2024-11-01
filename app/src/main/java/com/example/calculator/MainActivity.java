package com.example.calculator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {
    Vibrator vibrator;
    RecyclerView historyRecyclerView;
    HistoryAdapter historyAdapter;
    ConstraintLayout history;
    Button clearHistoryButton;
    private List<CalculationHistory> historyList = new ArrayList<>();

    EditText input;
    TextView result;
    Button historyButton;
    Button backspaceButton;
    Button button1; // clearAll
    Button button2; // Parentheses
    Button button3; // percent
    Button[] digitButtons;
    Button button4, button8, button12, button16; // operations
    Button button17; // toggle positivity :) -> :( -> :)
    Button button19; // point
    Button button20; // equals

    final Set<Character> operators = new HashSet<Character>(Arrays.asList('÷', '×', '-', '+'));
    Integer openParenthesesCount = 0;

    @Override
    protected void onResume() { // Request focus on the root layout to avoid focusing the EditText by default
        super.onResume();
        View rootLayout = findViewById(R.id.main);
        rootLayout.setFocusableInTouchMode(true);
        rootLayout.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        findViewByIdOfAllElements();

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new HistoryAdapter(this, historyList);
        historyRecyclerView.setAdapter(historyAdapter);

        setButtonClickListeners();
        setButtonTouchListeners();
    }

    private boolean hasTwoOrMoreNumbers(String input) {
        int numberCount = 0;
        boolean inNumber = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isDigit(currentChar) || currentChar == '.') {
                if (!inNumber) {
                    numberCount++;
                    inNumber = true;
                }
            } else inNumber = false;

            if (numberCount >= 2) return true;
        }
        return false;
    }

    private String getResult() {
        String inputText = input.getText().toString();

        if (hasTwoOrMoreNumbers(inputText))
            result.setVisibility(View.VISIBLE);
        else {
            result.setVisibility(View.INVISIBLE);
            return "";
        }

        try {
            StringBuilder modifiedExpression = new StringBuilder();

            for (int i = 0; i < inputText.length(); i++) {
                char currentChar = inputText.charAt(i);
                if (currentChar == '÷') modifiedExpression.append('/');
                else if (currentChar == '×') modifiedExpression.append('*');
                else modifiedExpression.append(currentChar);
            }

            int openParanthesesEnd = 0;
            for (int i = inputText.length() - 1; i > 0; i--) {
                if (inputText.charAt(i) == '(') openParanthesesEnd++;
                else if (!operators.contains(inputText.charAt(i))) break;
                modifiedExpression.deleteCharAt(i);
            }

            for (int i = 0; i < openParenthesesCount - openParanthesesEnd; i++)
                modifiedExpression.append(')');

            String expressionStr = modifiedExpression
                    .toString()
                    .replaceAll("(\\([^()]*\\)|\\d+(?:\\.\\d+)?)%", "($1/100.0)");

            Expression expression = new ExpressionBuilder(expressionStr).build();
            double result = expression.evaluate();

            if (result == (long) result)
                return String.valueOf((long) result);
            else return String.valueOf(result);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong :/\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return result.getText().toString();
    }

    private void clearAll() {
        input.setText("0");
        result.setVisibility(View.INVISIBLE);
        openParenthesesCount = 0;
        focusInput();
    }

    private void clearLast() {
        String inputText = input.getText().toString();

        if (inputText.length() > 1) {
            Character last = inputText.charAt(inputText.length() - 1);
            if (last.equals('(')) openParenthesesCount--;
            else if (last.equals(')')) openParenthesesCount++;

            inputText = inputText.substring(0, inputText.length() - 1);
        } else {
            inputText = "0";
            openParenthesesCount = 0;
        }
        input.setText(inputText);
        result.setText(getResult());
        focusInput();
    }

    private void inputNumber(String num) {
        String inputText = input.getText().toString();
        Character last = inputText.charAt(inputText.length() - 1);
        if (last.equals(')') || last.equals('%')) input.setText(inputText + "×" + num);
        else input.setText(inputText.equals("0") ? num : inputText + num);
        result.setText(getResult());
        focusInput();
    }

    private void inputOperator(Character operator) {
        String inputText = input.getText().toString();

        if (inputText.equals("0") && operator.equals('-')) input.setText("-");
        else if (inputText.equals("-")) {
            if (operator.equals('+'))
                input.setText("0");
        } else if (operators.contains(inputText.charAt(inputText.length() - 1))) {
            if (inputText.charAt(inputText.length() - 2) != '(')
                input.setText(inputText.substring(0, inputText.length() - 1) + operator);
        } else if (inputText.charAt(inputText.length() - 1) != '('
                || operator == '-')
            input.setText(inputText + operator);

        result.setText(getResult());
        focusInput();
    }

    private void inputParentheses() {
        String inputText = input.getText().toString();
        Character last = inputText.charAt(inputText.length() - 1);
        boolean isLastDigit = Character.isDigit(last);

        if (inputText.equals("0")) {
            input.setText("(");
            openParenthesesCount++;
        } else {
            boolean isExpression = isLastDigit || last.equals(')') || last.equals('%');
            if (openParenthesesCount > 0) {
                if (isExpression) {
                    input.setText(inputText + ")");
                    openParenthesesCount--;
                } else {
                    input.setText(inputText + "(");
                    openParenthesesCount++;
                }
            } else {
                if (isExpression) input.setText(inputText + "×(");
                else input.setText(inputText + "(");
                openParenthesesCount++;
            }
        }
        result.setText(getResult());
        focusInput();
    }

    private void inputPercent() {
        String inputText = input.getText().toString();
        Character last = inputText.charAt(inputText.length() - 1);
        if (Character.isDigit(last) || last == ')')
            input.setText(inputText + "%");
        result.setText(getResult());
        focusInput();
    }

    private void inputPoint() {
        String inputText = input.getText().toString();
        if (!isLastFloatNumber() && Character.isDigit(inputText.charAt(inputText.length() - 1)))
            input.setText(inputText + ".");
        result.setText(getResult());
        focusInput();
    }

    private void equals() {
        String resultText = result.getText().toString();
        if (!hasTwoOrMoreNumbers(input.getText().toString()) || resultText == "") return;

        addToHistory(input.getText().toString(), resultText);

        input.setText(resultText);
        result.setVisibility(View.INVISIBLE);
        openParenthesesCount = 0;
        focusInput();
    }

    private void addToHistory(String expression, String result) {
        CalculationHistory calculationHistory = new CalculationHistory(expression, result);
        historyList.add(calculationHistory);
        historyAdapter.notifyItemInserted(historyList.size() - 1);
        historyRecyclerView.scrollToPosition(historyList.size() - 1); // Scroll to the latest entry
    }

    private boolean isLastFloatNumber() {
        String inputText = input.getText().toString();

        for (int i = inputText.length() - 1; i > 0; i--) {
            if (inputText.charAt(i) == '.') return true;
            else if (!Character.isDigit(inputText.charAt(i))) return false;
        }
        return false;
    }

    private void togglePositivity() {
        String inputText = input.getText().toString();

        if (inputText.equals("0"))
            input.setText("-");
        else if (inputText.equals("-"))
            input.setText("0");
        else {
            Character last = inputText.charAt(inputText.length() - 1);
            boolean isExpression = Character.isDigit(last) || last.equals(')') || last.equals('%');

            if (!isExpression) return;

            int i = inputText.length() - 2;
            int paranthesesCount = last.equals(')') ? 1 : 0;

            while (paranthesesCount > 0 || (i >= 0 && Character.isDigit(inputText.charAt(i)))) {
                if (inputText.charAt(i) == ')')
                    paranthesesCount++;
                else if (inputText.charAt(i) == '(')
                    paranthesesCount--;
                i--;
            }

            StringBuilder result = new StringBuilder(inputText);

            if (i >= 0) {
                if (inputText.charAt(i + 1) == '(' && inputText.charAt(i + 2) == '-') {
                    result.delete(i + 1, i + 3);
                    result.delete(result.length() - 1, result.length());
                } else if (inputText.charAt(i) == '-') {
                    if (i == 0) result.delete(0, 1);
                    else result.setCharAt(i, '+');
                } else if (inputText.charAt(i) == '+')
                    result.setCharAt(i, '-');
                else {
                    result.insert(i + 1, "(-");
                    result.append(")");
                }

            } else result.insert(0, '-');

            input.setText(result.toString());
        }

        result.setText(getResult());
        focusInput();
    }

    private void focusInput() {
        if (!input.hasFocus()) input.requestFocus();
        input.setSelection(input.getText().length());
    }

    private void findViewByIdOfAllElements() {
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        input = findViewById(R.id.input);
        result = findViewById(R.id.result);

        history = findViewById(R.id.history);

        historyButton = findViewById(R.id.historyButton);
        backspaceButton = findViewById(R.id.backspaceButton);

        button1 = findViewById(R.id.button1);

        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        digitButtons = new Button[]{
                findViewById(R.id.button18),
                findViewById(R.id.button5),
                findViewById(R.id.button6),
                findViewById(R.id.button7),
                findViewById(R.id.button9),
                findViewById(R.id.button10),
                findViewById(R.id.button11),
                findViewById(R.id.button13),
                findViewById(R.id.button14),
                findViewById(R.id.button15)
        };

        button17 = findViewById(R.id.button17);
        button19 = findViewById(R.id.button19);
        button20 = findViewById(R.id.button20);

        button4 = findViewById(R.id.button4);
        button8 = findViewById(R.id.button8);
        button12 = findViewById(R.id.button12);
        button16 = findViewById(R.id.button16);
    }

    private void toggleHistoryVisibility() {
        if (history.getVisibility() == View.GONE)
            history.setVisibility(View.VISIBLE);
        else history.setVisibility(View.GONE);
    }

    private void setButtonClickListeners() {
        clearHistoryButton.setOnClickListener(v -> {
            historyList.clear();
            historyAdapter.notifyDataSetChanged();
        });

        historyButton.setOnClickListener(v -> toggleHistoryVisibility());
        backspaceButton.setOnClickListener(v -> clearLast());

        button1.setOnClickListener(v -> clearAll());

        button2.setOnClickListener(v -> inputParentheses());
        button3.setOnClickListener(v -> inputPercent());

        for (int i = 0; i < 10; i++) {
            final int number = i;
            digitButtons[i].setOnClickListener(v -> inputNumber(String.valueOf(number)));
        }

        button17.setOnClickListener(v -> togglePositivity());
        button19.setOnClickListener(v -> inputPoint());

        button4.setOnClickListener(v -> inputOperator('÷'));
        button8.setOnClickListener(v -> inputOperator('×'));
        button12.setOnClickListener(v -> inputOperator('-'));
        button16.setOnClickListener(v -> inputOperator('+'));

        button20.setOnClickListener(v -> equals());
    }

    private void setButtonTouchListeners() {
        input.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                input.requestFocus();
                input.setSelection(input.getText().length());
            }
            return true;
        });

        historyButton.setOnTouchListener((View v, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    vibrate();
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    return false;
            }
            return false;
        });

        backspaceButton.setOnTouchListener((View v, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    vibrate();
                    clearLast();
                    handler.postDelayed(backspaceRunnable, 300);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(backspaceRunnable);
                    return true;
            }
            return true;
        });

        button1.setOnTouchListener(this::handleTouchEvent);

        button2.setOnTouchListener(this::handleTouchEvent);
        button3.setOnTouchListener(this::handleTouchEvent);

        for (int i = 0; i < 10; i++) {
            digitButtons[i].setOnTouchListener(this::handleTouchEvent);
        }

        button17.setOnTouchListener(this::handleTouchEvent);
        button19.setOnTouchListener(this::handleTouchEvent);
        button20.setOnTouchListener(this::handleTouchEvent);

        button4.setOnTouchListener(this::handleTouchEvent);
        button8.setOnTouchListener(this::handleTouchEvent);
        button12.setOnTouchListener(this::handleTouchEvent);
        button16.setOnTouchListener(this::handleTouchEvent);
    }

    private final Handler handler = new Handler();
    private final Runnable backspaceRunnable = new Runnable() {
        @Override
        public void run() {
            clearLast();
            vibrate();
            handler.postDelayed(this, 50);
        }
    };

    private boolean handleTouchEvent(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                vibrate();
                animateScale(v, 0.8f);
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                animateScale(v, 1.0f);
                break;
        }
        return false;
    }

    private void animateScale(View view, float scale) {
        view.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(50)
                .start();
    }

    private void vibrate() {
        if (vibrator != null) vibrator.vibrate(50);
    }
}