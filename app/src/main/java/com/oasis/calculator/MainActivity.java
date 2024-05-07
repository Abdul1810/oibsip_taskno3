package com.oasis.calculator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    StringBuilder currentCalculation = new StringBuilder();
    double result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "C":
                clear();
                break;
            case "⌫":
                if (currentCalculation.length() > 0) {
                    currentCalculation.deleteCharAt(currentCalculation.length() - 1);
                    tvResult.setText(currentCalculation);
                }
                break;
            case "=":
                calculateResult();
                break;
            default:
                currentCalculation.append(buttonText);
                tvResult.append(buttonText);
                break;
        }
    }

    private void clear() {
        currentCalculation.setLength(0);
        tvResult.setText("");
    }

    private void calculateResult() {
        String calculation = currentCalculation.toString();
        try {
            result = evaluateExpression(calculation);
            tvResult.append(" = " + result);
        } catch (Exception e) {
            tvResult.setText("Error");
        }
    }

    private double evaluateExpression(String expression) throws Exception {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();

    }
}
