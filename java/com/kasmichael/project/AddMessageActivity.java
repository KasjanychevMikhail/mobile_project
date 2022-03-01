package com.kasmichael.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class AddMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView textViewToolbar;

    private EditText titleEditText;
    private EditText textEditText;
    private Button encodeBtn;
    private TextView codeTextView;

    private final static String MAIN_KEY = "ОАЛВДЖПХЭФЫЙМОИБПДВШКГЫОЛ";

    private final static char[] alphabetRU = { 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У',
            'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я' };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewToolbar = (TextView) toolbar.findViewById(R.id.toolbarTextView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewToolbar.setText(R.string.all_messages);

        titleEditText = (EditText) findViewById(R.id.titleMessageEditText);
        textEditText = (EditText) findViewById(R.id.textMessageEditText);
        codeTextView = (TextView) findViewById(R.id.codeMessageTextView);
        encodeBtn = (Button) findViewById(R.id.encodeMessageButton);
        encodeBtn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.encodeMessageButton:
                codeTextView.setText(encodeMessage(titleEditText.getText().toString(),
                        textEditText.getText().toString()));
                break;
            default:
                break;
        }
    }

    private String encodeMessage(String title, String text) {
        Date date = new Date();
        long time = date.getTime();
        String input = title + "=" + text + "#" + time;
        String res = "";
        String tmpkey = MAIN_KEY;
        while (tmpkey.length() < input.length()) {
            tmpkey += tmpkey;
        }
        for (int i = 0; i < input.length(); i++) {
            char bigSym = (String.valueOf(input.charAt(i)).toUpperCase()).charAt(0);
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
                                if (j + k >= alphabetRU.length) {
                                    if (input.charAt(i) == bigSym) {
                                        res += alphabetRU[j + k - alphabetRU.length];
                                    } else {
                                        res += (alphabetRU[j + k - alphabetRU.length] + "").toLowerCase();
                                    }
                                } else {
                                    if (input.charAt(i) == bigSym) {
                                        res += alphabetRU[j + k];
                                    } else {
                                        res += (alphabetRU[j + k] + "").toLowerCase();
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
}