import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/***
 * Как и в ситуации с клиентом, я правда пыталась протестировать по максимуму.
 * Но кусочно можно подтвердить тестами только то, что если не запускать все
 * в правильной последовательности, а по отдельности, то ничего почти не будет
 * работать как предполагается. Поэтому очень важно запускать сервер целиком,
 * а не по кускам))).
 */
class ServerTest {

    @Test
    void run() {
    }

    @Test
    void main() {
    }

    @Test
    void receiveMessage() {
    }

    @Test
    void collectFilesInfo() {
    }

    @Test
    void menu() throws IOException, ClassNotFoundException {
        ServerSocket connectionSocket = new ServerSocket(1234);
        Server.CONTENT_DIR = "";
        Server.ConnectionHandler connectionHandler = new Server.ConnectionHandler(connectionSocket);
        connectionHandler.start();
        InetAddress serverHost = InetAddress.getByName("localhost");
        Socket clientSocket = new Socket(serverHost, 1234);
        ObjectOutputStream socketOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream socketInput = new ObjectInputStream(clientSocket.getInputStream());
        socketOutput.writeObject("requestForFiles");
        socketOutput.writeObject("requestDirectory");
        ArrayList<String> result = (ArrayList<String>) socketInput.readObject();
        assertEquals(1, result.size());
        assertEquals("", result.get(0));
        socketOutput.writeObject("requestForFileDownload + mmm");
        socketOutput.writeObject("exit");
        Server.inWork = 0;
        clientSocket.close();
        socketOutput.close();
        socketInput.close();
        connectionSocket.close();
    }

    @Test
    void sendInfo() {
    }

    @Test
    void sendFile() {
    }

    @Test
    void close() throws IOException, InterruptedException {
        assertThrows(NullPointerException.class, Server::close);
        Server.inWork = 1;
        assertFalse(Server.close());
        ServerSocket connectionSocket = new ServerSocket(1234);
        Server.ConnectionHandler connectionHandler = new Server.ConnectionHandler(connectionSocket);
        connectionHandler.start();
        InetAddress serverHost = InetAddress.getByName("localhost");
        Socket clientSocket = new Socket(serverHost, 1234);
        ObjectOutputStream socketOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        socketOutput.writeObject("exit");
        assertFalse(Server.close());
        Server.inWork = 0;
        clientSocket.close();
        socketOutput.close();
    }
}