本專案是為了Demo 如何將 Springboot專案 製成docker image，並連上部署在docker的資料庫（運行中）
為了方便快速使用本專案，可使用專案資料夾中的 dbSmample.txt 建立相同資料庫環境

## 環境報備
- open jdk17
- MSSQL (docker container，image:mcr.microsoft.com/mssql/server:2017-latest)
- Macbook m1 
- SpringToolSuite4.app(eclipse) 建立專案
- Docker Engine v24.0.6
- Docker Desktop(喜視覺化介面可用)

## 在SpringBoot專案中，需要做什麼？
1. 建立檔案**Dockerfile**
＊直接建立在專案根目錄下
＊不需副檔名，檔名大小寫需一致
＊以下是檔案內容
```
# 使用官方的 OpenJDK 映像作為基礎
# 目前的springboot需要jdk以上，也可以替換其他你使用的版本映像

FROM openjdk:17-jdk-slim

# 建立容器中存放檔案的位置
WORKDIR /app

# 拷貝打包好的 Spring Boot JAR 檔案到容器中
# 基本上一個專案就是產出一個jar檔置於target下，為避免不泛用，直接使用*取代專案jar名
# 後面是要拷貝到容器中的app資料夾，並使用demo作為jar檔名
COPY target/*.jar /app/demo.jar

# 應用程式運行的port（根據你的port配置）
EXPOSE 8082

# 設置環境變數，將springboot專案轉為docker容器時使用
# 如果你的資料庫建立在本機，應該不用做此設定
ENV DATABASE_HOST=yourDBContainerName

# 定義容器啟動時運行的命令
CMD ["java", "-jar", "/app/demo.jar"
```
2. 修改資料庫連線的設定**application.properties**
**如果你的資料庫建立在本機，不用做此設定**
* 原本就使用docker 運行mssql的人應該也知道，我們在執行專案實用localhost就能順利連上資料庫了，但很抱歉，如果你要在container上運行資料庫，那就沒那麼順利了！
* 我自己的理解是，當你兩者環境都在container，就類似於在不同環境運行，這時候在使用localhost就不合理了，因此你需要讓兩個容器處於同一個網絡環境（簡言之，兩個container可以互ping），這個狀況下就能使用容器名稱來使用資料庫了（理解可能有誤，歡迎糾正補充！！）
* ${DATABASE_HOST:localhost}這個變數的意義是，在還沒建構Docker映像檔使用localhost(因為還未調用到Dockerfile設定)
```
spring.datasource.url=jdbc:sqlserver://${DATABASE_HOST:localhost}:1433;databaseName=yourDBName;encrypt=true;trustServerCertificate=true;
```

3. Maven建立jar檔
＊如果你在eclipse 環境，專案右鍵>Run As>Maven Clean>至console區看到Build Success後>專案右鍵>Run As>Maven Install，成功的話到專案>target，會看到xxxxx.jar就是你的專案jar包
＊應該也有其他方式，總之就是產生的jar包要在target，不然Dockerfile文件要修改一下

*以上完成後，我們進行Docker設定*

## Docker有哪些設定？
這邊都是用docker指令完成，請開啟終端機
1. Docker創建自定義網路 //my-network為自訂網路名稱
`docker network create my-network`
2. 將已存在的資料庫（mydb）連上自定義網路：
`docker network connect my-network mydb`
*補充.如果是要新創建一個資料庫容器(mydb)：*
`docker run -d --name mydb --network my-network <資料庫容器參照的image號>
`

3. 將springboot專案做成Docker映像檔
- 終端機位置移動到專案跟目錄
- docker-springboot-demo是自訂映像檔名稱
- ` docker build -t docker-springboot-demo .`
- 將映像檔做成docker容器 //mycontainer是容器名稱
`docker run -d --name mycontainer --network my-network -p 8082:8082 <docker-springboot-demo 的image編號>`

以上完成後，容器應該就執行成功囉！
