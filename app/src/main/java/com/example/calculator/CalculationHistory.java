package com.example.calculator;

public class CalculationHistory {
    private String expression;
    private String result;

    public CalculationHistory(String expression, String result) {
        this.expression = expression;
        this.result = result;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }
}