import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/***
 * Класс для сервера, обслуживающего торрент. Он наследует тред, он запускается.
 * У него есть коннекшн хендлер, который настраивает подключения, директория,
 * с которой он работает, свой сокет, сокет подключенного клиента, входной и выходной потоки,
 * массив найденных в директории файлов, сканнер для ввода из терминала,
 * флаги для того, пора ли закончить подключение и счетчик клиентов, с которыми
 * сейчас работает сервер (нужно, чтобы его нельзя было закрыть, пока есть клиенты).
 */
public class Server extends Thread {
    static ConnectionHandler connectionHandler;
    static int serverPortNumber = 2345;
    static String CONTENT_DIR;
    static ServerSocket connectionSocket;
    Socket connectedClient;
    ObjectInputStream inputStream;
    ObjectOutputStream socketOutput;
    boolean exit = false;
    static Integer inWork = 0;
    File[] pathFiles;
    static Scanner scanner;

    /***
     * Конструктор с параметром сокета, по которому подключился клиент. Создаются
     * входной и выходной потоки, далее сервер работает.
     * @param socket - подключенный клиент.
     */
    Server(Socket socket) {
        inWork++;
        connectedClient = socket;
        try {
            inputStream = new ObjectInputStream(connectedClient.getInputStream());
            socketOutput = new ObjectOutputStream(connectedClient.getOutputStream());
        } catch (Exception e) {
            System.out.println("Произошла ошибка: ");
            e.printStackTrace();
            System.out.println("Попробуйте снова.");
        }
    }

    /***
     * Метод работы треда сервера. Он пытается получить запрос клиента до тех
     * пор, пока знаечение закрытия подключения не станет правдой. Тогда он закрывает
     * потоки ввода и вывода и сокет клиента.
     */
    public void run() {
        try {
            do {
                receiveMessage();
            } while (!exit);

            inputStream.close();
            socketOutput.close();
            connectedClient.close();
        } catch (Exception e) {
            System.out.println("Произошла ошибка: ");
            e.printStackTrace();
            System.out.println("Попробуйте снова.");
        }
    }

    /***
     * Метод для получения запроса от клиента. Считывается сообщение из входного
     * потока, далее открывается меню по этому сообщению.
     * @return
     */
    boolean receiveMessage() {
        try {
            String get = (String) inputStream.readObject();
            System.out.println("Получен запрос " + get + ".");
            menu(get);
        } catch (Exception e) {
            System.out.println("Произошла ошибка: ");
            e.printStackTrace();
            System.out.println("Попробуйте снова.");
            return false;
        }
        return true;
    }

    /***
     * Метод для собирания информации о файлах в директории, с которой работает сервер.
     * Он проходится по файлам, выбирает те, которые соответствуют регулярным файлам, создает из
     * информации о них и их размерах массив массивов строк.
     * @return - коллекция строк о файлах в директории.
     */
    ArrayList<ArrayList<String>> collectFilesInfo() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> sizes = new ArrayList<>();
        File testDirectory = new File(CONTENT_DIR);
        pathFiles = testDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        for (File file : pathFiles) {
            names.add(file.getName());
            sizes.add(((Long) file.length()).toString());
        }

        ArrayList<ArrayList<String>> namesAndSizes = new ArrayList<>();
        namesAndSizes.add(names);
        namesAndSizes.add(sizes);

