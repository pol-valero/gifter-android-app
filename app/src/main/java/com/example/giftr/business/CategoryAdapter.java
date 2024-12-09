package com.example.giftr.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.giftr.business.entities.Category;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    public CategoryAdapter(Context context, List<Category> categoryList) {
        super(context, 0, categoryList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create or reuse a view for the item at the specified position
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        // Get the current Category object
        Category category = getItem(position);

        // Set the desired text for the Spinner item
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(category.getName());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Create or reuse a view for the dropdown item at the specified position
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        // Get the current Category object
        Category category = getItem(position);

        // Set the desired text for the dropdown item
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(category.getName());

        return view;
    }
}