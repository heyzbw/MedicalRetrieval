## 赛题要求
（1）能够上传任意 PDF 进行归档。  
（2）通过搜索栏，可以将包含搜索内容的文档搜索出来，搜索结果精确到该内容在 PDF 中的页数，并且摘要命中段落，段落长度 50 字左右。 
（3）可以搜索 PDF 中所有图片里包含的信息，搜索结果精确到该内容在 PDF 中的页 数。 
（4）用户可以对每个搜索结果进行反馈：赞、踩。 
（5）对搜索结果计算命中得分（满分 100），命中得分高排序靠前。 得分权重：内容相关性:点击率:用户反馈 6:3:1 即：内容相关性越高，得分越高，最高 60 分 文献点击率越高，得分越高，最高 30 分 该文献搜索用户反馈得赞数越高，得分越高，最高 10 分 （6）搜索结果展示总分以及各搜索维度的得分情况。 
（7）搜索的文献可以在线阅读，也可以进行下载

## 直播答疑

1. 要做前后端分离
>（可以使用前后端分离的框架来搭建比如以java为后端的SSM or SSH）
2. 使用H5作为展示系统，移动端和pc端最好都要
>tips：个人认为将他们分开为两个项目，可以将他们两个做一个关联，比如使用移动端查询后收藏，然后使用pc端做阅读、笔记记录等功能（反正就是各有倾向）
3. 可以在本地跑，也可以部署到云服务器，不要求注册域名
>（这个以后在考虑，本身部署到linux云服务器也不难）
4. OCR准确率92%以上，搜索的速度在2-3s也可以接受
**（速度方面可以考虑使用es搜索，我不太懂，以后可以慢慢看）**
5. 用户交互方面不做要求，评价系统不是核心模块
>（用户交互方面使用前端模板来做可以到达很多的用户交互体验，而且很方便，比如可以借用*若依*的系统或者[后台模板](https://panjiachen.github.io/vue-element-admin/#/login?redirect=%2Fdashboard)）
6. 自己添加数据集，最好是医学的

<br><br>

## 自己收集的一些资料
### 关于ES
[Elasticsearch 快速入门 from zhihu](https://zhuanlan.zhihu.com/p/54384152)
**分布式**  **RESTful** 风格的搜索和数据分析引擎
java小demo
```java
//创建RestHighLevelClient并调用
RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                    new HttpHost("localhost", 9200, "http")));
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
searchSourceBuilder.query(QueryBuilders.matchAllQuery());            
searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10));
SearchRequest searchRequest = new SearchRequest();
searchRequest.indices("social-*");
searchRequest.source(searchSourceBuilder);
SearchResponse searchResponse = client.search(searchRequest);
```

[ ElasticSearch 实战_ from CSDN](https://blog.csdn.net/weixin_41997327/article/details/113762424?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522167539524216800192241446%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=167539524216800192241446&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-113762424-null-null.142^v72^insert_down2,201^v4^add_ask&utm_term=elasticsearch%E5%AE%9E%E6%88%98&spm=1018.2226.3001.4187)
这个项目主要是使用了springboot来搭建一个简单的es检索
> 界面项目head记得要全局安装grunt
```
npm install -g grunt-cli  //全局安装
npm install -g grunt  //进入项目安装
```

es项目记得配置跨域，和spring项目一样，在yml中添加cors配置
```yml
http.cors.enabled : true
http.cors.allow-origin: "*"
```
>新建索引时记得不能有大写字母，否者无法创建

我是用的node版本是14.10

最新版本的es记得要去关闭**ssl**和**geoip**数据库更新
```
xpack.security.http.ssl:enabled:false
```

在pom.xml中使用maven配置es的版本信息**(一定要和本地的es版本一致，否则会报错)**

剩下的内容可以参考官方文档或者这个up的视频
>新建、获取、删除索引等操作


具体的可以参考一下b战的一个教程
>[ES教程from_bilibili](https://www.bilibili.com/video/BV17a4y1x7zq/?p=20&spm_id_from=pageDriver&vd_source=7a797772f005ad1b17042b72508c19d3)

-----
在github上看到一个很贴近我们的项目，使用的栈为vue+springboot+mongdb
[仓库链接](https://github.com/Jarrettluo/document-sharing-site)（包括了前端和后端）
[体验demo网站](http://81.69.247.172/#/)
tips：他的前端的node版本一定要是14.xx不要使用最新版本，否则node-sass版本会报错
前端：控制台运行
```
npm install //安装依赖
npm run serve //运行
npm run build //打包部署
```
后端：他的maven配置有点问题我也搞不懂为啥，去**maven repository**查了一下可能是版本更新，要把**pom.xml**里面的**org.elasticsearch**改成**testcontainers**
然后将application.yml的mongdb配置好就行，其他的部分和springboot差不多。

>整个框架的逻辑就是，基本就是**controller->service->持久化层**，主要是检索部分使用了es我没太看懂，前端部分的界面设计我们可以参考一下，他没有使用UI模板，我认为可以借助elementUI or uview 来快速开发WEB前端。

>我们可以参考一下他这个项目，但是我们将文献检索部分自己做成一个elasticsearch，封装到util中（如果是java的话），如果是python的话封装为**flask**接口，用java做请求转发。



