package ru.danilakondr.netalbum.api.utils;

import java.nio.charset.StandardCharsets;
import com.google.common.hash.Hashing;

public class FileIdGenerator {
    /**
     * Генератор кода файла на основе пути к файлу.
     *
     * @param name путь к файлу
     * @return 64-битный хэш
     */
    public static long generate(String name) {
        System.out.println(name);
        byte[] data = name.getBytes(StandardCharsets.UTF_8);

        var hashFn = Hashing.sipHash24();
        var hashCode = hashFn.hashBytes(data);

        return hashCode.padToLong();
    }
}
