package bookapp.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class SearchResults extends ActionBarActivity {

    /**
     * Grabs the string from the previous state and parses it to display in the ListView
     * @param savedInstanceState the previous screens state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ListView list = (ListView) findViewById(R.id.searchList);
        ArrayList<String> bookList = new ArrayList<>();
        Intent i = getIntent();
        String[] temp = i.getStringExtra("query").replace("|", "\n").split(Pattern.quote("$"));
        final String book = temp[0];
        String[] results = temp[1].split("%");
        bookList.addAll(Arrays.asList(results));

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.rowTextView , bookList);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView txt = (TextView) view.findViewById(R.id.rowTextView);
                txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = txt.getText().toString();
                        Intent nextScreen = new Intent(getApplicationContext(), BookInformation.class);
                        nextScreen.putExtra("query", msg);
                        nextScreen.putExtra("book", book);
                        Log.e("msg", msg);
                        startActivity(nextScreen);
                    }
                });

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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
