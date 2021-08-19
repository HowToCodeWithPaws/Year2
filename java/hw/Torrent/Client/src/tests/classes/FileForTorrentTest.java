package classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/***
 * Тесты для класса файла. Наиболее осмысленны.
 * Тестируют то, что все геттеры и сеттеры работают как надо.
 */
class FileForTorrentTest {

    @Test
    void getPath() {
        FileForTorrent file = new FileForTorrent("path", "name", 123L);
        assertEquals("path", file.getPath());
        assertNotEquals("path1", file.getPath());
    }

    @Test
    void getName() {
        FileForTorrent file = new FileForTorrent("path", "name", 123L);
        assertEquals("name", file.getName());
        assertNotEquals("name1", file.getName());
    }

    @Test
    void getDownloadedTo() {
        FileForTorrent file = new FileForTorrent("path", "name", 123L);
        assertEquals("no", file.getDownloadedTo());
        assertNotEquals("here", file.getDownloadedTo());
    }

    @Test
    void setDownloadedTo() {
        FileForTorrent file = new FileForTorrent("path", "name", 123L);
        assertEquals("no", file.getDownloadedTo());
        file.setDownloadedTo("here");
        assertEquals("here", file.getDownloadedTo());
    }

    @Test
    void getSize() {
        FileForTorrent file = new FileForTorrent("path", "name", 123L);
        assertEquals(123L, file.getSize());
        assertEquals(123, file.getSize());
        assertNotEquals(122, file.getSize());
    }
}