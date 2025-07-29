import java.util.*;
import java.util.stream.IntStream;

public class BinaryTreeArray {
    private Integer[] treeArray;

    public BinaryTreeArray(Integer[] arr) {
        this.treeArray = arr;
    }

    // 工具方法：取得有效索引範圍（1 ~ arr.length-1）
    private int getLeft(int index) { return 2 * index; }
    private int getRight(int index) { return 2 * index + 1; }
    private boolean isValidIndex(int index) { return index > 0 && index < treeArray.length; }

    // 前序走訪
    public List<Integer> preorder() {
        List<Integer> result = new ArrayList<>();
        preorderHelper(1, result);
        return result;
    }

    private void preorderHelper(int index, List<Integer> result) {
        if (!isValidIndex(index) || treeArray[index] == null) return;
        result.add(treeArray[index]);
        preorderHelper(getLeft(index), result);
        preorderHelper(getRight(index), result);
    }

    // 中序走訪
    public List<Integer> inorder() {
        List<Integer> result = new ArrayList<>();
        inorderHelper(1, result);
        return result;
    }

    private void inorderHelper(int index, List<Integer> result) {
        if (!isValidIndex(index) || treeArray[index] == null) return;
        inorderHelper(getLeft(index), result);
        result.add(treeArray[index]);
        inorderHelper(getRight(index), result);
    }

    // 後序走訪
    public List<Integer> postorder() {
        List<Integer> result = new ArrayList<>();
        postorderHelper(1, result);
        return result;
    }

    private void postorderHelper(int index, List<Integer> result) {
        if (!isValidIndex(index) || treeArray[index] == null) return;
        postorderHelper(getLeft(index), result);
        postorderHelper(getRight(index), result);
        result.add(treeArray[index]);
    }

    // 靜態方法：將字串陣列轉為 Integer 陣列（支援 null）
    public static Integer[] parseInput(String input) {
        String[] parts = input.split(",");
        Integer[] arr = new Integer[parts.length + 1]; // arr[0] 保留
        arr[0] = 0; // 節點數量初始化為0
        for (int i = 0; i < parts.length; i++) {
            String s = parts[i].trim();
            if (s.equalsIgnoreCase("null")) arr[i + 1] = null;
            else arr[i + 1] = Integer.parseInt(s);
            if (!s.equalsIgnoreCase("null")) arr[0]++;
        }
        return arr;
    }

    // 刪除指定索引的節點
    // 若為樹葉節點則直接刪除，若為中間節點則刪除整個子樹
    public void delete(int index) {
        if (!isValidIndex(index) || treeArray[index] == null) return;
        int left = getLeft(index);
        int right = getRight(index);
        boolean isLeaf = (!isValidIndex(left) || treeArray[left] == null)
                && (!isValidIndex(right) || treeArray[right] == null);
        if (isLeaf) {
            treeArray[index] = null;
        } else {
            deleteSubtree(index);
        }
    }

    // 遞迴刪除以 index 為根的子樹
    private void deleteSubtree(int index) {
        if (!isValidIndex(index) || treeArray[index] == null) return;
        treeArray[index] = null;
        deleteSubtree(getLeft(index));
        deleteSubtree(getRight(index));
    }

    // 測試主程式
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("請輸入以逗號分隔的陣列（用null表示空節點）：");
        System.out.println("或輸入 auto:N 由程式自動產生 N 個不重覆隨機數字");
        String input = sc.nextLine();
        Integer[] arr;
        if (input.trim().toLowerCase().startsWith("auto:")) {
            int n = 0;
            try {
                n = Integer.parseInt(input.trim().substring(5));
            } catch (Exception e) {
                System.out.println("格式錯誤，請輸入 auto:N，例如 auto:10");
                return;
            }
            if (n <= 0) {
                System.out.println("N 必須大於 0");
                return;
            }
            Set<Integer> nums = new LinkedHashSet<>();
            Random rand = new Random();
            while (nums.size() < n) {
                nums.add(rand.nextInt(10000)); // 可調整範圍
            }
            arr = new Integer[n + 1];
            arr[0] = n;
            int i = 1;
            for (int num : nums) arr[i++] = num;
        } else {
            arr = parseInput(input);
        }
        BinaryTreeArray tree = new BinaryTreeArray(arr);
        System.out.println("索引:   " + Arrays.toString(IntStream.range(1, arr.length).toArray()));
        System.out.println("內容:   " + Arrays.toString(Arrays.copyOfRange(arr, 1, arr.length)));
        System.out.println("節點數: " + arr[0]);
        System.out.println("中序: " + tree.inorder());
        System.out.println("前序: " + tree.preorder());
        System.out.println("後序: " + tree.postorder());

        while (true) {
            System.out.println("請選擇操作: 1) 增加節點 2) 刪除節點 3) 退出");
            String op = sc.nextLine();
            if (op.equals("1")) {
                System.out.print("請輸入要插入的索引: ");
                int idx = Integer.parseInt(sc.nextLine());
                System.out.print("請輸入要插入的值(整數): ");
                String valStr = sc.nextLine();
                Integer val = valStr.equalsIgnoreCase("null") ? null : Integer.parseInt(valStr);
                if (idx > 0 && idx < arr.length) {
                    // 若原本為null且新值非null，節點數+1
                    if (arr[idx] == null && val != null) arr[0]++;
                    // 若原本非null且新值為null，節點數-1
                    if (arr[idx] != null && val == null) arr[0]--;
                    arr[idx] = val;
                    System.out.println("已插入於索引 " + idx);
                } else {
                    System.out.println("索引超出範圍");
                }
            } else if (op.equals("2")) {
                System.out.print("請輸入要刪除的索引: ");
                int idx = Integer.parseInt(sc.nextLine());
                // 遞迴計算刪除節點數
                int[] delCount = new int[]{0};
                countDelete(arr, idx, delCount);
                tree.delete(idx);
                arr[0] -= delCount[0];
                System.out.println("已刪除索引 " + idx);
            } else if (op.equals("3")) {
                System.out.println("程式結束");
                break;
            } else {
                System.out.println("無效選項");
            }
            System.out.println("索引:   " + Arrays.toString(IntStream.range(1, arr.length).toArray()));
            System.out.println("內容:   " + Arrays.toString(Arrays.copyOfRange(arr, 1, arr.length)));
            System.out.println("節點數: " + arr[0]);
            System.out.println("中序: " + tree.inorder());
            System.out.println("前序: " + tree.preorder());
            System.out.println("後序: " + tree.postorder());
        }
    }

    // 遞迴計算將被刪除的節點數
    private static void countDelete(Integer[] arr, int idx, int[] count) {
        if (idx <= 0 || idx >= arr.length || arr[idx] == null) return;
        count[0]++;
        countDelete(arr, 2 * idx, count);
        countDelete(arr, 2 * idx + 1, count);
    }
}
