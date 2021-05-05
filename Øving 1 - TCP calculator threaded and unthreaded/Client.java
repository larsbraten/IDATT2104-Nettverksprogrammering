import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  public static void main(String[] args) throws IOException {

    /* Using a scanner to read input */
    Scanner inn = new Scanner(System.in);
    System.out.println(
        "What is the IP address of the server?");
    String serverMachine = inn.nextLine();

    /* Connects to the server machine */
    Socket connection = new Socket(serverMachine, 1250);
    System.out.println(
        "Connection established.\nThis calculator supports addition and subtraction "
            +
            "\nType your equation in the form 'Operand SPACE Operator SPACE Operand'");

    /* Opens a connection for communication with the server program */
    InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
    BufferedReader reader = new BufferedReader(readConnection);
    PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

    /* Client input loop */

    String line = inn.nextLine().trim();
    boolean hasExited;
    while (!line.equals("")) {
      System.out.println(reader.readLine());
      writer.println(line);

      /* Reply */
      String reply = reader.readLine();
      System.out.printf(
          "Reply: %s%n", reply);
      hasExited = (reply.contains(
          "Closing connection"));
      if (hasExited) {
        break;
      }
      line = inn.nextLine();
    }
  }
}

