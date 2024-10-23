//package com.fp.basecalc;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class HistoryActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_history);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}
package com.fp.basecalc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Reference to the main layout where you want to add the LinearLayouts
        LinearLayout mainLayout = findViewById(R.id.populate_history);

        // Generate three LinearLayouts with TextViews
        for (int i = 0; i < 100; i++) {
            LinearLayout layout = createLinearLayout(this, "Entry " + (i + 1));

            // Set click listener
            layout.setOnClickListener(v -> {
                // Use the ID to handle the click event
                int id = v.getId();
                Intent intent = new Intent(HistoryActivity.this, CalculateActivity.class);
                intent.putExtra("selected_id", id); // Pass the ID or some value
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });

            mainLayout.addView(layout);
        }
    }

    private LinearLayout createLinearLayout(Context context, String text) {
        // Create LinearLayout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(View.generateViewId()); // Generate a unique ID
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT // Allow height to wrap content
        ));
        linearLayout.setClickable(true);
        linearLayout.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);
        linearLayout.setFocusable(true);
        linearLayout.setOrientation(LinearLayout.VERTICAL); // Ensure the orientation is vertical

        // Create TextView
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(null, Typeface.BOLD);
        int margin = (int) getResources().getDimension(R.dimen.dp20); // Use a defined dimension for margins
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.setMargins(margin, 30, margin, 30);
        textView.setLayoutParams(layoutParams);

        // Add TextView to LinearLayout first
        linearLayout.addView(textView);

        // Create and add the line
        View br = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1 // Height of the line
        );
        br.setLayoutParams(params);
        br.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Add line to LinearLayout
        linearLayout.addView(br);

        return linearLayout;
    }
}
