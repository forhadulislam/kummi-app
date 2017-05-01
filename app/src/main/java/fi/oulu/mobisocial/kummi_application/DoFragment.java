package fi.oulu.mobisocial.kummi_application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoFragment extends Fragment implements View.OnClickListener {

    public DoFragment(){
        // Required empty public constructor

    }

    private static final String TAG = "TASKERI MAIN";
    private static final int PICK_LOCATION_REQUEST = 1;  // The request code
    public static final String ACTION_TASK_PROXIMITY = "ACTION_TASK_PROXIMITY";
    public static final String TASK_ID = "task_id";

    private ListView listView;
    public static ArrayList<Task> arrayofTask = new ArrayList<Task>();
    private Task task;
    public TaskAdapter adapter;
    private DatabaseHandler db;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private AlertDialogManager alert = new AlertDialogManager();
    LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_do, container, false);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("TASK_DELETED"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(locationReceiver, new IntentFilter(ACTION_TASK_PROXIMITY));
        // Inflate the layout for this fragment
        pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        task = new Task();
        db = new DatabaseHandler(getActivity());
        db.deleteOldTasks();

        // Linking adapter

        adapter = new TaskAdapter(getActivity(), arrayofTask);
        listView = (ListView) view.findViewById(R.id.task_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Deleting task" );
                db.deleteTask(arrayofTask.get(position).getID());
                updateTaskList();
            }
        });
        // Reading all tasks
        updateTaskList();
        FloatingActionButton add_task = (FloatingActionButton) view.findViewById (R.id.add_task);
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.init();
                newTask();
            }
        });
        return view;

    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);



    }

    @Override
    public void onDetach(){
        super.onDetach();
        arrayofTask.clear();

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Task deleted broadcastreceiver.");

            updateTaskList();
        }
    };
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        arrayofTask.clear();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationReceiver);
        db.close();

    }

    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            db = new DatabaseHandler(context);
            if (intent.getAction().equals(ACTION_TASK_PROXIMITY) && intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
                //entered geofence
                long id = intent.getLongExtra(DoFragment.TASK_ID, 0);
                if (id != 0) {
                    Log.d(TAG, String.format("Location broadcastreceiver, id: %d", id));
                    Task t = db.getTask(id);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = getNotification(t.getInfo(), id, Calendar.getInstance().getTimeInMillis());
                    notificationManager.notify((int) id, notification);
                }
            } else {
                //exited geofence
            }
        }
    };

    private void updateTaskList() {
        Log.d(TAG, "in updateTaskList");
        List<Task> tasks = db.getAllTasks();
        arrayofTask.clear();
        for (Task t : tasks) {
            arrayofTask.add(t);
        }
        adapter.notifyDataSetChanged();
    }
    public void newTask() {
        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Create new task");
        alertDialog.setMessage("Task info:");
        alertDialog.setView(input);

        // Set up the buttons
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String info = "";
                info = input.getText().toString();
                task.setInfo(info);
                askType();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    private void askType() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Location or time alert?");

        // Set up the buttons
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Time", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askDate();
            }
        });
        alertDialog.show();
    }

    private void askDate() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                task.setDate(selectedyear, selectedmonth, selectedday);
                askTime();
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    private void askTime() {
        TimePickerDialog mTimePicker;
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                task.setTime(selectedHour, selectedMinute);
                addTimeTask();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void addTimeTask() {
        Log.d(TAG, String.format("in addTimeTask"));
        long id = db.addTask(task);
        updateTaskList();
        long willHappen = task.getCalendar().getTimeInMillis();
        long diff = willHappen - Calendar.getInstance().getTimeInMillis();
        scheduleNotification(getNotification(task.getInfo(), id, willHappen), diff, id);
        alert.showAlertDialog(getActivity(), "Task added", "Task added succesfully", true);
    }
    private void scheduleNotification(Notification notification, long delay, long id) {
        Log.d(TAG, "in scheduleNotification");
        Intent notificationIntent = new Intent(getActivity(), Notificationer.class);
        notificationIntent.putExtra(Notificationer.NOTIFICATION_ID, (int)id);
        notificationIntent.putExtra(Notificationer.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int)id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content, long id, long willHappen) {
        Log.d(TAG, "in getNotification with id: " + Long.toString(id));
        Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setContentTitle("Tasker");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
        builder.setAutoCancel(true);
        builder.setWhen(willHappen);
        Intent resultIntent = new Intent(getActivity(), NotificationClicked.class);
        resultIntent.putExtra(NotificationClicked.ID, id);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(getActivity(),
                (int)id,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }


    @Override
    public void onClick(View v) {

    }
}




