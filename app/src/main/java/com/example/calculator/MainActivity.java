package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public enum Operation {
        DIVIDE, MULTIPLY, SUBTRACT, ADD
    }
    Operation operationState = null;
    int firstOperand = 0;
    int secondOperand = 0;
    TextView input;

    Button clearLastButton;
    Button button5, button6, button7, button9, button10, button11, button13, button14, button15, button18; // Numpad
    Button button2, button3; // brackets
    Button button4, button8, button12, button16;
    Button buttonRes;

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

        input = findViewById(R.id.input);

        clearLastButton = findViewById(R.id.backspaceButton);
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
        button18 = findViewById(R.id.button18);

        button4 = findViewById(R.id.button4);
        button8 = findViewById(R.id.button8);
        button12 = findViewById(R.id.button12);
        button16 = findViewById(R.id.button16);

        setButtonListeners();
    }

    private void setButtonListeners() {
        clearLastButton.setOnClickListener(v -> clearLast());

        button2.setOnClickListener(v -> inputNumber("("));
        button3.setOnClickListener(v -> inputNumber(")"));

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

//        button4.setOnClickListener(v -> setOperationState(Operation.DIVIDE));
//        button8.setOnClickListener(v -> setOperationState(Operation.MULTIPLY));
//        button12.setOnClickListener(v -> setOperationState(Operation.SUBTRACT));
//        button16.setOnClickListener(v -> setOperationState(Operation.ADD));
    }

    private void clearLast() {
        if(operationState != null) {
            operationState = null;
            return;
        }
        String inputText = input.getText().toString();
        if (inputText.length() > 1) {
            inputText = inputText.substring(0, inputText.length() - 1);
        } else inputText = "0";
        input.setText(inputText);
    }

    private void inputNumber(String num) {
        String inputText = input.getText().toString();
        input.setText(inputText.equals("0") ? num : inputText + num);
    }

//    private void setOperationState(Operation o) {
//        operationState = o;
//        String inputText = input.getText().toString();
//        String operationChar;
//        input.setText(inputText + operationChar);
//    }

//    private void result()
}