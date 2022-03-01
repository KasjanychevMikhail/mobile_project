package com.kasmichael.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.kasmichael.project.R;
import com.kasmichael.project.item.Message;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends BaseAdapter implements Filterable {

    private List<Message> list = new ArrayList<>();

    public MessagesAdapter(List<Message> mList) {
        list.clear();
        list.addAll(mList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Message getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.titleItemMessageTextView),
                    (TextView) convertView.findViewById(R.id.timeItemMessageTexView)));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Message currMessage = list.get(position);
        final String itemTitle = currMessage.getTitle();
        final String itemTime = currMessage.getTime();

        holder.time.setText(itemTime);
        holder.title.setText(itemTitle);
        holder.time.setSelected(true);
        holder.title.setSelected(true);
        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView time;

        ViewHolder(TextView mTitle, TextView mTime) {
            title = mTitle;
            time = mTime;
        }
    }

    @Override
    public Filter getFilter() {
        MessagesAdapter.filterHere filterHere = new MessagesAdapter.filterHere();
        return filterHere;
    }

    public class filterHere extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults Result = new FilterResults();
            if (constraint.length() == 0 ) {
                Result.values = list;
                Result.count = list.size();
                return Result;
            }

            ArrayList<Message> Filtered_Names = new ArrayList<>();
            String filterString = constraint.toString().toLowerCase();
            Message filterableMessage;
            String filterableString;

            for (int i = 0; i < list.size(); i++) {
                filterableMessage = list.get(i);
                filterableString = filterableMessage.getTitle();
                if (filterableString.toLowerCase().contains(filterString)) {
                    Filtered_Names.add(filterableMessage);
                }
            }
            Result.values = Filtered_Names;
            Result.count = Filtered_Names.size();

            return Result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<Message>) results.values;
            notifyDataSetChanged();
        }
    }
}