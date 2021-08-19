package classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/***
 * Класс для клиента, пользующегося торрентом. Этот объект
 * содержит сокет, потоки ввода и вывода, таск для загрузки файла,
 * коллекцию доступных для скачивания файлов. Первым делом клиент
 * подключается к серверу, получает директорию, доступные файлы,
 * далее при запросах на скачивание получает также скачиваемый файл.
 * При закрытии клиента, кроме закрытия всех потоков ввода\вывода и
 * сокета, на сервер отправляется запрос на закрытие подключения.
 */
public class Client {
    private String serverHostName = "localhost";
    private int serverPortNumber = 3456;
    Socket clientSocket;
    String directory;
    ObservableList<FileForTorrent> files;
    ObjectInputStream socketInput;
    ObjectOutputStream socketOutput;
    Task download;
    FileOutputStream fout;

    /***
     * Метод для попытки подключения - клиент создает сокет по указанному адресу,
     * если все хорошо, записывает адрес, иначе выводит сообщение об ошибке.
     * @param host - хост, к которому подключаемся.
     * @param port - порт, к которому подключаемся.
     * @return - статус успешности подключения.
     */
    public boolean tryConnect(String host, Integer port) {
        try {
            InetAddress serverHost = InetAddress.getByName(host);
            clientSocket = new Socket(serverHost, port);
            serverHostName = host;
            serverPortNumber = port;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /***
     * Метод для инициации взаимодействия с сервером. Создаются выходной и
     * входной поток, серверу отправляется запрос на получение названия директории,
     * в которой тот работает, далее получается ответ и выводится полученная директория.
     * @return - статус успешности выполнения приветствий.
     */
    public boolean greetings() {
        try {
            socketOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            sendOutput("requestDirectory");
            socketInput = new ObjectInputStream(clientSocket.getInputStream());
            directory = getInput().get(0);
            System.out.println("directory " + directory);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /***
     * Метод для отправки запроса на сервер. Записывает в
     * выходной поток сериализованное сообщение, которое должно быть обработано на сервере.
     * @param message - запрос, который мы отправляем серверу.
     * @return -  статус успешности отправки запроса.
     */
    public boolean sendOutput(String message) {
        try {
            socketOutput.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /***
     * Метод для получения ответа от сервера. Из выходного потока получаем сериализованные
     * объекты, так как сервер всегда отправляет строки, десериализуем полученное в
     * коллекцию строк.
     * @return - коллекция строк, полученная от сервера.
     */
    public ArrayList<String> getInput() {
        try {
            ArrayList<String> result = (ArrayList<String>) socketInput.readObject();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /***
     * Метод для запроса к серверу на получение файлов для таблицы - отправляется
     * запрос, ожидается ответ на него, далее сериализованная информация о файлах
     * десериализуется и переводится в объекты класса FileForTorrent, которые собираются
     * в коллекцию для отображения в таблице.
     * @return - статус успешности получения файлов.
     */
    public boolean requestTable() {
        if (!sendOutput("requestForFiles")) {
            return false;
        }
        ArrayList<String> fileNames = getInput();
        ArrayList<String> fileSizes = getInput();
        System.out.println("files acquired");
        if (fileNames.isEmpty() || fileSizes.isEmpty() || fileNames.size() != fileSizes.size()) {
            return false;
        }
        files = FXCollections.observableArrayList();
        for (int i = 0; i < fileNames.size(); ++i) {
            FileForTorrent file = new FileForTorrent(directory, fileNames.get(i), Long.parseLong(fileSizes.get(i)));
            files.add(file);
        }
        return true;
    }

    /***
     * Метод для получения обзервабл коллекции доступных для скачивания файлов.
     * @return - коллекция файлов, полученных от сервера.
     */
    public ObservableList<FileForTorrent> getFiles() {
        return files;
    }

    /***
     * Метод для получения таска загрузки файла. Файл загружается по кускам
     * размером 1024 байт. Клиент отправляет запрос на сервер для получения файла,
     * сервер отправляет его по кускам в сериализованном виде, клиент принимает
     * куски и записывает байты в поток вывода, соответствующий директории, куда
     * мы хотим скачать файл. На каждой итерации цикла происходит апдейт
     * значения прогресса выполнения таска для отображения в прогресс бар в интерфейсе.
     * @param file - файл, который мы скачиваем.
     * @param path - путь, куда мы хотим загружать файл.
     * @return - возвращает таск, который будет загружать файл.
     */
    public Task getTaskDownload(FileForTorrent file, String path) {
        download = new Task() {
            @Override
            protected Object call() throws IOException {
                if (!sendOutput("requestForFileDownload + " + file.getName())) {
                    return false;
                }
                System.out.println(file.getName() + " downloading");
                try {
                    System.out.println("path " + path);
                    File ffile = new File(path, file.getName());
                    fout = new FileOutputStream(ffile.getAbsolutePath());
                    int i = (int) Math.ceil(file.getSize() / 1024.0);
                    System.out.println("we have " + i + " blocks");
                    for (int j = 0; j < i; ++j) {
                        byte[] buffer = (byte[]) socketInput.readObject();
                        fout.write(buffer);
                        this.updateProgress(j + 1, i);
                        System.out.println("block " + j + " done");
                    }
                    fout.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    fout.close();
                    return false;
                }
                return true;
            }
        };
        return download;
    }

    /***
     * Метод для запуска загрузки файла. Настраивается возвращение
     * результата выполнения, действия при окончании, затем запускается
     * тред с таском для загрузки.
     * @return - статус успешности выполнения.
     */
    public boolean downloadStart() {
        final boolean[] result = new boolean[1];

        download.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                result[0] = (boolean) download.getValue();
                System.out.println("finished " + result[0]);
            }
        });
        Thread t = new Thread(download);
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return result[0];
    }

    /***
     * Метод для завершения работы клиента - отправляется запрос на закрытие
     * подключения серверу, закрываются входной и выходной поток, закрывается сокет.
     * @return - статус успешности выполнения закрытия.
     */
    public boolean closeSocket() {
        try {
            sendOutput("exit");
            clientSocket.close();
            socketInput.close();
            socketOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
