package com.example.calculator;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<CalculationHistory> historyList;
    private Context context;

    public HistoryAdapter(Context context, List<CalculationHistory> historyList) {
        this.context = context; // Initialize context
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new HistoryViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        CalculationHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView expressionTextView;
        private TextView resultTextView;

        HistoryViewHolder(View itemView, Context context) {
            super(itemView);
            expressionTextView = itemView.findViewById(android.R.id.text1);
            resultTextView = itemView.findViewById(android.R.id.text2);

            expressionTextView.setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.material_on_primary_emphasis_high_type));
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true);
            resultTextView.setTextColor(typedValue.data);
        }

        void bind(CalculationHistory calculationHistory) {
            expressionTextView.setText(calculationHistory.getExpression());
            resultTextView.setText("=" + calculationHistory.getResult());
        }
    }
}
