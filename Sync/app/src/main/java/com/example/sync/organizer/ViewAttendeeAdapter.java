package com.example.sync.organizer;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.example.sync.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

// Codes are written by following the Youtube instruction:
//  Android WorldClub, Apr 25, 2020, Youtube
//  https://www.youtube.com/watch?v=cxVBXpw4Fc8
public class ViewAttendeeAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> listName;
    private HashMap<String, ArrayList<String>> listContent;

    public ViewAttendeeAdapter(Context context, ArrayList<String> listName, HashMap<String, ArrayList<String>> listContent) {
        this.context = context;
        this.listName = listName;
        this.listContent = listContent;
    }

    @Override
    public int getGroupCount() {
        return listName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listContent.get(listName.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listContent.get(listName.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String name = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.parent_list, parent, false);
        }

        TextView listNameView= convertView.findViewById(R.id.parent);
        listNameView.setText(name);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String data = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_list, parent, false);
        }

        TextView listNameView= convertView.findViewById(R.id.child);
        listNameView.setText(data);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
