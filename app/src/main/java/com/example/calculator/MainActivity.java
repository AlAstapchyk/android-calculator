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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Vibrator vibrator;

    EditText input;
    TextView result;
    Button backspaceButton;
    Button button1; // clearAll
    Button button2; // Parentheses
    Button button3; // percent
    Button button5, button6, button7, button9, button10, button11, button13, button14, button15, button18; // Numpad
    Button button4, button8, button12, button16; // operations
    Button button17; // toggle positivity :) -> :( -> :)
    Button button19; // point
    Button button20; // equals

    private static final Set<Character> operators = new HashSet<Character>(Arrays.asList('÷', '×', '-', '+'));
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

        setButtonClickListeners();
        setButtonTouchListeners();
    }

    private void clearAll() {
        input.setText("0");
        result.setText("");
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
        focusInput();
    }

    private void inputNumber(String num) {
        String inputText = input.getText().toString();
        Character last = inputText.charAt(inputText.length() - 1);
        if (last.equals(')') || last.equals('%')) input.setText(inputText + "×" + num);
        else input.setText(inputText.equals("0") ? num : inputText + num);
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
        focusInput();
    }

    private void inputPercent() {
        String inputText = input.getText().toString();
        Character last = inputText.charAt(inputText.length() - 1);
        if (Character.isDigit(last) || last == ')')
            input.setText(inputText + "%");
        focusInput();
    }

    private void inputPoint() {
        String inputText = input.getText().toString();
        if (!isLastFloatNumber() && Character.isDigit(inputText.charAt(inputText.length() - 1)))
            input.setText(inputText + ".");
        focusInput();
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

        focusInput();
    }

    private void focusInput() {
        if (!input.hasFocus()) input.requestFocus();
        input.setSelection(input.getText().length());
    }

    private void findViewByIdOfAllElements() {
        input = findViewById(R.id.input);
        result = findViewById(R.id.result);

        backspaceButton = findViewById(R.id.backspaceButton);

        button1 = findViewById(R.id.button1);

        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button9 = findViewById(R.id.button9);
        button10 = findViewById(R.id.button10);
        button11 = findViewById(R.id.button11);
        button13 = findViewById(R.id.button13);
        button14 = findViewById(R.id.button14);
        button15 = findViewById(R.id.button15);

        button17 = findViewById(R.id.button17);
        button18 = findViewById(R.id.button18);
        button19 = findViewById(R.id.button19);
        button20 = findViewById(R.id.button20);

        button4 = findViewById(R.id.button4);
        button8 = findViewById(R.id.button8);
        button12 = findViewById(R.id.button12);
        button16 = findViewById(R.id.button16);
    }

    private void setButtonClickListeners() {
        backspaceButton.setOnClickListener(v -> clearLast());

        button1.setOnClickListener(v -> clearAll());

        button2.setOnClickListener(v -> inputParentheses());
        button3.setOnClickListener(v -> inputPercent());

        button5.setOnClickListener(v -> inputNumber("1"));
        button6.setOnClickListener(v -> inputNumber("2"));
        button7.setOnClickListener(v -> inputNumber("3"));
        button9.setOnClickListener(v -> inputNumber("4"));
        button10.setOnClickListener(v -> inputNumber("5"));
        button11.setOnClickListener(v -> inputNumber("6"));
        button13.setOnClickListener(v -> inputNumber("7"));
        button14.setOnClickListener(v -> inputNumber("8"));
        button15.setOnClickListener(v -> inputNumber("9"));
        button18.setOnClickListener(v -> inputNumber("0"));

        button17.setOnClickListener(v -> togglePositivity());
        button19.setOnClickListener(v -> inputPoint());

        button4.setOnClickListener(v -> inputOperator('÷'));
        button8.setOnClickListener(v -> inputOperator('×'));
        button12.setOnClickListener(v -> inputOperator('-'));
        button16.setOnClickListener(v -> inputOperator('+'));
    }

    private void setButtonTouchListeners() {
        input.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                input.requestFocus();
                input.setSelection(input.getText().length());
            }
            return true;
        });

        backspaceButton.setOnTouchListener((v, event) -> {
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

        button5.setOnTouchListener(this::handleTouchEvent);
        button6.setOnTouchListener(this::handleTouchEvent);
        button7.setOnTouchListener(this::handleTouchEvent);
        button9.setOnTouchListener(this::handleTouchEvent);
        button10.setOnTouchListener(this::handleTouchEvent);
        button11.setOnTouchListener(this::handleTouchEvent);
        button13.setOnTouchListener(this::handleTouchEvent);
        button14.setOnTouchListener(this::handleTouchEvent);
        button15.setOnTouchListener(this::handleTouchEvent);
        button18.setOnTouchListener(this::handleTouchEvent);

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