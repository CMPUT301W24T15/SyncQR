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

/**
 * An adapter for displaying expandable lists of attendees.
 */
public class ViewAttendeeAdapter extends BaseExpandableListAdapter {
    /***
     * The context of the list view
     */
    private Context context;
    /**
     * The title of the list
     */
    private ArrayList<String> listName;
    /**
     * The subtitle of the list
     */
    private HashMap<String, ArrayList<String>> listContent;

    /**
     * Constructs a ViewAttendeeAdapter.
     * @param context The context of the list view.
     * @param listName The list containing lists names.
     * @param listContent The map containing child items for each group.
     */
    public ViewAttendeeAdapter(Context context, ArrayList<String> listName, HashMap<String, ArrayList<String>> listContent) {
        this.context = context;
        this.listName = listName;
        this.listContent = listContent;
    }

    /**
     * Return the size of the outer list
     * @return int
     */
    @Override
    public int getGroupCount() {
        return listName.size();
    }

    /**
     * Return the size of the inner list
     * @param groupPosition the position of the group for which the children
     *            count should be returned
     * @return int
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return listContent.get(listName.get(groupPosition)).size();
    }

    /**
     * Return the name of the list
     * @param groupPosition the position of the group
     * @return String
     */
    @Override
    public Object getGroup(int groupPosition) {
        return listName.get(groupPosition);
    }

    /**
     * Return the content of the list
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *            children in the group
     * @return String
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listContent.get(listName.get(groupPosition)).get(childPosition);
    }

    /**
     * Set the id of out list item as the same as its position
     * @param groupPosition the position of the group for which the ID is wanted
     * @return long
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * get the index of position in inner list
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *            the ID is wanted
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * indicates whether the child and group IDs are stable across changes to the underlying data
     * @return boolean
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the group--the Views for the
     * group's children will be fetched using getChildView(int, int, boolean, View, ViewGroup).
     * @param groupPosition the position of the group for which the View is
     *            returned
     * @param isExpanded whether the group is expanded or collapsed
     * @param convertView the old view to reuse, if possible. You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view. It is not
     *            guaranteed that the convertView will have been previously
     *            created by
     *            {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent the parent that this view will eventually be attached to
     * @return View
     */
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

    /**
     * Gets a View that displays the data for the given child within the given group.
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *            returned) within the group
     * @param isLastChild Whether the child is the last child within the group
     * @param convertView the old view to reuse, if possible. You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view. It is not
     *            guaranteed that the convertView will have been previously
     *            created by
     *            {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent the parent that this view will eventually be attached to
     * @return View
     */
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

    /**
     * Whether the child at the specified position is selectable.
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return boolean
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
