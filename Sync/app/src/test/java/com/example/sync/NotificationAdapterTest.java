package com.example.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for the NotificationAdapter class.
 */
public class NotificationAdapterTest {

    private Context context;
    private List<Notification> notifications;
    private NotificationAdapter adapter;

    /**
     * Setup method to initialize test data.
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        notifications = new ArrayList<>();
        notifications.add(new Notification("1", "Title 1", "Message 1"));
        notifications.add(new Notification("2", "Title 2", "Message 2"));
        adapter = new NotificationAdapter(context, notifications);
    }

    /**
     * Test case for getItemCount method.
     * Verifies that the adapter returns the correct number of items.
     */
    @Test
    public void testGetItemCount() {
        assertEquals(notifications.size(), adapter.getItemCount());
    }

    /**
     * Test case for onCreateViewHolder method.
     * Verifies that the adapter creates a ViewHolder with the correct layout.
     */
    @Test
    public void testOnCreateViewHolder() {
        ViewGroup parent = new RecyclerView(context);
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
        assertTrue(viewHolder instanceof NotificationAdapter.ViewHolder);

        View itemView = viewHolder.itemView;
        assertNotNull(itemView);
        assertEquals(R.layout.notification_item, itemView.getId());
    }

    /**
     * Test case for onBindViewHolder method.
     * Verifies that the adapter binds data correctly to the ViewHolder.
     */
    @Test
    public void testOnBindViewHolder() {
        ViewGroup parent = new RecyclerView(context);
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);

        adapter.onBindViewHolder((NotificationAdapter.ViewHolder) viewHolder, 0);

        TextView titleTextView = viewHolder.itemView.findViewById(R.id.notificationTitle);
        TextView messageTextView = viewHolder.itemView.findViewById(R.id.notificationMessage);

        assertEquals("Title 1", titleTextView.getText().toString());
        assertEquals("Message 1", messageTextView.getText().toString());
    }
}

