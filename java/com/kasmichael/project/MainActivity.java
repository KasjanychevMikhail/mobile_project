package com.kasmichael.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kasmichael.project.adapter.MessagesAdapter;
import com.kasmichael.project.data.MessagesContract;
import com.kasmichael.project.data.MessagesDBHelper;
import com.kasmichael.project.dialog.ConfirmDeletingMessage;
import com.kasmichael.project.dialog.FullTextMessage;
import com.kasmichael.project.item.Message;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView textViewToolbar;

    private TextView countOfMessagesTextView;
    private LinearLayout panelLayout;
    private ListView messagesListView;
    private FloatingActionButton fab;

    private TextView currentTitleTextView;
    private Button deleteMessageBtn;
    private Button fullTextBtn;
    private Button cancelPanelBtn;
    private Button decodeMessageBtn;

    private MessagesDBHelper dbHelper;

    private ArrayList<Message> messages = new ArrayList<>();

    private SharedPreferences preferences;

    private int currID = -1;
    private int currPosition = -1;

    private int typeOfSort = 0;

    private final static String MAIN_KEY = "ОАЛВДЖПХЭФЫЙМОИБПДВШКГЫОЛ";
    private final static String TYPE_OF_SORT = "type_of_sort";

    private final static char[] alphabetRU = { 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У',
            'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я' };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewToolbar = (TextView) toolbar.findViewById(R.id.toolbarTextView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textViewToolbar.setText(R.string.all_messages);

        countOfMessagesTextView = (TextView) findViewById(R.id.countOfMessagesTextView);
        panelLayout = (LinearLayout) findViewById(R.id.panelLinearLayout);
        fab = (FloatingActionButton) findViewById(R.id.messageFloatingActionButton);

        currentTitleTextView = (TextView) findViewById(R.id.currentTitleMessageTextView);
        deleteMessageBtn = (Button) findViewById(R.id.deleteMessageButton);
        fullTextBtn = (Button) findViewById(R.id.fullTextMessageButton);
        cancelPanelBtn = (Button) findViewById(R.id.cancelPanelButton);
        decodeMessageBtn = (Button) findViewById(R.id.decodeMessageButton);
        fab.setOnClickListener(this);
        deleteMessageBtn.setOnClickListener(this);
        fullTextBtn.setOnClickListener(this);
        cancelPanelBtn.setOnClickListener(this);
        decodeMessageBtn.setOnClickListener(this);

        messagesListView = (ListView) findViewById(R.id.messagesListView);

        dbHelper = new MessagesDBHelper(this);

        preferences = getPreferences(MODE_PRIVATE);
        typeOfSort = preferences.getInt(TYPE_OF_SORT, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_messages);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Поиск");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final MessagesAdapter messagesAdapter = new MessagesAdapter(messages);

                messagesAdapter.getFilter().filter(newText);
                messagesListView.setAdapter(messagesAdapter);
                messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Message currMessage = messagesAdapter.getItem(position);
                        int cPosition = messages.indexOf(currMessage);

                        currID = messages.get(cPosition).getId();
                        currPosition = cPosition;
                        panelLayout.setVisibility(View.VISIBLE);
                        currentTitleTextView.setText(messages.get(cPosition).getTitle());
                        currentTitleTextView.setSelected(true);
                    }
                });

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_time_new:
                typeOfSort = 0;
                SharedPreferences.Editor ed = preferences.edit();
                ed.putInt(TYPE_OF_SORT, typeOfSort);
                ed.commit();
                onResume();
                return true;
            case R.id.action_sort_by_time_old:
                typeOfSort = 1;
                SharedPreferences.Editor ed1 = preferences.edit();
                ed1.putInt(TYPE_OF_SORT, typeOfSort);
                ed1.commit();
                onResume();
                return true;
            case R.id.action_sort_by_name:
                typeOfSort = 2;
                SharedPreferences.Editor ed2 = preferences.edit();
                ed2.putInt(TYPE_OF_SORT, typeOfSort);
                ed2.commit();
                onResume();
                return true;
            case R.id.action_delete_all:
                deleteAllMessages();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messageFloatingActionButton:
                Intent intent = new Intent(this, AddMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.decodeMessageButton:
                addMessage();
                break;
            case R.id.deleteMessageButton:
                ConfirmDeletingMessage confirmDeletingMessage = new ConfirmDeletingMessage();
                confirmDeletingMessage.show(getSupportFragmentManager(), "confirm_deleting_message");
                break;
            case R.id.fullTextMessageButton:
                FullTextMessage fullTextMessage = new FullTextMessage(messages.get(currPosition).getTitle(),
                        messages.get(currPosition).getText());
                fullTextMessage.show(getSupportFragmentManager(), "full_text_message");
                break;
            case R.id.cancelPanelButton:
                clearPanel();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addMessagesFromDB(typeOfSort);
        countOfMessagesTextView.setText(String.valueOf(messages.size()));

        MessagesAdapter adapter = new MessagesAdapter(messages);
        messagesListView.setAdapter(adapter);
        messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currID = messages.get(position).getId();
                currPosition = position;
                panelLayout.setVisibility(View.VISIBLE);
                currentTitleTextView.setText(messages.get(position).getTitle());
                currentTitleTextView.setSelected(true);
            }
        });
    }

    public void deleteMessage() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tmp = MessagesContract.MessagesTable._ID + " = " + currID;
        db.delete(MessagesContract.MessagesTable.TABLE_NAME, tmp, null);

        clearPanel();
        onResume();
    }

    private void clearPanel() {
        currentTitleTextView.setText("");
        currPosition = -1;
        currID = -1;
        panelLayout.setVisibility(View.GONE);
    }

    private String decodeMessage(String input) {
        String res = "";
        if (input.equals(""))
            return res;
        String tmpkey = MAIN_KEY;
        while (tmpkey.length() < input.length()) {
            tmpkey += tmpkey;
        }
        for (int i = 0; i < input.length(); i++) {
            char bigSym = ((input.charAt(i) + "").toUpperCase()).charAt(0);
            if (bigSym == 'А' || bigSym == 'Б' || bigSym == 'В' || bigSym == 'Г' || bigSym == 'Д' || bigSym == 'Е' ||
                    bigSym == 'Ё' || bigSym == 'Ж' || bigSym == 'З' || bigSym == 'И' || bigSym == 'Й' || bigSym == 'К' ||
                    bigSym == 'Л' || bigSym == 'М' || bigSym == 'Н' || bigSym == 'О' || bigSym == 'П' || bigSym == 'Р' ||
                    bigSym == 'С' || bigSym == 'Т' || bigSym == 'У' || bigSym == 'Ф' || bigSym == 'Х' || bigSym == 'Ц' ||
                    bigSym == 'Ч' || bigSym == 'Ш' || bigSym == 'Щ' || bigSym == 'Ъ' || bigSym == 'Ы' || bigSym == 'Ь' ||
                    bigSym == 'Э' || bigSym == 'Ю' || bigSym == 'Я') {
                for (int j = 0; j < alphabetRU.length; j++) {
                    if (bigSym == alphabetRU[j]) {
                        for (int k = 0; k < alphabetRU.length; k++) {
                            if (tmpkey.charAt(i) == alphabetRU[k]) {
                                if (j - k < 0) {
                                    if (bigSym == input.charAt(i)) {
                                        res += alphabetRU[alphabetRU.length + (j - k)];
                                    } else {
                                        res += (alphabetRU[alphabetRU.length + (j - k)] + "").toLowerCase();
                                    }
                                } else {
                                    if (bigSym == input.charAt(i)) {
                                        res += alphabetRU[j - k];
                                    } else {
                                        res += (alphabetRU[j - k] + "").toLowerCase();
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                res += input.charAt(i);
            }
        }
        return res;
    }

    private void addMessage() {
        LayoutInflater li = LayoutInflater.from(this);
        View addMessageView = li.inflate(R.layout.dialog_add_message, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(addMessageView);
        final EditText codeInput = (EditText) addMessageView.findViewById(R.id.addTaskEditText);

        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputCode = codeInput.getText().toString();
                        addingMessage(inputCode);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    private void addingMessage(String inputCode) {
        String code = decodeMessage(inputCode);
        String title = separationTitle(code);
        String text = separationText(code);
        long time = separationTime(code);

        if (title.equals("") || text.equals("") || time == 0L) {
            Toast.makeText(this, "Некорректные данные.", Toast.LENGTH_SHORT).show();
            return;
        }

        addMessageIntoDB(title, text, time);
        clearPanel();
    }

    private void addMessageIntoDB(String title, String text, long time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesContract.MessagesTable.COLUMN_TITLE, title);
        values.put(MessagesContract.MessagesTable.COLUMN_TEXT, text);
        values.put(MessagesContract.MessagesTable.COLUMN_TIME, time);

        long newRowID = db.insert(MessagesContract.MessagesTable.TABLE_NAME, null, values);

        if(newRowID == -1) {
            Toast.makeText(this, "Ошибка при добавлении задания!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Сообщение успешно добавлено.", Toast.LENGTH_SHORT).show();

        onResume();
    }

    private String separationTitle(String input) {
        String res = "";
        int i = 0;
        while (i < input.length() && input.charAt(i) != '=') {
            res += input.charAt(i);
            i++;
        }
        return res;
    }

    private String separationText(String input) {
        String res = "";
        int i = 0;
        while (i < input.length() && input.charAt(i) != '=') {
            i++;
        }
        i++;
        while (i < input.length() && input.charAt(i) != '#') {
            res += input.charAt(i);
            i++;
        }
        return res;
    }

    private long separationTime(String input) {
        long res = 0L;
        String tmp = "";
        int i = 0;
        while (i < input.length() && input.charAt(i) != '=') {
            i++;
        }
        i++;
        while (i < input.length() && input.charAt(i) != '#') {
            i++;
        }
        i++;
        while (i < input.length()) {
            tmp += input.charAt(i);
            i++;
        }
        if (tmp.equals(""))
            return res;
        res = Long.parseLong(tmp);
        return res;
    }

    private void deleteAllMessages() {
        messages.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int clearCount = db.delete(MessagesContract.MessagesTable.TABLE_NAME, null, null);
        Toast.makeText(this, "Удалено " + clearCount + " сообщений.", Toast.LENGTH_SHORT).show();
        clearPanel();

        onResume();
    }

    private void addMessagesFromDB(int type) {
        messages.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MessagesContract.MessagesTable._ID,
                MessagesContract.MessagesTable.COLUMN_TITLE,
                MessagesContract.MessagesTable.COLUMN_TEXT,
                MessagesContract.MessagesTable.COLUMN_TIME };
        Cursor cursor;

        switch (type) {
            case 1:
                cursor = db.query(MessagesContract.MessagesTable.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        MessagesContract.MessagesTable.COLUMN_TIME + " ASC");
                break;
            case 2:
                cursor = db.query(MessagesContract.MessagesTable.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        MessagesContract.MessagesTable.COLUMN_TITLE + " ASC");
                break;
            default:
                cursor = db.query(MessagesContract.MessagesTable.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        MessagesContract.MessagesTable.COLUMN_TIME + " DESC");
                break;
        }

        try {
            if (cursor.moveToFirst()) {
                int idColIndex = cursor.getColumnIndex(MessagesContract.MessagesTable._ID);
                int titleColIndex = cursor.getColumnIndex(MessagesContract.MessagesTable.COLUMN_TITLE);
                int textColIndex = cursor.getColumnIndex(MessagesContract.MessagesTable.COLUMN_TEXT);
                int timeColIndex = cursor.getColumnIndex(MessagesContract.MessagesTable.COLUMN_TIME);

                String title, text, timeS;
                long time;
                int id;
                do {
                    title = cursor.getString(titleColIndex);
                    text = cursor.getString(textColIndex);
                    time = cursor.getLong(timeColIndex);
                    id = cursor.getInt(idColIndex);
                    timeS = getDate(time);

                    messages.add(new Message(title, text, timeS, id));
                } while (cursor.moveToNext());
            } else cursor.close();
        } finally {
            cursor.close();
        }


    }

    private String getDate(long time) {
        Date date = new Date(time);
        return date.toString();
    }
}