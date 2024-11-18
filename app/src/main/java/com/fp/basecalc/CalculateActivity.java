package com.fp.basecalc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CalculateActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private Runnable runnable;

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public enum Operation {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("*"),
        DIVIDE("/");

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum DecimalType {
        HEXADECIMAL("0x"),
        OCTAL("0c"),
        BINARY("0b");

        private final String format;

        // Constructor should match the enum name
        DecimalType(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculate);

        final String[] old_input = new String[1];
        Button btn_history = findViewById(R.id.btn_history);
        EditText input_text = findViewById(R.id.input_text);
        TextView decimal = findViewById(R.id.dec);
        TextView binary = findViewById(R.id.bin);
        TextView octal = findViewById(R.id.oct);
        TextView hexadecimal = findViewById(R.id.hex);

        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculateActivity.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        input_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                old_input[0] = s.toString();
                handler.removeCallbacks(runnable);
                runnable = () -> {
                    if (s.toString().isEmpty()) {
                        return;
                    }
                    CalculationEntry newEntry = new CalculationEntry(System.currentTimeMillis(), s.toString());
                    if (ReadWrite.isFilePresent(CalculateActivity.this, "storage.json")) {
                        boolean success = ReadWrite.write(CalculateActivity.this, "storage.json", newEntry);
                        if (!success) {
                            Toast.makeText(getApplicationContext(), "Failed to save history.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        List<CalculationEntry> ent = new ArrayList<CalculationEntry>();
                        ent.add(newEntry);
                        ReadWrite.create(CalculateActivity.this, "storage.json", ent);
                        boolean success = ReadWrite.write(CalculateActivity.this, "storage.json", newEntry);
                        if (!success) {
                            Toast.makeText(getApplicationContext(), "Failed to save history.", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                handler.postDelayed(runnable, 3000);

                boolean decimal_input_valid = false;
                int decimal_input;
                String binary_input;
                String octal_input;
                String hexadecimal_input;

                String input = s.toString().trim();
                input = input.replaceAll("\\\\s+", "");

                if (input.matches(".*[\\+\\-\\*/]{2,}.*")) {
                    String buff = "Not a valid input!";
                    decimal.setText(buff);
                    binary.setText(buff);
                    octal.setText(buff);
                    hexadecimal.setText(buff);
                    return;
                }

                if (input.contains("+") || input.contains("-") || input.contains("*") || input.contains("/")) {
                    String regex = String.join("|",
                            Pattern.quote(Operation.ADD.getSymbol()),
                            Pattern.quote(Operation.SUBTRACT.getSymbol()),
                            Pattern.quote(Operation.MULTIPLY.getSymbol()),
                            Pattern.quote(Operation.DIVIDE.getSymbol())
                    );

                    Pattern pattern = Pattern.compile("[\\+\\-\\*/]");
                    Matcher matcher = pattern.matcher(input);

                    ArrayList<String> operations = new ArrayList<>();

                    while (matcher.find()) {
                        operations.add(matcher.group());
                    }

                    String[] stuff = input.split("\\s*[" + regex + "]\\s*");

                    int stuff_len = stuff.length;
                    if (stuff_len <= 1) {
                        return;
                    }
                    int index = 0;
                    int[] nums = new int[stuff_len];
                    int tmp_buf;
                    String[] operations_arr = operations.toArray(new String[0]);
                    for (String part : stuff) {
                        if (part.startsWith(DecimalType.HEXADECIMAL.getFormat())) {
                            try {
                                tmp_buf = Integer.parseInt(part.substring(2), 16);
                            } catch (NumberFormatException e) {
                                tmp_buf = 0;
                            }
                        } else if (part.startsWith(DecimalType.OCTAL.getFormat())) {
                            try {
                                tmp_buf = Integer.parseInt(part.substring(2), 7);
                            } catch (NumberFormatException e) {
                                tmp_buf = 0;
                            }
                        } else if (part.startsWith(DecimalType.BINARY.getFormat())) {
                            try {
                                tmp_buf = Integer.parseInt(part.substring(2), 2);
                            } catch (NumberFormatException e) {
                                tmp_buf = 0;
                            }
                        } else {
                            try {
                                tmp_buf = Integer.parseInt(part);
                            } catch (NumberFormatException e) {
                                tmp_buf = 0;
                            }
                        }
                        nums[index] = tmp_buf;
                        index += 1;
                    }

                    index = 0;
                    int operation_index = 0;
                    int sum = 0;
                    while (index < nums.length) {
                        if (index == 0) {
                            sum = nums[index];
                            index += 1;
                            continue;
                        }
                        switch (operations_arr[operation_index]) {
                            case "+":
                                sum += nums[index];
                                break;
                            case "-":
                                sum -= nums[index];
                                break;
                            case "*":
                                sum *= nums[index];
                                break;
                            case "/":
                                sum /= nums[index];
                                break;
                        }

                        index += 1;
                        operation_index += 1;
                    }
                    decimal_input = sum;
                    hexadecimal_input = Integer.toHexString(decimal_input);
                    octal_input = Integer.toOctalString(decimal_input);
                    binary_input = Integer.toBinaryString(decimal_input);

                    binary.setText(binary_input);
                    decimal.setText(String.valueOf(decimal_input));
                    octal.setText(octal_input);
                    hexadecimal.setText(hexadecimal_input);
                    return;
                }

                if (input.startsWith(DecimalType.HEXADECIMAL.getFormat()) && input.length() > 2) {
                    hexadecimal_input = input.substring(2);
                    try {
                        decimal_input = Integer.parseInt(hexadecimal_input, 16);
                        binary_input = Integer.toBinaryString(decimal_input);
                        octal_input = Integer.toOctalString(decimal_input);
                        decimal_input_valid = true;
                    } catch (NumberFormatException e) {
                        hexadecimal_input = "";
                        binary_input = "";
                        decimal_input = 0;
                        octal_input = "";
                    }
                } else if (input.startsWith(DecimalType.BINARY.getFormat()) && input.length() > 2) {
                    binary_input = input.substring(2);
                    try {
                        decimal_input = Integer.parseInt(binary_input, 2);
                        hexadecimal_input = Integer.toHexString(decimal_input);
                        octal_input = Integer.toOctalString(decimal_input);
                        decimal_input_valid = true;
                    } catch (NumberFormatException e) {
                        hexadecimal_input = "";
                        binary_input = "";
                        decimal_input = 0;
                        octal_input = "";
                    }
                } else if (isNumeric(input)) {
                    decimal_input = Integer.parseInt(input);
                    hexadecimal_input = Integer.toHexString(decimal_input);
                    octal_input = Integer.toOctalString(decimal_input);
                    binary_input = Integer.toBinaryString(decimal_input);
                    decimal_input_valid = true;
                } else if (input.startsWith(DecimalType.OCTAL.getFormat()) && input.length() > 2) {
                    octal_input = input.substring(2);
                    try {
                        decimal_input = Integer.parseInt(octal_input, 7);
                        hexadecimal_input = Integer.toHexString(decimal_input);
                        octal_input = Integer.toOctalString(decimal_input);
                        binary_input = Integer.toBinaryString(decimal_input);
                        decimal_input_valid = true;
                    } catch (NumberFormatException e) {
                        hexadecimal_input = "";
                        binary_input = "";
                        decimal_input = 0;
                        octal_input = "";
                    }
                } else {
                    hexadecimal_input = "";
                    binary_input = "";
                    decimal_input = 0;
                    octal_input = "";
                }
                binary.setText(binary_input);
                if (!decimal_input_valid){
                    decimal.setText("");
                } else {
                    decimal.setText(String.valueOf(decimal_input));
                }
                octal.setText(octal_input);
                hexadecimal.setText(hexadecimal_input);
            }
        });

        Intent history_intent = getIntent();
        if (history_intent != null) {
            String buffer = history_intent.getStringExtra("calc");
            input_text.setText(buffer);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}