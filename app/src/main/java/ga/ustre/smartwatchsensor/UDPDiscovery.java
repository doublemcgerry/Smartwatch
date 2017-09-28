package ga.ustre.smartwatchsensor;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ga.ustre.smartwatchsensor.webvisservices.DiscoveryServicesDefinitions;

/**
 * Created by achelius on 03/01/2017.
 */

public class UDPDiscovery {

    public interface Callbacks{
        void onAddressDiscovered(String address, DiscoveryServicesDefinitions defs);
        void onProgressUpdate(String message);
    }

    private AsyncTask<Void, String, Void> async_client;
    public String Message = "DISCOVER_SERVICES";
    private final Callbacks callbacks;

    public UDPDiscovery(Callbacks callbacks){
        this.callbacks=callbacks;
    }

    public void restartSearch(){
        if (async_client.getStatus()== AsyncTask.Status.FINISHED){
            SendDiscovery();
        }
    }

    public void SendDiscovery() {
        async_client = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                boolean foundAddress=false;
                while(!foundAddress) {
                    DatagramSocket ds = null;

                    try {
                        publishProgress("Searching for a server");
                        ds = new DatagramSocket();
                        DatagramPacket dp;
                        dp = new DatagramPacket(Message.getBytes(), Message.length(), InetAddress.getByName("255.255.255.255"), 9000);
                        ds.setBroadcast(true);
                        ds.send(dp);
                        publishProgress("Search packet sent");
                        byte[] recVBuf = new byte[1500];
                        dp = new DatagramPacket(recVBuf, recVBuf.length);
                        ds.setSoTimeout(4000);
                        ds.receive(dp);
                        publishProgress("Received the packet from the server");
                        publishProgress("Found server:" + dp.getAddress().getHostAddress());

                        String message = new String(dp.getData()).trim();
                        Gson gson = new Gson();
                        DiscoveryServicesDefinitions defs= gson.fromJson(message, DiscoveryServicesDefinitions.class);
                        callbacks.onAddressDiscovered(dp.getAddress().getHostAddress(),defs);
                        foundAddress=true;
                    } catch (SocketTimeoutException e) {
                        //timeout
                        publishProgress("Timeout while waiting for response");
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
            }
        };

        async_client.execute();
    }
}
