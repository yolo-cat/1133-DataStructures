加入mp3播放功能: 使用javaFX
mediaplayer library https://gluonhq.com/products/javafx/


3. 導入 JavaFX SDK
   方法一：以 Library 形式導入
   右鍵點擊專案，選擇 Open Module Settings (或按 F4)。
   點擊左側 Libraries，然後點選右側的「+」 > Java。
   選擇剛才解壓縮的 JavaFX SDK 資料夾下的 lib 資料夾，然後全選裡面的所有 .jar 檔案，點擊 OK。
   確認 Library 被加到了你的專案。
   方法二：使用 Maven/Gradle（建議）
   Maven pom.xml 加入：

XML
<dependencies>
<dependency>
<groupId>org.openjfx</groupId>
<artifactId>javafx-controls</artifactId>
<version>21</version>
</dependency>
  <!-- 根據需要加入其他模組 -->
</dependencies>
Gradle build.gradle 加入：

Groovy
dependencies {
implementation 'org.openjfx:javafx-controls:21'
// 根據需要加入其他模組
}
4. 設定 VM options（執行設定）


不要只用 classpath，而是要加上這些 VM options：

Code
--module-path /Users/Guest/Downloads/javafx-sdk-24/lib --add-modules javafx.controls,javafx.fxml,javafx.media
步驟（IntelliJ IDEA）
點選右上角執行設定下拉選單 → 選「Edit Configurations…」
找到你的執行設定（或 Application 節點下的 main class）
把上面這一行貼到 VM options 欄位