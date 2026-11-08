import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

//      Uncomment the code below to pass the first stage
        ServerSocket serverSocket = null; //serversocket created
        Socket clientSocket = null; //client socket instance created
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port); //port assigned and server started
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.

            while(true){
                 clientSocket = serverSocket.accept();
                //accept the connection from client

                Socket finalClientSocket = clientSocket; //copy the client socket to another variable
                new Thread(() -> {
                    try{
                        InputStream inputStream = finalClientSocket.getInputStream();
                        OutputStream outputStream = finalClientSocket.getOutputStream();

                        //read input
                        int byteRead;

                        while((byteRead = inputStream.read(new byte[1024])) != -1){ //check connection not closed andread 1024 bytes
                            outputStream.write("+PONG\r\n".getBytes());
                            outputStream.flush();
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }).start();
            }



        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
