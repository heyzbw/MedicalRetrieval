## 基于all-documents-vue-master进行二次开发

> 关于怎么部署项目（有问题就来问我吧，我可能会说漏）

前端运行项目：

1. ``npm install``

如果在安装时出现node-sass版本问题的话，可以试试这个指令：(就是换个源，这玩意就是一个坑没办法)
``npm config set sass_binary_site=https://npm.taobao.org/mirrors/node-sass``

2. 运行``npm run serve``，没有报错然后访问[http://localhost:8080](http://localhost:8080)没问题就ok

3. 打包``npm run build``（运行DEV版本不需要打包，后期再弄打包部署）

>注意版本nodejs的版本，前端会有很多问题，不推荐使用最新的版本
>
>我的nodejs版本是v12.13.0




后端使用的依赖相对比较多：

1. 打开**mongodb** ``mongod --dbpath D:\software\compiler\mongoDB\data``

   - 推荐使用`Navicat` or `MongoDBCompass`作为可视化管理工具，自己人谁用命令行来运行mongodb啊。
   - 如果有修改MongoDB的配置，一定要在YML中修改，基本上上默认就行，没有的库与表会在第一次使用时创建。
   - mongoDB一定要是 >=3.6.0(我一开始使用3.4报错，换成了6.x就没事了)

2. 打开redis(如果本地redis有密码一定要去yml中配置)

   - 在后端项目中，打开``根目录—src—main—resource—application.yml``(所有spring项目通用的)，在spring.redis.password，我这里本地的密码为123456，如果默认没有密码就不要``:123456``

     ```yml
     spring:
       redis:
         database: 0
         host: ${REDIS_HOST:127.0.0.1}
         password: ${REDIS_PWD:123456}
         port: ${REDIS_PORT:6379}
         timeout: 3000
         jedis:
           pool:
             # 连接池中的最大空闲连接
             max-idle: 500
             # 连接池中的最小空闲连接
             min-idle: 50
             # 连接池最大连接数（使用负值表示没有限制）
             max-active: 2000
             # 连接池最大阻塞等待时间（使用负值表示没有限制）
             max-wait: 1000
         testOnBorrow: true
     ```

     

3. 打开es相关的几个组件
   运行``elasticsearch\bin\elasticsearch.bat``
   在**elasticsearch-head-master**运行``npm run start``
   运行``kibana-7.6.1-windows-x86_64\bin\kibana.bat`` 

   - 嫌麻烦的可以将这些命令封到一个.bat文件中（window下）,自己改路径就行

     ```bash
     start cmd /k "cd .\elasticsearch-7.6.1-windows-x86_64\elasticsearch-7.6.1\bin && call elasticsearch.bat"
     start cmd /k "cd .\kibana-7.6.1\kibana-7.6.1\bin && call kibana.bat"
     start cmd /k "cd .\elasticsearch-head-master && npm run start"
     start cmd /k "mongod --dbpath D:\software\compiler\mongoDB6.0Data"
     ```

   > 由于默认情况下是不会对base64进行解码的，所有要手动配置一下，可以参考一下这个[blog](https://www.cnblogs.com/strongchenyu/p/13777596.html)

   具体做法如下：

   1. 安装文本抽取插件：(在es的根目录运行这个命令)

      ```bash
      ./bin/elasticsearch-plugin install ingest-attachment
      ```

   2. 打开Kibana的dev tools，定义文本抽取的管道(记得去``elasticsearch-head-master``处删除原本的**docwrite**索引)

      ```json
      PUT /_ingest/pipeline/attachment
      {
          "description": "Extract attachment information",
          "processors": [
              {
                  "attachment": {
                      "field": "content",
                      "ignore_missing": true
                  }
              },
              {
                  "remove": {
                      "field": "content"
                  }
              }
          ]
      }
      ```

   3. 定义文档结构映射(就是各种编码与解码方式)

      ```json
      PUT /docwrite
      {
        "mappings": {
          "properties": {
            "id":{
              "type": "keyword"
            },
            "name":{
              "type": "text",
              "analyzer": "ik_max_word"
            },
            "type":{
              "type": "keyword"
            },
            "attachment": {
              "properties": {
                "content":{
                  "type": "text",
                  "analyzer": "ik_smart"
                }
              }
            }
          }
        }
      }
      
      ```

      每一步都是返回**acknowledge:true**就ok

4. 运行springboot项目，直接使用idea来启动就行，我现在的版本下如果运行后出现这个表示ok，后面就能直接使用该项目了

![image-20230307191227017](./../../AppData/Roaming/Typora/typora-user-images/image-20230307191227017.png)

5. Python OCR，由于我们项目使用的OCR是通过Python实现的，所以还有加上一步

   1. 打开OCR项目，安装依赖（主要是EasyOCR），目前我没有遇到版本问题，就用Pycharm默认的版本就行
   2. 将项目的根目录下的``utils.py``替换EasyOCR库中的原文件(在External Libraries里面一层一层往下翻就是)，否则无法识别中文
   3. 运行``main.py``打开Flask就行，出现如下的输出，表示OK

   ![image-20230307191711014](./../../AppData/Roaming/Typora/typora-user-images/image-20230307191711014.png)

!!如果遇到bug或者运行不通的地方，可以上网查查，或者问问我（我可能会说漏）!!

