package com.pkasemer.MyFamlinkApp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkasemer.MyFamlinkApp.Models.Case;
import com.pkasemer.MyFamlinkApp.R;

import java.util.List;

public class NameAdapter extends ArrayAdapter<Case> {

    //storing all the names in the list
    private List<Case> aCases;

    //context object
    private Context context;

    //constructor
    public NameAdapter(Context context, int resource, List<Case> aCases) {
        super(context, resource, aCases);
        this.context = context;
        this.aCases = aCases;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.names, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView child_description = (TextView) listViewItem.findViewById(R.id.child_description);
        ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        Case aCase = aCases.get(position);

        //setting the name to textview
        textViewName.setText(aCase.getName());
        child_description.setText(aCase.getDescription());


        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (aCase.getStatus() == 0)
            imageViewStatus.setBackgroundResource(R.drawable.ic_offline);
        else
            imageViewStatus.setBackgroundResource(R.drawable.ic_success);

        return listViewItem;
    }

}