import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

  public static void main(String[] args) throws IOException {
    DatagramSocket datagramSocket = new DatagramSocket(1234);
    byte[] bytes = new byte[1024];
    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);

    while (true) {
      datagramSocket.receive(datagramPacket);
      String input = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
      /* The UDP calculator uses the same logic as the TCP calculator */
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
            int result = operand1 + operand2;
            byte[] b2 = String.valueOf(result).getBytes();
            DatagramPacket datagramPacket1 = new DatagramPacket(b2, b2.length,
                InetAddress.getLocalHost(), datagramPacket.getPort());
            datagramSocket.send(datagramPacket1);
          } else if (operation.equals("-")) {
            int result = operand1 - operand2;
            byte[] b2 = String.valueOf(result).getBytes();
            DatagramPacket datagramPacket1 = new DatagramPacket(b2, b2.length,
                InetAddress.getLocalHost(), datagramPacket.getPort());
            datagramSocket.send(datagramPacket1);
          } else {
            String result = "I didn't understand, please try again";
            byte[] b2 = result.getBytes();
            DatagramPacket datagramPacket1 = new DatagramPacket(b2, b2.length,
                InetAddress.getLocalHost(), datagramPacket.getPort());
            datagramSocket.send(datagramPacket1);
          }
        }
      } else {
        String result = "I didn't understand, please try again";
        byte[] b2 = result.getBytes();
        DatagramPacket datagramPacket1 = new DatagramPacket(b2, b2.length,
            InetAddress.getLocalHost(),
            datagramPacket.getPort());
        datagramSocket.send(datagramPacket1);
      }
    }
  }
}