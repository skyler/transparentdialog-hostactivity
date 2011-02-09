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

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import us.skylerslade.android.dialog.transparenthost.R;

public class DialogTasks extends Activity implements DatePickerDialog.OnDateSetListener
{
    private static final int EDIT_NAME_DIALOG = 0;
    private static final int CREATE_NEW_DIALOG = 1;
    private static final int SAVING_DIALOG = 2;
    private static final int DATE_PICKER_DIALOG = 3;
    private static final int CONFIRM_DELETE_DIALOG = 4;
    private static final int DELETING_DIALOG = 5;

    private static final Calendar calendar = Calendar.getInstance();

    /**
     * Extra sent to create a new ToDo item
     */
    public static final String EXTRA_CREATE_TODO = "createToDo";
    /**
     * Extra sent to edit an existing ToDo item's name
     */
    public static final String EXTRA_EDIT_TODO_NAME = "editToDoName";
    /**
     * Extra sent to delete an existing ToDo item
     */
    public static final String EXTRA_DELETE_TODO = "deleteToDo";

    /**
     * Stores the new To Do name between when the user enters it and chooses a
     * data
     */
    private String newToDoItemName;

    /**
     * Stores passed-in ToDoItem, sent as Intent extra, used for edit and delete
     * To Do items
     */
    private ToDoList.ToDoItem currentToDoItem;

    private boolean dateSet;

    /**
     * Handler used to simulate delayed network responses
     */
    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle inState)
    {
        super.onCreate(inState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentToDoItem = extras.getParcelable(ToDoList.ToDoItem.EXTRA_TODO);
            if (extras.containsKey(EXTRA_CREATE_TODO)) {
                showDialog(CREATE_NEW_DIALOG);
            } else if (extras.containsKey(EXTRA_EDIT_TODO_NAME)) {
                showDialog(EDIT_NAME_DIALOG);
            } else if (extras.containsKey(EXTRA_DELETE_TODO)
                    && extras.containsKey(ToDoList.ToDoItem.EXTRA_TODO)) {
                showDialog(CONFIRM_DELETE_DIALOG);
            } else {
                finish();
            }
            /*
             * Overwrite our Intent's original extras so that on configuration
             * change we do not create duplicate, overlapping dialogs. Recall that
             * on configuration change, Android will re-create and show any
             * already-open managed dialogs.
             */
            Intent intent = getIntent();
            if (intent != null) {
                intent.replaceExtras((Bundle) null);
            }
        }
    }

    /**
     * Lifecycle method invoked to create a dialog shown via showDialog() NOTE:
     * we override the older, deprecated method so that we can work on older sdk.
     * versions.
     */
    @Override
    public Dialog onCreateDialog(int which) {
        if (which == EDIT_NAME_DIALOG) {
            final View dialogView = getLayoutInflater().inflate(R.layout.new_todo_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(R.string.edit_todo_title)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String name = ((EditText) dialogView.findViewById(R.id.todo_name_edit)).getText().toString();
                        ToDoList.ToDoItem editedToDoItem = new ToDoList.ToDoItem(
                                name, currentToDoItem.year,
                                currentToDoItem.monthOfYear,
                                currentToDoItem.dayOfMonth);
                        ToDoList.getInstance().remove(currentToDoItem);
                        ToDoList.getInstance().add(editedToDoItem);
                        showDialog(SAVING_DIALOG);
                        // Normally this would start an AsyncTask to make a network request or
                        // to write to a local database, but for this demo I'm using a Handler
                        // on the main thread to simulate the delay of sending a network request
                        // and waiting for its response.
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(DialogTasks.this, R.string.todo_updated,
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 3000);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }).create();
            return dialog;
        } else if (which == CREATE_NEW_DIALOG) {
            final View dialogView = getLayoutInflater().inflate(R.layout.new_todo_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(R.string.create_todo_title)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        newToDoItemName = ((EditText) dialogView.findViewById(
                                R.id.todo_name_edit)).getText().toString();
                        showDialog(DATE_PICKER_DIALOG);
                    }
                 })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                 }).create();
            return dialog;
        } else if (which == SAVING_DIALOG) {
            ProgressDialog dialog = new ProgressDialog(this);
            // Only setting this ProgressDialog to not be cancelable for this demo.
            // Any progress dialogs displayed while waiting for a network response
            // should always be cancelable, and its onCancel() callback should be wired
            // to cancel the network request and free any associated resources.
            dialog.setMessage(getString(R.string.saving_todo));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            return dialog;
        } else if (which == DATE_PICKER_DIALOG) {
            DatePickerDialog dialog = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.setTitle(R.string.pick_todo_date);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    if (!dateSet) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
            });
            return dialog;
        } else if (which == CONFIRM_DELETE_DIALOG) {
            return new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        showDialog(DELETING_DIALOG);
                        ToDoList.getInstance().remove(currentToDoItem);
                        // Normally this would be a network request or a query to a local database.
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(DialogTasks.this, R.string.todo_deleted,
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 3000);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }).create();
        } else if (which == DELETING_DIALOG) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.deleting_todo));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            return dialog;
        } else {
            return null;
        }
    }

    @Override
    public void onPrepareDialog(int which, Dialog dialog)
    {
        if (which == DATE_PICKER_DIALOG) {
            /**
             * When the "cancel" button on DatePickerDialog is pressed, the onCancelListener
             * callback is never invoked, however the onCancelListener is invoked when the
             * user presses the phone's "back" hard key. To know if the user pressed the cancel
             * button, and to know if we should finish(), we set a boolean value in onDateSet(),
             * and reset it in the onPrepareDialog(). 
             */
            dateSet = false;
        } else if (which == EDIT_NAME_DIALOG) {
            ((EditText) dialog.findViewById(R.id.todo_name_edit)).setText(currentToDoItem.name);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        dateSet = true;
        showDialog(SAVING_DIALOG);
        ToDoList.ToDoItem newToDo = new ToDoList.ToDoItem(newToDoItemName,
                year, monthOfYear, dayOfMonth);
        ToDoList.getInstance().add(newToDo);
        setResult(RESULT_OK);

        // Normally this would be delayed because of a network request or a
        // query to a local database.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }
}
