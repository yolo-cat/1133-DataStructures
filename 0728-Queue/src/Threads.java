public class Threads {
    // 方法一：繼承Thread類別
    static class MyThread extends Thread {
        public void run() {
            System.out.println("[MyThread] 啟動");
            try {
                System.out.println("[MyThread] 執行中");
                Thread.sleep(500); // 模擬延遲500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[MyThread] 結束");
            System.out.println("方法一：繼承Thread類別");
        }
    }

    // 方法二：實作Runnable介面
    static class MyRunnable implements Runnable {
        public void run() {
            System.out.println("[MyRunnable] 啟動");
            try {
                System.out.println("[MyRunnable] 執行中");
                Thread.sleep(500); // 模擬延遲500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[MyRunnable] 結束");
            System.out.println("方法二：實作Runnable介面");
        }
    }

    public static void main(String[] args) {
        // 方法一
        Thread t1 = new MyThread();
        t1.start();

        // 方法二
        Thread t2 = new Thread(new MyRunnable());
        t2.start();

        // 方法三：使用Lambda表達式
        Thread t3 = new Thread(() -> {
            System.out.println("[Lambda] 啟動");
            try {
                System.out.println("[Lambda] 執行中");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[Lambda] 結束");
            System.out.println("方法三：Lambda 表達式");
        });
        t3.start();
    }
}
