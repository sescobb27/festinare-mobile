package com.festinare.discount.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.festinare.discount.R;
import com.festinare.discount.models.Category;

import java.util.List;


public class CategoriesAdapter extends ArrayAdapter<Category> implements CompoundButton.OnCheckedChangeListener{

    public SparseBooleanArray mCheckStates;

    /** Activity that contains the RecyclerView */
    private final Activity context;
    /** Data that is going to be displayed in the RecyclerView */
    private List<Category> data;

    /** Constructor */
    public CategoriesAdapter(Activity context,List<Category> data) {
        super(context,R.layout.item_category,data);
        this.context=context;
        this.data=data;
        mCheckStates = new SparseBooleanArray(data.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.item_category, parent,false);

            holder = new ViewHolder();
            holder.cbCategory = (CheckBox) convertView.findViewById(R.id.cbCategory);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cbCategory.setText(data.get(position).getName());
        holder.cbCategory.setTag(position);
        holder.cbCategory.setChecked(mCheckStates.get(position, false));
        holder.cbCategory.setOnCheckedChangeListener(this);

        return convertView;

    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {
        Log.d("check ",Integer.toString((Integer) buttonView.getTag()));

        mCheckStates.put((Integer) buttonView.getTag(), isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));
    }

    /** This class defines the elements of each item */
    class ViewHolder{

        CheckBox cbCategory;
    }
}
