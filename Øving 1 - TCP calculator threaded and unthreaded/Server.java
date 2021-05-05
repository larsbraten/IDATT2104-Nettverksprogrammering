import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public static void main(String[] args) {
    Socket socketConnection = null;

    try {
      /* InetAddress.getByName(null) points to the loopback address localhost(127.0.0.1) */
      ServerSocket serverSocket = new ServerSocket(1250, 0, InetAddress.getByName(null));
      /* Prints out the server IP address, which the client can use to connect. */
      System.out.println(
          "Log for the server side. Now we wait... Use IP address " + serverSocket
              .getInetAddress()
              + " to connect to the server,");
      /* Idles until someone contacts the server */
      while (true) {
        socketConnection = serverSocket.accept();
        System.out.printf(
            "New client connected %s : ", socketConnection);
        /* Assigns a thread to the new client */
        System.out.println(
            "Assigning new thread");
        Thread newClient = new ThreadServer(socketConnection);
        newClient.start();
      }

    } catch (IOException e) {
      e.printStackTrace();

    }

    try {
      assert socketConnection != null;
      socketConnection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
