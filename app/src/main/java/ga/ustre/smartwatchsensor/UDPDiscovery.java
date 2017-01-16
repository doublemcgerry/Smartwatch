package ga.ustre.smartwatchsensor;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by achelius on 03/01/2017.
 */

public class UDPDiscovery {

    public interface Callbacks{
        void onAddressDiscovered(final String address);
        void onProgressUpdate(final String message);
    }

    private AsyncTask<Void, String, Void> async_client;
    public String Message = "DISCOVER_AUISERVER_REQUEST";
    private final Callbacks callbacks;
    private boolean working=false;
    private boolean closing=false;

    public UDPDiscovery(Callbacks callbacks){
        this.callbacks=callbacks;
    }


    public void SendDiscovery() {
        if (working){
            return;
        }
        async_client = new AsyncTask<Void, String, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                working=true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                boolean foundAddress=false;
                while(!closing && !foundAddress) {
                    DatagramSocket ds = null;

                    try {
                        publishProgress("Searching for a server");
                        ds = new DatagramSocket();
                        DatagramPacket dp;
                        dp = new DatagramPacket(Message.getBytes(), Message.length(), InetAddress.getByName("255.255.255.255"), 8091);
                        ds.setBroadcast(true);
                        ds.send(dp);
                        publishProgress("Searching for a server...");
                        byte[] recVBuf = new byte[1500];
                        dp = new DatagramPacket(recVBuf, recVBuf.length);
                        ds.setSoTimeout(4000);
                        ds.receive(dp);
                        publishProgress("Connecting to :" + dp.getAddress().getHostAddress());
                        callbacks.onAddressDiscovered(dp.getAddress().getHostAddress());
                        foundAddress=true;
                    } catch (SocketTimeoutException e) {
                        //timeout
                        publishProgress("No server found!");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (ds != null) {
                            ds.close();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                callbacks.onProgressUpdate(values[0]);
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                working=false;
                closing=false;
            }
        };

        async_client.execute();
    }

    public boolean isWorking(){
        return working;
    }

    public void stopDiscovery(){
        closing=true;

    }


}
