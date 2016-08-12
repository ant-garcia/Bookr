package bookapp.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client extends AsyncTask<String, Void, String>
{
    String msg;

    /**
     * Task that allows the client to send and recieve messages from the server
     * @param  msg that is to be sent to the server
     * @return a string that is recieved from the server to be used by the client
     */
    protected String doInBackground(String... msg)
    {
        final int PORT = 9072;
        String host = "";//server host
        String results = "";

        Socket tcp = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try
        {
            tcp = new Socket(host, PORT);
            out = new PrintWriter(tcp.getOutputStream());
            in = new BufferedReader(new InputStreamReader(tcp.getInputStream()));
            Log.e("SENDING% ", msg[0]);
            out.println(msg[0]);
            out.flush();
            results = in.readLine();
            Log.e("GOT% ", results);
            out.close();
            in.close();
            tcp.close();

            return results;
        }
        catch(IOException e)
        {

            Log.e("ERROR: ", e.getMessage());
        }
            return "error";
    }


    protected void onPostExecute(String s)
    {
    }
}
