package com.example.rightshoulderangel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.rightshoulderangel.affirmationNotification.CHANNEL;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;
    private TextView thePlus, closer;
    private LinearLayout linearLayout;
    private EditText editText;
    private AppCompatSpinner spinnerList;
    private ImageView imageClose;
    private ArrayList<String> affirmations;
    private RelativeLayout relativeLayout;
    private String toPlaceIntoArray, theString;
    private boolean previousDataSaved = false;
    //private NotificationManagerCompat notificationManagerCompat;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private boolean canRemoveTheView = false;


    private int randint;
    Integer a = 0;
    Integer b = 0;
    Integer c = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // so first we will show the popup to give the user an idea of what they should do in the app
        showPopUp();

        // then we will load the data so as to get access to an array list with permanent storage!
        loadData();

        // so here we will initialise the view's
        thePlus = (TextView) findViewById(R.id.pluser);
        linearLayout = (LinearLayout) findViewById(R.id.layoutList);

        relativeLayout = (RelativeLayout) findViewById(R.id.relolayo);

        // so here in the case were affirmations is not empty when the user opens the app we will send to the display function
        // so as to display the affirmations in the app
        if (!affirmations.isEmpty()) {
            display();
        }

        //notificationManagerCompat = NotificationManagerCompat.from(this);


        // this is to hide the keyboard more easily
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });


        // so in the case were we are re-opening the app we immediately get a section for writing our affirmation
        // this makes things slightly more user-friendly were the plus button does not need to be pressed fir the first affirmation
        if (a == 0 && b == 0) {
            // then so that this vent only happenns once we change the values of the intergers
            a += 1;
            b += 1;
            // then we add the view
            addView();
        }


        // so if the user re-opened the app when the arraylist was not empty we set the value of toPlaceIntoArray to the last values
        // this is to not confuse thePlus.setOnClickListener
        if (!previousDataSaved) {
            toPlaceIntoArray = editText.getText().toString();
        }


        // so now we will listen for a plus onclicker
        thePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this will get the current text so we can check if the user actually wrote something for their affirmation
                String withinTheOnClick = editText.getText().toString();
                // so here we will make the previous affirmation unrevisable
                editText.setKeyListener(null);
                // so if the affirmationwas not empty it is permanently saved to the arraylist and we create a new view
                if (withinTheOnClick != null && !TextUtils.isEmpty(withinTheOnClick)) {
                    affirmations.add(toPlaceIntoArray);
                    withSharedPreferences();
                    addView();
                } else {
                    // if the user deleted the empty affirmation but still has a full affirmation open we will also allow for a new affirmation
                    if (!TextUtils.isEmpty(toPlaceIntoArray)) {
                        addView();
                    } else {
                        // so if it is empty we inform the user that they need to input a value
                        Toast.makeText(MainActivity.this, "Fill in your value!\nCome on we know there is something:)", Toast.LENGTH_SHORT).show();
                    }
                }



                // so if there is already ap ending intent we will remiove it so as to not get overwhelmed with as many notifications as there are affirmations
                if (pendingIntent!=null) {
                    alarmManager.cancel(pendingIntent);
                }


                forTheNotification();


            }
        });

        forTheNotification();



    }

    public void forTheNotification() {
        if (!affirmations.isEmpty()) {
            // and here we will send to the class that allows for the creation of the notification
            Intent intenter = new Intent(MainActivity.this, affirmationsBroadcast.class);
            // we will pass the array list to said intent

            intenter.putExtra("The Array List", affirmations);
            // so here we will be passing on a unique id each times so as to obtain a unique id for the affirmations each time
            // this so as to have the arraylist update on each +
            intenter.putExtra("id",c);

            // then we create the pending intent
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, c, intenter, 0);

            // this is to set the notification to be logged over a specific interval
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


            long timeAt = System.currentTimeMillis();
            long minuteInMillis = 6000 * 10;

            // so now we will go about obtaining a random number
            long randomNumber = (long) Math.random() * 500;

            // and we will multiply it by the amount of minutes so as to obtain a random number for the amount of time until the answer is logged
            long finalNumber = randomNumber * minuteInMillis;



            // note that the RTC_WAKEUP assurres that the alarm goes off even when the mobile is on sleep
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, finalNumber, AlarmManager.INTERVAL_DAY, pendingIntent);

            c+=1;


        }
    }


    public void display() {
        // so we will iterate through the affirmations so as to gain access to each affirmation!
        for (String x: affirmations) {
            // then we will add the view for the affirmations
            final View ibView = getLayoutInflater().inflate(R.layout.row_add,null,false);

            editText = ibView.findViewById(R.id.ibdetails);
            imageClose = (ImageView) ibView.findViewById(R.id.cancel);

            // we will set the text fr each affirmation (as per the array list)
            editText.setText(x);

            // then we will set an onclicklistener to remove any affirmations on the delete button
            imageClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeView(ibView);
                }
            });

            // we add the view
            linearLayout.addView(ibView);

            // we signify here that the text is not empty so to not confuse things on the add a new affirmation click listener
            toPlaceIntoArray = x;
            // then we change the value of the boolean so that the value of the variable can be changed
            previousDataSaved = true;
            // we prevent the text from being revisable
            editText.setKeyListener(null);
        }
    }

    public void showPopUp() {
        // so first off we will create the dialog
        myDialog = new Dialog(this);
        // now we will set the cotentview as per the xml that we created
        myDialog.setContentView(R.layout.custompopup);
        // the we intitalise the delete button so as to be able to dismiss the pop-up when the user presses 'X'
        closer = (TextView) myDialog.findViewById(R.id.deleteTextView);
        closer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // and this would then be to show the diaog
        myDialog.show();
    }

    public void addView() {

        // so we create the affirmations section with an edittext and a imageClose
        final View ibView = getLayoutInflater().inflate(R.layout.row_add,null,false);

        editText = ibView.findViewById(R.id.ibdetails);
        imageClose = (ImageView) ibView.findViewById(R.id.cancel);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // this is to listen to the change of the value of the text at all times
                toPlaceIntoArray = editable.toString();
            }
        });


        // then if the imageclose is pressed we will delete this affirmation view
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(ibView);
            }
        });

        // and this is to add the view
        linearLayout.addView(ibView);



    }

    public void removeView(View v) {

        // so we get the value of the affirmation so that we will know what to delete from the array list
        EditText the = v.findViewById(R.id.ibdetails);
        theString = the.getText().toString();


        // so if there is nothing in the affirmations we will not allow for the deletion of the affirmation section
        if (affirmations.isEmpty()) {
            Toast.makeText(this, "Please tell us what is great about you\nWe know there is something:)", Toast.LENGTH_SHORT).show();
        // this prevents us from ever being left without an affirmation section
        } else if (TextUtils.isEmpty(theString)) {
            Toast.makeText(this, "Error\nYou cannot remove this part!", Toast.LENGTH_SHORT).show();
        } else {

            // so here we will create an alertdialog to assurre that the user wishes to remove the affirmation
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Are you sure that you want to remove the affirmation:");
            alert.setMessage(theString);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        // or else we will remove the deleted from both the affirmation section and the list view
                        affirmations.remove(theString);
                        // and here we will permanently store the data
                        withSharedPreferences();
                    } catch (Exception e) {
                    }
                    // so we sill now make it allowed to remove the view
                    canRemoveTheView = true;

                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // so in this case just do nothing
                }
            });

            alert.create().show();

            // so if it is the case that the view can be removed it is removed
            if (canRemoveTheView) {
                linearLayout.removeView(v);
            }

            // and now we will work on the notifications
            if (pendingIntent!=null) {
                alarmManager.cancel(pendingIntent);
            }

            forTheNotification();

        }

    }

    public void hideKeyboard() {
        // we get the current view and if it is clicked the keyboard is hidden
        View view = this.getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void withSharedPreferences() {
        // so firest we create the sharedpreferences and the gson
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        // so now we will pass on our arraylist
        String json = gson.toJson(affirmations);
        editor.putString("affirmations list",json);
        // so now we apply the constant
        editor.apply();
    }

    public void loadData() {
        // we permanently store through shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // now we create a Gson  so as to later on convert it based on the type
        Gson gson = new Gson();
        String json = sharedPreferences.getString("affirmations list",null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        affirmations = gson.fromJson(json,type);

        // and of course if the affirmations is null (when the app is first opened) we will initialise it
        if (affirmations == null) {
            affirmations = new ArrayList<>();
        }
    }


}