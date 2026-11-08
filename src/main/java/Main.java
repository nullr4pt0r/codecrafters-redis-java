import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket = null; //serversocket created
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port); //port assigned and server started
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.
            while(true){
                Socket clientSocket = serverSocket.accept();
                //client socket created & server socket accept the connection from client

                new Thread(() -> {
                    handleClientConnection(clientSocket);
                }).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void handleClientConnection(Socket clientSocket){
        try{
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            //read input
            int byteRead;
            while((byteRead = inputStream.read(new byte[1024])) != -1){ //check connection not closed andread 1024 bytes
                outputStream.write("+PONG\r\n".getBytes());
                outputStream.flush();
            }
        } catch (Exception e) {
            System.out.println("Client handler exception: " + e.getMessage());
        }
    }
}
