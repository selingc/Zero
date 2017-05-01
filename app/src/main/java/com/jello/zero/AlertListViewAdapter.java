package com.jello.zero;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kimpham on 4/30/17.
 */

public class AlertListViewAdapter extends ArrayAdapter<Alert> implements View.OnClickListener{
    private List<Alert> alertList;
    Context context;

    private static class ViewHolder {
        TextView alertString;
      //  Button confirmButton;
    }

    public AlertListViewAdapter(List<Alert> data, Context context) {
        super(context, R.layout.alert_row, data);
        this.alertList = data;
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Alert dataModel=(Alert)object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Alert alert = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.alert_row, parent, false);
            viewHolder.alertString = (TextView) convertView.findViewById(R.id.alert_string);
          //  viewHolder.confirmButton = (Button) convertView.findViewById(R.id.confirm_button);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.alertString.setText(alert.toString());

        // Return the completed view to render on screen
        return convertView;
    }


}
