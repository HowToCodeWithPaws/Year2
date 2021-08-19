package classes;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/***
 * Тесты для класса клиента. Честно, сложно в это поверить, но я делала тесты
 * так нормально, как могла, чтобы протестировать это, а не просто для галочки.
 * Но тесты на сокеты это какой-то ад. Если не пытаться читерски создать просто
 * клиент и сервер как экземлпяры классов, а реально тестировать методы отдельно,
 * там нормально нельзя настроить взаимодействие, поэтому протестировать можно почти исключительно ошибки.
 * Короче, грустно, не бейте, я пыталась.
 */
class ClientTest {

    @Test
    void getTaskDownload() {
        Client newClient = new Client();
        FileForTorrent file = new FileForTorrent("hello", "name", 12L);
        assertDoesNotThrow(() -> newClient.getTaskDownload(file, "path"));
        Task task = newClient.getTaskDownload(file, "path");
        Thread thread = new Thread(task);
        assertDoesNotThrow(thread::start);
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("what is it");
        }
    }

    @Test
    void getFiles() {
        Client newClient = new Client();
        assertNull(newClient.getFiles());
        newClient.files = FXCollections.observableArrayList();
        assertEquals(0, newClient.getFiles().size());
        FileForTorrent file = new FileForTorrent("hello", "name", 12L);
        newClient.getFiles().add(file);
        assertEquals(1, newClient.getFiles().size());
    }

    @Test
    void downloadStart() {
        Client client = new Client();
        assertThrows(NullPointerException.class,client::downloadStart);
        client.download =new Task() {
            @Override
            protected Object call() throws IOException {
                return true;
            }
        };
        assertFalse(client.downloadStart());
    }

    @Test
    void requestTable() {
        Client client = new Client();
        assertDoesNotThrow(client::requestTable);
        assertFalse(client.requestTable());
    }

    @Test
    void sendOutput() throws IOException {
        Client newClient = new Client();
        assertDoesNotThrow(() -> newClient.sendOutput("hi"));
        assertFalse(newClient.sendOutput("hi"));
        Client client = new Client();
        ServerSocket connectionSocket = new ServerSocket(1234);
        client.clientSocket = new Socket("localhost", 1234);
        client.socketOutput = new ObjectOutputStream(client.clientSocket.getOutputStream());
        assertTrue(client.sendOutput("hi"));
        connectionSocket.close();
        client.closeSocket();
    }

    @Test
    void getInput() {
        Client client = new Client();
        assertDoesNotThrow(client::getInput);
        assertEquals(0, client.getInput().size());
    }

    @Test
    void tryConnect() throws IOException {
        Client client = new Client();
        assertFalse(client.tryConnect("localhost", 1234));
        ServerSocket connectionSocket = new ServerSocket(1234);
        assertTrue(client.tryConnect("localhost", 1234));
        client.closeSocket();
        connectionSocket.close();
    }

    @Test
    void greetings() throws IOException {
        Client newClient = new Client();
        assertFalse(newClient.greetings());
        newClient.closeSocket();
    }

    @Test
    void closeSocket() throws IOException {
        Client newClient = new Client();
        assertDoesNotThrow(newClient::closeSocket);
        assertFalse(newClient::closeSocket);
    }
}