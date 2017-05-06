/*package com.jello.zero;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kimpham on 5/2/17.
 */
/*
public class CommentListViewAdapter extends ArrayAdapter<Comment> implements View.OnClickListener{
    private List<Comment> commentList;
    Context context;

    private static class ViewHolder {
        private TextView content;
        private TextView author;
    }

    public CommentListViewAdapter(List<Comment> data, Context context) {
        super(context, R.layout.comment_row, data);
        this.commentList = data;
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Comment dataModel=(Comment) object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_row, parent, false);
        }

        TextView authorField = (TextView) convertView.findViewById(R.id.comment_author_field);
        TextView commentField = (TextView) convertView.findViewById(R.id.comment_content_field);

        authorField.setText(comment.getAuthor());
        commentField.setText(comment.getContent());

        return convertView;
    }



}
*/