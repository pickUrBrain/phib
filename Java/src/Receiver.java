import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhiling on 5/20/17.
 */
public class Receiver implements Runnable {

    // unbounded
    BlockingQueue<byte[]> receivedMsgs = new ArrayBlockingQueue<byte[]>(100);

    private DatagramSocket mySocket;
    private int myPort;

    private boolean isRunning = true;

    public int timeOut = 5000;

    public Receiver(int port) {
        myPort = port;
        try {
            mySocket = new DatagramSocket(myPort);
            mySocket.setSoTimeout(timeOut);        // wait for 0.5 second for data
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        DatagramPacket packet;
        while (isRunning) {
            // buffer is filled with the data received
            try {
                InetAddress address = InetAddress.getByName("127.0.0.1");
                byte[] msg = new byte[1024];
//                packet = new DatagramPacket(msg, msg.length, address, myPort);
                packet = new DatagramPacket(msg, msg.length);
                mySocket.receive(packet);    // blocks until it receives a datagram
                receivedMsgs.offer(msg, timeOut, TimeUnit.MILLISECONDS);
                System.out.println(new String(msg));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void stop() {
        // interrupt a blocked socket.
        isRunning = false;
        mySocket.close();
    }

    public BlockingQueue<byte[]> getMsgQueue() {
        return receivedMsgs;
    }

    public static void main(String[] args) {
        String ipAddress = null;
        int port = -1;
        if (args.length < 1) {

        }
        // Check given file is Simple Text File using Java
        else if (args.length == 1) {
            try {
                // ../Preference.txt
                Scanner reader = new Scanner(new FileInputStream(args[0]));
                port = reader.nextInt();
                ipAddress = reader.nextLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else if (args.length == 2)  {
            port = Integer.parseInt(args[0]);
        }

        Receiver receiver = new Receiver(port);
        new Thread(receiver).start();
    }
}
