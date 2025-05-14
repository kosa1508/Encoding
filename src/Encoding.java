import java.util.ArrayList;
import java.util.List;

public class Encoding {
    public static byte[] encode(long[] numbers) {
        // Введем список в который будем помещать закодированные байты
        List<Byte> byteList = new ArrayList<>();

        for (long numero : numbers) {

            if (numero < 128) {
                // Рассмотрим числа размером в 1 байт
                // Добавим число, переведя его в байт
                byteList.add((byte) numero);
            } else if (numero < 16384) {
                // Числа по 2 байта
                // Рассмотрим максимальный байт и сформируем маску
                // 10xxxxxx xxxxxxxx
                byteList.add((byte) (0x80 | (numero >> 8)));
                // Добавим последний байт (стандартная операция, которую
                // будем использовать для всех, кроме максимальных байтов)
                byteList.add((byte) (numero & 0xFF));
            } else if (numero < 536870912) {  // 2^29
                // Числа по 4 байта
                // Сформируем маску
                // 110xxxxx xxxxxxxx xxxxxxxx xxxxxxxx
                // за счет максимального байта
                byteList.add((byte) (0xC0 | (numero >> 24)));
                byteList.add((byte) ((numero >> 16) & 0xFF));
                byteList.add((byte) ((numero >> 8) & 0xFF));
                byteList.add((byte) (numero & 0xFF));
                // можно также использовать побитовый сдвиг для
                // задания границы 1_152_921_504_606_846_976L
            } else if (numero < 1_152_921_504_606_846_976L) {
                // 8 байтов
                // Сформируем маску 1110xxxx xxxxxxxx ... xxxxxxxx
                byteList.add((byte) (0xE0 | (numero >> 56)));
                byteList.add((byte) ((numero >> 48) & 0xFF));
                byteList.add((byte) ((numero >> 40) & 0xFF));
                byteList.add((byte) ((numero >> 32) & 0xFF));
                byteList.add((byte) ((numero >> 24) & 0xFF));
                byteList.add((byte) ((numero >> 16) & 0xFF));
                byteList.add((byte) ((numero >> 8) & 0xFF));
                byteList.add((byte) (numero & 0xFF));
            }
        }

        // Переведем все в тип byte[]
        byte[] result = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            result[i] = byteList.get(i);
        }
        return result;
    }

    public static long[] decode(byte[] codes) {
        // Введем список в который будем помещать закодированные байты
        List<Long> numbers = new ArrayList<>();
        int i = 0;

        while (i < codes.length) {
            byte firstByte = codes[i];
            long numero = 0;
            // Создадим счетчик декодированных байтов для своевременного окончания цикла-While
            int bytesToRead = 0;

            //Проверка, что число закодировано одним байтом
            if ((firstByte & 0x80) == 0) {
                // Считываем число - делаем побитовое И для маски с "1" на последних 8 битах
                numero = firstByte & 0xFF;
                bytesToRead = 1;
                // Проверка на число длиной 2 байта
            } else if ((firstByte & 0xC0) == 0x80) {
                bytesToRead = 2;
                // Обработка 2 байтов через сдвиг на 8 битов и префикс "10"
                numero = ((long) (firstByte & 0x3F) << 8) |
                        (codes[i + 1] & 0xFF);
                // Проверка на число длиной 4 байта
            }  else if ((firstByte & 0xE0) == 0xC0) {
                // Обработка 4 байтов через сдвиг битов и префикс "110"
                bytesToRead = 4;
                numero = ((long) (firstByte & 0x1F) << 24) |
                        ((long) (codes[i + 1] & 0xFF) << 16) |
                        ((long) (codes[i + 2] & 0xFF) << 8) |
                        (codes[i + 3] & 0xFF);
                // Проверка на число длиной 8 байтов
            } else if ((firstByte & 0xF0) == 0xE0) {
                // Обработка 4 байтов через сдвиг битов и префикс "1110"
                bytesToRead = 8;
                numero = ((long) (firstByte & 0x0F) << 56) |
                        ((long) (codes[i + 1] & 0xFF) << 48) |
                        ((long) (codes[i + 2] & 0xFF) << 40) |
                        ((long) (codes[i + 3] & 0xFF) << 32) |
                        ((long) (codes[i + 4] & 0xFF) << 24) |
                        ((long) (codes[i + 5] & 0xFF) << 16) |
                        ((long) (codes[i + 6] & 0xFF) << 8) |
                        (codes[i + 7] & 0xFF);
            }
            // Добавление декодированных чисел
            numbers.add(numero);
            // Переменная с количеством считанных байтов
            i += bytesToRead;
        }

        // Переведем все в тип long[]
        long[] result = new long[numbers.size()];
        for (int j = 0; j < result.length; j++) {
            result[j] = numbers.get(j);
        }
        return result;
    }
}



