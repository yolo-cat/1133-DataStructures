import java.util.HashMap;

public class HashSearch {
    public static Main.Position search(HashMap<Integer, Main.Position> hashMap, int target) {
        // 只計算搜尋時間，不包含取得位置的時間
        return hashMap.get(target);
    }

    // 只檢查是否存在，不取得位置
    public static boolean containsKey(HashMap<Integer, Main.Position> hashMap, int target) {
        return hashMap.containsKey(target);
    }
}
