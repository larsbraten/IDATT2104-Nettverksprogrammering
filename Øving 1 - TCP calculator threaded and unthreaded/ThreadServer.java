import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadServer extends Thread {

  private final Socket socketConnection;

  public ThreadServer(Socket socketConnection) {
    this.socketConnection = socketConnection;
  }

  @Override
  public void run() {
    PrintWriter writer = null;
    InputStreamReader readConnection = null;
    try {
      readConnection = new InputStreamReader(socketConnection.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert readConnection != null;
    BufferedReader reader = new BufferedReader(readConnection);
    try {
      writer = new PrintWriter(socketConnection.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }

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
          "Equation received from :"
              + socketConnection);
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
                + " Sending the result back to the client.");
        {
          if (operation.equals("+")) {
            writer.println(operand1 + operand2);
          } else if (operation.equals("-")) {
            writer.println(operand1 - operand2);
          } else {
            writer.println(
                "I didn't understand, try again.");
          }
        }
      } else {
        writer.println(
            "I didn't understand, try again.");
      }
    }
  }
}