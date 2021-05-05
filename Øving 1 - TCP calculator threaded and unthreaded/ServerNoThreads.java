import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNoThreads {

  public static void main(String[] args) throws IOException {
    /* InetAddress.getByName(null) points to the loopback address localhost(127.0.0.1) */
    ServerSocket serverSocket = new ServerSocket(1250, 0, InetAddress.getByName(null));
    /* Prints out the server IP address, which the client can use to connect.
    I don't know why, but it prints out 0.0.0.0, meaning all IPV4 addresses on the machine instead of 127.0.0.1, but both addresses work. */
    System.out.println(
        "Log for the server side. Now we wait... Use IP address " + serverSocket
            .getInetAddress()
            + " to connect to the server,");
    /* Idles until someone contacts the server */
    Socket connection = serverSocket.accept();

    InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
    BufferedReader reader = new BufferedReader(readConnection);
    PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

    while (true) {
      /* Waiting for input */
      String input = null;
      assert writer != null;
      writer.println(
          "Enter your equation");
      try {
        input = reader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println(
          "Equation received from :" + serverSocket);
      System.out.println(input);
      /* This regular expression both checks if the input if correct, and also splits it by whitespaces. */
      assert input != null;
      if (input.matches("[0-9]* [+*] [0-9]*")) {
        String[] substrings = input.split(" ");
        int operand1 = Integer.parseInt(substrings[0]);
        String operation = substrings[1];
        int operand2 = Integer.parseInt(substrings[2]);
        System.out.println(
            "Received input : "
                + input
                + " Sending the result back to the client");
        {
          if (operation.equals("+")) {
            writer.println(operand1 + operand2);
          } else if (operation.equals("-")) {
            writer.println(operand1 - operand2);
          } else if (operation.equals("/") && operand2 != 0) {
            writer.println(operand1 / operand2);
          } else if (operation.equals("*")) {
            writer.println(operand1 * operand2);
          } else {
            writer.println(
                "I didn't understand, try again");
          }
        }
      } else {
        writer.println(
            "I didn't understand, try again");
      }
    }
  }
}