        return namesAndSizes;
    }

    /***
     * Метод для направления ответов сервера в зависимости от запроса клиента.
     * Работает в сущности просто как свитч кейс по строке запроса.
     * Если клиент запрашивает выход, сервер задает переменную выхода
     * равной true и выходит из цикла ожидания сообщений.
     * Если клиент запрашивает файлы, сервер собирает и отправляет информацию
     * о них.
     * Если клиент запрашивает директорию, сервер отправляет ее название.
     * Если клиент запрашивает какой-то конкретный файл, вызывается метод его отправки.
     * @param key - строка, которую клиент отправил как запрос.
     */
    void menu(String key) {
        if (key.equals("exit")) {
            exit = true;
            inWork--;
        } else if (key.equals("requestForFiles")) {
            sendInfo(collectFilesInfo().get(0));
            sendInfo(collectFilesInfo().get(1));
        } else if (key.equals("requestDirectory")) {
            ArrayList<String> dir = new ArrayList<>();
            dir.add(CONTENT_DIR);
            sendInfo(dir);
            System.out.println("Директория передана.");
        } else if (key.contains("requestForFileDownload + ")) {
            String name = key.substring(key.indexOf("+") + 2);
            File temp = new File(CONTENT_DIR, name);
            sendFile(temp.getPath(), temp.length());
            System.out.println("Файл отправлен.");
        }
    }

    /***
     * Метод для отправки сообщения. Сообщение сериализуется и
     * записывается в выходной поток.
     * @param message - сообщение, которое мы отправляем клиенту.
     * @return - статус успешности выполнения отправки.
     */
    boolean sendInfo(ArrayList<String> message) {
        try {
            socketOutput.writeObject(message);
            socketOutput.flush();
        } catch (Exception e) {
            System.out.println("Произошла ошибка: ");
            e.printStackTrace();
            System.out.println("Попробуйте снова.");
            return false;
        }
        return true;
    }

    /***
     * Метод для отправки файла. Запрошенный файл разбивается на блоки по 1024 байта,
     * считывается из рабочей директории по этим кусочкам и ими же отправляется клиенту.
     * @param path - путь, по которому расположен файл, включая имя.
     * @param length - размер отправляемого файла.
     */
    void sendFile(String path, long length) {
        try {
            FileInputStream fin = new FileInputStream(path);
            System.out.println("Предстоит обработать " + Math.ceil(length / 1024.0) + " блоков.");
            int i = 0;
            while (length > 0) {
                int currentBlocklength = length < 1024 ? (int) length : 1024;
                byte[] buffer = new byte[currentBlocklength];
                fin.read(buffer);
                length -= currentBlocklength;
                ++i;
                socketOutput.writeObject(buffer);
                socketOutput.flush();
                System.out.println("блок " + i + " обработан");
            }
            fin.close();
        } catch (Exception e) {
            System.out.println("Произошла ошибка: ");
            e.printStackTrace();
            System.out.println("Попробуйте снова.");
        }
    }

    /***
     * Класс коннекшн хендлер, который отвечает за подключения и создает
     * новые объекты сервера по одному на каждый клиент, запускает их как треды.
     */
    static class ConnectionHandler extends Thread {
        ServerSocket serverSocket;

        ConnectionHandler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        public void run() {
            try {
                while (true) {
                    Socket connected = serverSocket.accept();
                    System.out.println("Произошло подключение, dataSocket = " + connected);
                    new Server(connected).start();
                }
            } catch (Exception ex) {
            }
        }
    }

    /***
     * Запускаемый метод класса, статический, создает новый коннекшн хендлер, получает название
     * рабочей директории с проверкой корректности, создает сокет для подключений, настраивает
     * закрытие по вводу "exit", подключения осуществляются до тех пор, пока не будет введен выход.
     * @param args - аргументы запуска.
     * @throws Exception -
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        boolean dir = false;
        System.out.println("Это сервер.\nЧтобы его выключить, введите \"exit\"," +
                " если не работает никакой клиент, иначе выключите клиента и введите \"exit\".");
        System.out.println("Введите, пожалуйста, директорию, из которой хотите передавать файлы:");
        String contDir = "";
        while (!dir) {
            try {
                contDir = scanner.nextLine();
                if (!Files.exists(Path.of(contDir))) {
                    System.out.println("Название директории " + contDir + " некорректно. Введите что-то другое.");
                } else {
                    dir = true;
                }
            } catch (InvalidPathException e) {
                dir = false;
                System.out.println("Название директории " + contDir + " некорректно. Введите что-то другое.");
            }
        }
        CONTENT_DIR = contDir;
        try {
            System.out.println("Директория, из которой будут загружаться файлы: " + CONTENT_DIR);
            connectionSocket = new ServerSocket(serverPortNumber);
            System.out.println("Простой TCP/IP сервер готов принять клиентов по адресу localhost:" + serverPortNumber);
            connectionHandler = new ConnectionHandler(connectionSocket);
            connectionHandler.start();
            while (!scanner.nextLine().equalsIgnoreCase("exit")) {
            }
            close();
        } catch (Exception e) {
            System.out.println("Произошла ошибка: ");
            e.printStackTrace();
            System.out.println("Попробуйте снова.");
        }
    }

    /***
     * Метод для закрытия сервера, закрываются сокеты, сканер, коннекшн хендлер.
     * @return - возвращает статус успешности закрытия.
     * @throws IOException - возможный эксепшен при закрытии потока сканнера.
     * @throws InterruptedException - возможный эксепшен при закрытии тредов.
     */
    static boolean close() throws IOException, InterruptedException {
        if (inWork <= 0) {
            connectionSocket.close();
            connectionHandler.join();
            scanner.close();
            System.out.println("Сервер выключен.");
            return true;
        } else {
            System.out.println("Сейчас нельзя выключить сервер. Сначала отключите клиент.");
        }
        return false;
    }

}
