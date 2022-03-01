package com.kasmichael.project.data;

import android.provider.BaseColumns;

public class MessagesContract {

    public MessagesContract() {
    }

    public static final class MessagesTable implements BaseColumns {
        public final static String TABLE_NAME = "messages";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_TEXT = "text";
        public final static String COLUMN_TIME = "time";
    }
}
