package bookapp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;


public class SellABook extends ActionBarActivity {

    private EditText authorEditText;
    private EditText titleEditText;
    private EditText isbnEditText;
    private Button autofillButton;

    /**
     * Creates a spinner of the conditions String array
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_a_book);

        authorEditText = (EditText)findViewById(R.id.enterAuthor);
        titleEditText  = (EditText)findViewById(R.id.enterTitle);
        isbnEditText   = (EditText)findViewById(R.id.enterISBN);
        autofillButton = (Button)findViewById(R.id.button6);

        Button save = (Button) findViewById(R.id.listBook);
        if(titleEditText.getText().toString().equalsIgnoreCase(""))
            save.setEnabled(false);
        if(authorEditText.getText().toString().equalsIgnoreCase(""))
            save.setEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sell_abook, menu);
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



    public void autoComplete(View view){

        String ISBNString = isbnEditText.getText().toString();
        ISBNString = ISBNString.replaceAll("[^0-9]", "");
        String stringUrl = "http://www.isbnsearch.org/isbn/" + ISBNString;

        autofillButton.setText(R.string.dotdotdot);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
            Button save = (Button) findViewById(R.id.listBook);
            if(titleEditText.getText().toString().equalsIgnoreCase(""))
                save.setEnabled(false);
            if(authorEditText.getText().toString().equalsIgnoreCase(""))
                save.setEnabled(false);
            else
                save.setEnabled(true);
            save.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String txt = "ADD%" + isbnEditText.getText().toString() + "|" +
                            titleEditText.getText().toString() + "|" + authorEditText.getText().toString() +
                            "|" + MyProperties.getInstance().email + "|" + ((EditText) findViewById(R.id.priceTextField)).getText().toString();
                    getMessage(txt);
                    Intent intent = new Intent(getApplicationContext(), FrontPage.class);
                    startActivity(intent);
                }
            });
        } else {
            //Toast no connection available
            Context context = getApplicationContext();
            Toast connToast = Toast.makeText(context, "No connection available.", Toast.LENGTH_SHORT);
            connToast.show();
            //authorEditText.setText("No Connection Available");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve information. ISBN may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //authorEditText.setText("" + (result.trim()).length());
            Document doc            = Jsoup.parse(result);
            Elements bookinfos      = doc.select("div.bookinfo");
            Element bookinfo        = bookinfos.first();
            if(bookinfo == null){
                Context context = getApplicationContext();
                Toast connToast = Toast.makeText(context, "ISBN not found.", Toast.LENGTH_SHORT);
                connToast.show();
                autofillButton.setText(R.string.autoFill);
                //titleEditText.setText("ISBN not found.");
                //one day this should be a Toast.
                return;
            }
            Elements titleElements  = bookinfo.getElementsByTag("h2");
            Element titleElement    = titleElements.first();
            String title            = titleElement.text();
            Elements authorElements = bookinfo.getElementsByTag("p");
            Element authorElement   = authorElements.get(2);
            String authorString     = authorElement.text();

            if(authorString.contains("Authors"))
                authorString = authorString.substring(9, authorString.length());
            else
                authorString = authorString.substring(8, authorString.length());

            authorEditText.setText(authorString);
            titleEditText.setText(title);
            autofillButton.setText(R.string.autoFill);
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 30000 characters of the retrieved
        // web page content.
        int len = 32000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            String DEBUG_TAG = "downloadURL";
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            InputStreamReader isr = new InputStreamReader(is);

            String contentAsString = "";

            int data = isr.read();
            while(data != -1){
                char current = (char)data;
                contentAsString += current;
                data = isr.read();
            }

            // Convert the InputStream into a string
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Grabs the string from the server and parses it to display in the ListView
     * @param savedInstanceState the previous screens state
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
