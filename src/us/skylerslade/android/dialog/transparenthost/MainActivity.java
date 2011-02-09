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

import android.app.ListActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import us.skylerslade.android.dialog.transparenthost.R;

public class MainActivity extends ListActivity
{
    private static final int REQUEST_CODE_CREATE_NEW = 1;
    
    @Override
    public void onCreate(Bundle inState)
    {
        super.onCreate(inState);
        setContentView(R.layout.main);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                new String[] {getString(R.string.todo_items),
                        getString(R.string.create_new_todo)}));
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        if (position == 0) {
            Intent i = new Intent(this, ToDoListActivity.class);
            startActivity(i);
        } else if (position == 1) {
            Intent i = new Intent(this, DialogTasks.class);
            i.putExtra(DialogTasks.EXTRA_CREATE_TODO, true);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(i, REQUEST_CODE_CREATE_NEW);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if (requestCode == REQUEST_CODE_CREATE_NEW) {
           if (resultCode == RESULT_OK) {
               Toast.makeText(this, R.string.todo_created, Toast.LENGTH_SHORT).show();
           }
       }
    }
}
