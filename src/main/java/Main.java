import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");


        try {

            //using event loop

            //create a event loop core
            Selector selector = Selector.open();

            //create server socket channel
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(6379));
            serverChannel.configureBlocking(false); //non blocking

            //register the server channel with selector with interest in accepting events
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
//            System.out.println("Event Loop Redis Server started on port 6379...");
            //now check for events for our server
            while (true){
                selector.select();

                Set<SelectionKey> selectionKeys = selector.selectedKeys(); //get the events
                Iterator<SelectionKey> iterator = selectionKeys.iterator(); //create iterator for that

                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(!key.isValid()){
                        continue;
                    }

                    if(key.isAcceptable()){
                        //convert key to client socket
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel client = channel.accept();

                        if (client != null) {
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
//                            System.out.println("Accepted connection from: " + client.getRemoteAddress());
                        }

                    }else if(key.isReadable()){ //existing client
                        SocketChannel client = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        int byteRead = client.read(buffer);
//                        System.out.println("Loop fired! byteRead = " + byteRead);
                        if(byteRead==-1){
//                            System.out.println("Client disconnected.");
                            client.close();
                            continue;
                        }
                        buffer.flip();

                        if(byteRead > 0){
                            String request = new String(buffer.array(), buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
                            System.out.println(request);

                            client.write(handlePayload(request));
                        }


                    }
                }

            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteBuffer handlePayload(String request){
        //split it as array of payloads

        String[] payloads = request.replace("\\r\\n","\n").split("\n");

        System.out.println(Arrays.toString(payloads));

        //check them for command
        List<String> commands = new ArrayList<>();
        for(String payload:payloads){
            if(payload.isEmpty()){
                continue;
            }

            if(payload.startsWith("*") || payload.startsWith("$")){
                continue;
            }

            commands.add(payload);
        }


        if(commands.isEmpty()){
            return getPong();
        }

        for(int index = 0; index < commands.size(); index++){
            String command = commands.get(index);
            if("PING".equalsIgnoreCase(command)){
                return getPong();
            }
            else if("ECHO".equalsIgnoreCase(command)){
                //take next argument
                String arg = commands.get(index+1);
                return buildRESPString(arg);
            }
        }
        return getPong();
    }

    public static ByteBuffer getPong(){
      return  ByteBuffer.wrap("+PONG\r\n".getBytes());
    }

    public static ByteBuffer buildRESPString(String arg){
        String respDelimiter = "\\r\\n";
        int length = arg.length();
        if(length <= 0){
            return null;
        }
       return ByteBuffer.wrap(("$"+length+respDelimiter+arg+respDelimiter).getBytes());
    }


}
