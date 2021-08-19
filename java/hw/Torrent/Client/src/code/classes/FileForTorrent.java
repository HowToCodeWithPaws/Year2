package classes;

import java.io.Serializable;

/***
 * Класс для файла при работе клиента. Содержит важную информацию, которую
 * надо знать о файле.
 */
public class FileForTorrent implements Serializable {
    private final Long size;
    private final String path;
    private final String name;
    private String downloadedTo;

    /***
     * Конструктор с параметрами. У файла задается название, директория, в которой
     * он находится на сервере, размер. Директория, в которую файл скачан задается
     * как "no".
     * @param path_ - директория, с которой можно загружать файлы с сервера.
     * @param name_ - имя файла.
     * @param size_ - размер файла в байтах.
     */
    public FileForTorrent(String path_, String name_, Long size_) {
        path = path_;
        name = name_;
        size = size_;
        downloadedTo = "no";
    }

    /***
     * Метод для получения директории файла на сервере.
     * @return - директория, с которой работает сервер.
     */
    public String getPath() {
        return path;
    }

    /***
     * Метод для получения названия файла.
     * @return - имя файла.
     */
    public String getName() {
        return name;
    }

    /***
     * Метод для получения директории, в которую клиент загрузил файл.
     * @return - директория, в которую файл загружен.
     */
    public String getDownloadedTo() {
        return downloadedTo;
    }

    /***
     * Метод для установки директории, в которую файл загружен.
     * Изменяется при скачивании куда-то и при удалении оттуда.
     * @param value - значение директории, куда файл скачали.
     */
    public void setDownloadedTo(String value) {
        downloadedTo = value;
    }

    /***
     * Метод для получения размера файла.
     * @return - размер файла в байтах.
     */
    public Long getSize() {
        return size;
    }
}