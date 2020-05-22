package com.example.dailyquote;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static RecyclerAdapter recyclerAdapter;
    private TextView quoteTextView, subQuoteTextView;
    private static List<String> quoteList, authorList;
    private String userInput;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Quotes");
        setSupportActionBar(toolbar);

        //Open Notification Channel
        createNotificationChannel();

        //Instantiate Quote Array Lists
        quoteList = new ArrayList<>();
        authorList = new ArrayList<>();

        //Load Quotes into the ArrayLists
        loadPreferences();

        //Attach RecyclerView and setup RecyclerAdapter
        recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerAdapter = new RecyclerAdapter(quoteList, authorList);

        //Set the RecyclerView layout to LinearLayout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Attach RecyclerAdapter to our RecyclerView
        recyclerView.setAdapter(recyclerAdapter);

        //Create and Add a Divider between our rows
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //Attach our fab and set a click listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInput();
            }
        });

        //Setup ItemTouchHelper and attach to RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Setup our repeating alarm
        setAlarm();
    }

    String deletedQuote = null;
    String deletedAuthor = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            deletedQuote = quoteList.get(position);
            deletedAuthor = authorList.get(position);
            quoteList.remove(position);
            authorList.remove(position);
            recyclerAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deletedQuote + " Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            quoteList.add(position, deletedQuote);
                            authorList.add(position, deletedAuthor);
                            recyclerAdapter.notifyItemInserted(position);
                        }
                    }).show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent))
                    .addSwipeRightActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void getUserInput(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.alert_dialog, null);

        final Button okButton = view.findViewById(R.id.okButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        final EditText quoteInput = view.findViewById(R.id.quoteInput);
        final EditText authorInput = view.findViewById(R.id.authorInput);

        okButton.setEnabled(false);

        quoteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    okButton.setEnabled(false);
                }else{
                    okButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quoteList.add(quoteInput.getText().toString());
                if(authorInput.getText().toString().length() == 0) {
                    authorList.add("Unknown");
                }else{
                    authorList.add(authorInput.getText().toString());
                }
                recyclerAdapter.notifyItemInserted(quoteList.lastIndexOf(quoteInput.getText().toString()));
                alertDialog.cancel();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences =   getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        Iterator<String> quoteListIterator = quoteList.iterator();
        Iterator<String> authorListIterator = authorList.iterator();

        while(quoteListIterator.hasNext()){
            String quote = quoteListIterator.next();
            String author = authorListIterator.next();
            editor.putString(quote, author);
        }
        editor.commit();
    }

    public void loadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()){
            quoteList.add(entry.getKey());
            authorList.add(entry.getValue().toString());
        }
    }

    private void setAlarm(){
        //Setup the AlarmManager and Intents
        alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //Get current time in a calendar
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendarNow.setTimeInMillis(System.currentTimeMillis());
        //Set Alarm time to 8:00 a.m
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);

        //If time has already passed, add a day so the alarm doesn't fire immediately
        if(calendar.before(calendarNow)){
            calendar.add(Calendar.DATE, 1);
        }

        //Set Alarm to repeat once a day at the specified time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("${context.packageName}-$name", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static List<String> getQuoteList(){
        return quoteList;
    }
}
