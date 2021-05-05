import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {

  public static void main(String[] args) throws IOException {

    DatagramSocket datagramSocket = new DatagramSocket();
    Scanner inn = new Scanner(System.in);
    String input = inn.nextLine().trim();
    while (!input.equals("")) {
      byte[] bytes;
      bytes = (input).getBytes();
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length,
          InetAddress.getLocalHost(), 1234);
      datagramSocket.send(datagramPacket);

      byte[] bytes1 = new byte[1024];
      DatagramPacket datagramPacket1 = new DatagramPacket(bytes1, bytes1.length);
      datagramSocket.receive(datagramPacket1);

      String receive = new String(datagramPacket1.getData());
      System.out.printf("The result is: %s%n", receive);
      input = inn.nextLine().trim();

    }
  }
}
