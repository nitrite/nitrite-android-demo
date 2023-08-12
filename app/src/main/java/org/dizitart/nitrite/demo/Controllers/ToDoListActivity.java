package org.dizitart.nitrite.demo.Controllers;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.dizitart.nitrite.demo.Model.TodoItem;
import org.dizitart.nitrite.demo.R;
import org.dizitart.nitrite.demo.Toolbox.DataBaseManager;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ToDoListActivity extends AppCompatActivity {

    DataBaseManager manager;
    ListView listView;
    CustomAdapter customAdapter;
    TextView activityTitle;
    EditText addTask;
    int currentBiggerId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        //naming the activity
        activityTitle = (TextView) findViewById(R.id.activityTitle);
        activityTitle.setText(getIntent().getStringExtra("listName"));
        //getting list color
        String color = getIntent().getStringExtra("listColor");

        addTask = (EditText) findViewById(R.id.editTextAddTask);

        listView = (ListView) findViewById(R.id.listTodo);
        manager = new DataBaseManager(activityTitle.getText().toString());
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.e("Item Long Click", "Removing the item");
            manager.removeTask(getApplicationContext(), (customAdapter.todoList.get(position)).getId());
            try {
                printDB(true);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        });
        try {
            printDB(true);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addTask(View v) {
        Runnable runnable = () -> {
            currentBiggerId = manager.getCurrentBiggestId(getApplicationContext());
            int randomInt = new Random().nextInt(3);
            String color = "";
            switch (randomInt) {
                case 0:
                    color = "blue";
                    break;
                case 1:
                    color = "green";
                    break;
                case 2:
                    color = "violet";
                    break;
            }
            manager.addTask(getApplicationContext(), new Random().nextInt(), false, addTask.getText().toString(), color);
            try {
                printDB(true);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    public void printDB(boolean updateFromDb) throws ExecutionException, InterruptedException {
        if (updateFromDb) {
            customAdapter = new CustomAdapter(manager.readFromDB(getApplicationContext()));
            listView.setAdapter(customAdapter);
        }
        customAdapter.notifyDataSetChanged();
        currentBiggerId = manager.getCurrentBiggestId(getApplicationContext());
        addTask.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Toast.makeText(this, "Nitrite Database Demo", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            printDB(true);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(customAdapter.todoList.size());
    }

    public class CustomAdapter extends BaseAdapter {
        List<TodoItem> todoList;

        CustomAdapter(List<TodoItem> todoList) {
            this.todoList = todoList;
        }

        @Override
        public int getCount() {
            return todoList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.task_todo_item, null);

            final TextView title = view.findViewById(R.id.todoName);
            title.setText(todoList.get(i).getTaskName());
            CheckBox button = view.findViewById(R.id.checkButton);
            button.setChecked(todoList.get(i).getStatus());

            //region the fucking shit i do to get procedurally generated stuff -.-
            int[][] states = new int[][]{
                    new int[]{-android.R.attr.state_checked}, //disabled
                    new int[]{android.R.attr.state_checked} //enabled
            };
            @SuppressLint("ResourceType") int[] colorBlue = new int[]{Color.parseColor(getString(R.color.colorBlue)),
                    Color.parseColor(getString(R.color.colorBlue))};
            @SuppressLint("ResourceType") int[] colorGreen = new int[]{Color.parseColor(getString(R.color.colorGreen)),
                    Color.parseColor(getString(R.color.colorGreen))};
            @SuppressLint("ResourceType") int[] colorViolet = new int[]{Color.parseColor(getString(R.color.colorViolet)),
                    Color.parseColor(getString(R.color.colorViolet))};
            //endregion

            //region setting UI colors
            RelativeLayout layout = view.findViewById(R.id.itemBackground);
            String color = todoList.get(i).getColor();
            final int taskId = todoList.get(i).getId();
            final String taskName = todoList.get(i).getTaskName();
            if (color.matches("blue")) {
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.list_item_background_blue));
                button.setButtonTintList(new ColorStateList(states, colorBlue));
            } else if (color.matches("green")) {
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.list_item_background_green));
                button.setButtonTintList(new ColorStateList(states, colorGreen));
            } else {
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.list_item_background_violet));
                button.setButtonTintList(new ColorStateList(states, colorViolet));
            }
            //endregion
            button.setOnCheckedChangeListener((compoundButton, b) -> itemChecked(b, taskId, taskName));

            return view;
        }

        void itemChecked(final boolean taskStatus, final int taskId, final String taskName) {
            Runnable runnable = () -> manager.updateTask(getApplicationContext(), taskStatus, taskId, taskName);
            runnable.run();
        }
    }
}
