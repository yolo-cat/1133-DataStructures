public class Queue1 {
    private static final int MAX_SIZE = 5;
    private Object[] data = new Object[MAX_SIZE];
    private int front = 0;
    private int rear = 0;
    private int size = 0;

    // 入隊
    public void enqueue(Object item) {
        if (size == MAX_SIZE) {
            System.out.println("空間不足");
            return;
        }
        data[rear] = item;
        rear = (rear + 1) % MAX_SIZE;
        size++;
    }

    // 出隊
    public Object dequeue() {
        if (size == 0) {
            System.out.println("空間淨空");
            return null;
        }
        Object item = data[front];
        front = (front + 1) % MAX_SIZE;
        size--;
        return item;
    }

    // 檢查是否為空
    public boolean isEmpty() {
        return size == 0;
    }

    // 檢查是否為滿
    public boolean isFull() {
        return size == MAX_SIZE;
    }

    // 列印 queue 內容
    public void printQueue() {
        System.out.print("Queue: ");
        for (int i = 0; i < size; i++) {
            System.out.print(data[(front + i) % MAX_SIZE] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Queue1 q = new Queue1();
        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (true) {
            System.out.println("1. 加入 2. 排出 3. 離開");
            int op = sc.nextInt();
            if (op == 1) {
                System.out.print("請輸入要加入的值: ");
                Object val = sc.next();
                q.enqueue(val);
            } else if (op == 2) {
                Object out = q.dequeue();
                if (out != null) System.out.println("排出: " + out);
            } else if (op == 3) {
                break;
            }
            q.printQueue();
        }
        sc.close();
    }
}
