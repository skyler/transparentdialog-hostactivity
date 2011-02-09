/*
   Copyright 2011 Skyler Slade

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package us.skylerslade.android.dialog.transparenthost;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

class ToDoList
{
    static class ToDoItem implements Parcelable
    {
        public static final String EXTRA_TODO = "toDoExtra";

        public static final Parcelable.Creator<ToDoItem> CREATOR
                = new Parcelable.Creator<ToDoItem>() {
            @Override
            public ToDoItem createFromParcel(Parcel in)
            {
                return new ToDoItem(in);
            }

            @Override
            public ToDoItem[] newArray(int size)
            {
                return new ToDoItem[size];
            }
        };

        public final String name;
        public final int year;
        public final int monthOfYear;
        public final int dayOfMonth;

        public ToDoItem(String name, int year, int monthOfYear, int dayOfMonth)
        {
            this.name = name.length() == 0 ? "No name" : name;
            this.year = year;
            this.monthOfYear = monthOfYear;
            this.dayOfMonth = dayOfMonth;
        }

        public ToDoItem(Parcel in)
        {
            name = in.readString();
            year = in.readInt();
            monthOfYear = in.readInt();
            dayOfMonth = in.readInt();
        }

        @Override
        public String toString()
        {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String summary = name + " (" + dateFormat.format(calendar.getTime()) + ")";
            calendar.clear();
            return summary;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(name);
            dest.writeInt(year);
            dest.writeInt(monthOfYear);
            dest.writeInt(dayOfMonth);
        }
        
        @Override
        public int hashCode()
        {
            return name.hashCode();
        }
        
        @Override
        public boolean equals(Object object)
        {
            return (object != null && name != null
                    && name.equals(((ToDoItem) object).name));
        }
    }

    private static final Calendar calendar = Calendar.getInstance();
    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private final List<ToDoItem> toDos = new ArrayList<ToDoItem>();

    private static ToDoList instance;

    private ToDoList()
    {
        //
    }

    public static ToDoList getInstance()
    {
        if (instance == null) {
            instance = new ToDoList();
        }
        return instance;
    }

    public boolean add(ToDoItem newToDoItem)
    {
        return toDos.add(newToDoItem);
    }

    public void remove(ToDoItem task)
    {
        toDos.remove(task);
    }

    public List<ToDoItem> getAllToDoItems()
    {
        return toDos;
    }
}
