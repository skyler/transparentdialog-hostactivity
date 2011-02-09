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

import java.lang.Override;

import android.app.ListActivity;

import android.content.Intent;

import android.os.Bundle;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import us.skylerslade.android.dialog.transparenthost.R;

public class ToDoListActivity extends ListActivity
{
    private static final int MENU_CREATE_NEW = 1;

    /**
     * Request code used with startActivityForResult to create a new To Do
     */
    private static final int REQUEST_CODE_CREATE_NEW = 1;
    /**
     * Request code used with startActivityForResult to edit an existing To Do's name
     */
    private static final int REQUEST_CODE_EDIT_NAME = 2;
    /**
     * Request code used with startActivityForResult to delete a To Do item
     */
    private static final int REQUEST_CODE_DELETE = 3;

    private static final int CONTEXT_ITEM_EDIT_NAME = 0;
    private static final int CONTEXT_ITEM_DELETE = 1;

    private ArrayAdapter<ToDoList.ToDoItem> listAdapter;

    @Override
    public void onCreate(Bundle inState)
    {
        super.onCreate(inState);
        setContentView(R.layout.todo_list);

        listAdapter = new ArrayAdapter<ToDoList.ToDoItem>(this,
                android.R.layout.simple_list_item_1, ToDoList.getInstance()
                        .getAllToDoItems());
        setListAdapter(listAdapter);
        
        registerForContextMenu(getListView());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_CREATE_NEW, 1, R.string.create_today_todo_menu)
                .setIcon(android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == MENU_CREATE_NEW) {
            Intent i = new Intent(this, DialogTasks.class);
            i.putExtra(DialogTasks.EXTRA_CREATE_TODO, true);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                    | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(i, REQUEST_CODE_CREATE_NEW);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(Menu.NONE, CONTEXT_ITEM_EDIT_NAME, Menu.NONE, 
                R.string.context_edit_todo_name);
        menu.add(Menu.NONE, CONTEXT_ITEM_DELETE, Menu.NONE,
                R.string.context_delete_todo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem)
    {
        AdapterContextMenuInfo item = (AdapterContextMenuInfo) menuItem.getMenuInfo();
        ToDoList.ToDoItem toDoItem = (ToDoList.ToDoItem) listAdapter.getItem(item.position);

        Intent i = new Intent(this, DialogTasks.class);
        i.putExtra(ToDoList.ToDoItem.EXTRA_TODO, toDoItem);
        int id = menuItem.getItemId();
        if (id == CONTEXT_ITEM_EDIT_NAME) {
            i.putExtra(DialogTasks.EXTRA_EDIT_TODO_NAME, true);
            startActivityForResult(i, REQUEST_CODE_EDIT_NAME);
            return true;
        } else if (id == CONTEXT_ITEM_DELETE) {
            i.putExtra(DialogTasks.EXTRA_DELETE_TODO, true);
            startActivityForResult(i, REQUEST_CODE_DELETE);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * When DialogTasks is started for result, upon finish, this method is invoked.
     * Update the list with a new adapter and show a toast.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        listAdapter = new ArrayAdapter<ToDoList.ToDoItem>(this,
                    android.R.layout.simple_list_item_1, ToDoList.getInstance()
                            .getAllToDoItems());
            setListAdapter(listAdapter);
        if (requestCode == REQUEST_CODE_CREATE_NEW && resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.todo_created, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_EDIT_NAME && resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.todo_updated, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_DELETE && resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.todo_deleted, Toast.LENGTH_SHORT).show();
        }
    } 
}
