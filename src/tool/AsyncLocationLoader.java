package tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AsyncLocationLoader {
    // 用于存储键值对的Map
    private Map<String, String> locationMap = new HashMap<>();

    // 异步加载txt文件内容到内存
    public CompletableFuture<Void> loadLocationsAsync(String filePath) {
        return CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        locationMap.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 通过数值获取对应的地区名
    public String getLocation(String key) {
        return locationMap.get(key);
    }
}
