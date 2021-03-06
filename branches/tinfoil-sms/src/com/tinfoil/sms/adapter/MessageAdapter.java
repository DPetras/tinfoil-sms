/** 
 * Copyright (C) 2011 Tinfoilhat
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tinfoil.sms.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tinfoil.sms.R;

public class MessageAdapter extends ArrayAdapter<String[]> {

	private static class MessageHolder
    {
        TextView c_name;
        TextView c_message;
        TextView c_date;
    }
	
    private final Context context;
    private final int layoutResourceId;
    private List<String[]> data = null;
    private int count;

    public MessageAdapter(final Context context, final int layoutResourceId, final List<String[]> data, final int count) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.count = count;
    }

    /**
     * Add new rows to be formated to the end of the list
     * 
     * @param data
     *            : List<String[]>
     */
    public void addData(final List<String[]> data)
    {
        for (int i = 0; i < data.size(); i++)
        {
            this.add(data.get(i));
        }
    }

    public void setCount(final int count)
    {
        this.count = count;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View row = convertView;
        MessageHolder holder = null;

        if (row == null)
        {
            final LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceId, parent, false);

            holder = new MessageHolder();
            holder.c_name = (TextView) row.findViewById(R.id.c_name);
            holder.c_message = (TextView) row.findViewById(R.id.c_message);
            holder.c_date = (TextView) row.findViewById(R.id.c_date);

            row.setTag(holder);
        }
        else
        {
            holder = (MessageHolder) row.getTag();
        }

        final String contact[] = this.data.get(position);
        holder.c_name.setText(contact[0]);
        holder.c_date.setText(contact[2]);
        holder.c_message.setText(contact[1]);

        if (position < this.count)
        {
            holder.c_name.setTypeface(null, Typeface.BOLD);
            holder.c_message.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.c_name.setTypeface(null, Typeface.NORMAL);
            holder.c_message.setTypeface(null, Typeface.NORMAL);
        }

        return row;
    }
}