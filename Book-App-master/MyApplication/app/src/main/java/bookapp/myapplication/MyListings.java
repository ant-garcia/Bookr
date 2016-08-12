package bookapp.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class MyListings extends ActionBarActivity {

    private boolean isRemoved;
    private TextView removedView;

    /**
     * Grabs the string from the previous state and parses it to display in the ListView
     * @param savedInstanceState the previous screens state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        ListView list = (ListView) findViewById(R.id.listView);
        ArrayList<String> bookList = new ArrayList<>();
        Intent i = getIntent();

        String[] results = i.getStringExtra("list").replace("|", "\n").split("%");
        bookList.addAll(Arrays.asList(results));
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.rowTextView , bookList);
        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView txt = (TextView) view.findViewById(R.id.rowTextView);
                removedView = txt;
                txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = txt.getText().toString();
                        Log.e("n", msg);
                        openDialog();
                    }
                });

            }
        });

        Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if(isRemoved)
                {
                    Log.e("n", "here");
                    Log.e("txt", removedView.getText().toString());
                    getMessage("DEL%" + removedView.getText().toString().replace("\n", "|"));
                }
                Intent next = new Intent(getApplicationContext(), FrontPage.class);
                startActivity(next);
            }
        });
    }

    /**
     * An dialog box to confirm whether a book should be deleted
     * @return true/false
     */
    public boolean openDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure that you want to remove this book?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d1, int by) {
                Toast.makeText(MyListings.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                isRemoved = true;
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d2, int bn) {
                Toast.makeText(MyListings.this,"You clicked no button",Toast.LENGTH_LONG).show();
                isRemoved = false;
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return isRemoved;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_listings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends and receives messages from the client
     * @param  msg to be sent to the server
     * @return the string sent back to the client
     */
    public String getMessage(String msg)
    {
        Client c = new Client();
        try
        {
            c.execute(msg);
            return c.get();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return "error";
    }
}
