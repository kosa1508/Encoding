package tests;

import org.junit.Test;
import src.Encoding;

public class EncodingTest {
    @Test
    public void test() {
        // Тестовые числа, покрывающие все варианты кодирования
        long[] testNumbers = {
                75,         // 1 байт (0xxxxxxx)
                128,        // 2 байта (10xxxxxx xxxxxxxx)
                16383,      // Максимальное 2-байтное
                16384,      // 4 байта (110xxxxx ...)
                1_000_000,  // 4-байтное число
                1_000_000_000_000L  // 8 байт (1110xxxx ...)
        };

        // кодируем числа в байты
        byte[] encoded = Encoding.encode(testNumbers);
        // используем специальную функцию для вывода данных
        printEncodedBytes(encoded);

        // декодируем данные и получаем исходные значения
        long[] decoded = Encoding.decode(encoded);
        for (long numero : decoded) {
            System.out.println(numero);
        }
    }
    // функция для вывода закодированных байтов
    private static void printEncodedBytes(byte[] bytes) {
        int i = 0;
        while (i < bytes.length) {
            byte firstByte = bytes[i];
            int length = 0;

            // Находим сколькими байтами закодировано число
            if ((firstByte & 0x80) == 0) {
                length = 1;
            } else if ((firstByte & 0xC0) == 0x80) {
                length = 2;
            } else if ((firstByte & 0xE0) == 0xC0) {
                length = 4;
            } else if ((firstByte & 0xF0) == 0xE0) {
                length = 8;
            }

            // Выводим байты текущего числа
            for (int j = 0; j < length; j++) {
                System.out.print(String.format("%8s",
                                Integer.toBinaryString(bytes[i + j] & 0xFF))
                        .replace(' ', '0') + " ");
            }
            System.out.println();

            // Записываем сколько считали байтов
            i += length;
        }
    }
}
