## 答疑记录

搜索关键字个数限制在100以内，也可以搜索一句话

手机端pc端都用H5页面做呈现,或者移动端都可以，只要能呈现效果

要做前后端分离

上传的文档存在云或者服务器本地都可以

可以做搜索记录的保存，加分项

不提供服务器，可以用自己的电脑达到演示效果就行，在演示的时候可以部署到阿里云服务器上，平时可以在本地部署

不用注册域名，ip访问即可

数据集自己找，尽量找医学相关的,官方提供了一点数据集的可以去看看

归档的数据不一定要存在数据库

中英文都要能够搜索

识别后的文本存在数据库方便搜索，或者是elasticsearch搜索引擎建立索引项，数据库的搜索能力不太能满足搜索要求，搜索引擎更好

OCR模型的准确率92%以上，这点相对于指导书上的98%做了调整

搜索的速度在2-3s也可以接受

刚上传的文献要马上可以搜索出来，上传进行预处理的文献就可以进行文字识别了，可以归档了

通过图片中的文字找出pdf在哪一页

用户界面不作为要求，做了锦上添花

用户评价（赞和踩）不作为考核标准

归档后的数据是格式化的表数据rowdata，数据源自己选择（数据库或者text文本都可以）

## 文献搜索功能实现不错的项目

https://github.com/Jarrettluo/document-sharing-site

他那个展示效果挺不错的（http://81.69.247.172/#/）

而且功能也很齐全，我们可以考虑在这个项目上进行改进，添加ocr文字功能，或者实现一些进阶功能（一些多词条的高级搜索功能）

## 项目组成部分的参考

https://martin.blog.csdn.net/article/details/103031863?spm=1001.2014.3001.5506

## es学习资料

es学习视频：https://www.bilibili.com/video/BV1hh411D7sb/?spm_id_from=333.337.search-card.all.click&vd_source=aef30d9017588bd7e04d2148f001628c

es实战视频：https://www.bilibili.com/video/BV1Nk4y1R7Hf/?spm_id_from=333.337.search-card.all.click&vd_source=aef30d9017588bd7e04d2148f001628c)

答疑视频中说要前后端分离，这个实战是前后端分离的，前面document-sharing-site那个项目不知道是不是

## 主要问题（es软件无法上传文档和图片ocr索引）

找到了3种解决方法

#### 三者功能实现和对比（Elasticsearch检索PDF和Office文档的方案测评）

https://yemilice.com/2020/07/29/elasticsearch%E6%A3%80%E7%B4%A2pdf%E5%92%8Coffice%E6%96%87%E6%A1%A3%E7%9A%84%E6%96%B9%E6%A1%88%E6%B5%8B%E8%AF%84/#3-Ambari%E6%9C%8D%E5%8A%A1

三者都为ES提供文件传输服务，后面两个fscrawle和 Ambari服务提到可以实现对pdf文档中图片的自动检索和ocr识别

#### 1、Ingest-Attachment

https://blog.csdn.net/m0_37739193/article/details/86421246

#### 2、fscrawler 插件

对于ocr实现部分的官方文档

https://fscrawler.readthedocs.io/en/latest/user/ocr.html

可以通过设置不同的参数实现是否调用API服务

其中OCR PDF Strategy中auto对应的ocr option部分对应链接

（https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=109454066）

这里面提到了两种ocr实现方法，一种是提取pdf中的图片对图片进行ocr，另一种是将每一页pdf整体进行ocr扫描，两个均是调用api实现

他用的应该是他们自己搞的一个TiKaOCRhttps://cwiki.apache.org/confluence/display/tika/TikaOCR

前面说的方案测评那篇文章里面说他的ocr效果不是很好，但是可以试试看看效果怎么样，虽然是调用API实现的，但是不交代码如果效果好感觉试试这个也可以

##### fscrawler实现pdf传输教程

https://huaweicloud.csdn.net/637ef038df016f70ae4ca371.html?spm=1001.2101.3001.6650.11&utm_medium=distribute.pc_relevant.none-task-blog-2~default~BlogCommendFromBaidu~activity-11-115467774-blog-121515258.pc_relevant_3mothn_strategy_and_data_recovery&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2~default~BlogCommendFromBaidu~activity-11-115467774-blog-121515258.pc_relevant_3mothn_strategy_and_data_recovery&utm_relevant_index=14

这里面有在Kibana中搜索效果展示，整个过程感觉说的很清晰

#### 3、 Ambari服务

根据前面那篇测评文章所说，这个也可以调用API实现ocr，而且这个软件还是开源的

仓库链接：https://github.com/apache/ambari

里面应该有ocr API调用对应的代码实现，看看能不能把他的ocr功能换成我们的

## 参考博客

ElasticSearch 全文检索实战，可以帮助理解整个项目过程(https://blog.csdn.net/ejinxian/article/details/105596783)

ElasticSearch、FSCrawler及 SearchUI搭建文件搜索引擎实战(https://zhuhuix.blog.csdn.net/article/details/121515258?spm=1001.2014.3001.5506)

ElasticSearch的站内全文搜索实现（https://binhao.blog.csdn.net/article/details/114456257?spm=1001.2014.3001.5506)

es分词器（https://zhuanlan.zhihu.com/p/111775508) es可以使用一些中文分词器，目前了解到PubMed中的医学分词似乎不能用在这里面，还需进一步了解。而且参考最前面用es实现的那个文献搜索引擎项目，感觉使用es提供的分词器搜索效果也挺好的

## 存在的问题

对于fscrawle和 Ambari服务的ocr功能还没有实现不知道具体效果怎么样，感觉可以考虑实现一下

ambari开源项目代码还不了解，对应其ocr API的代码实现部分还不清楚在哪

document-sharing-site开源文献搜索引擎项目代码可以更深一步了解，要实现高级搜索和ocr功能还需要自己改代码

es对于文档的索引方式和其中图片的索引方式还不是很清楚

目前对于Java不太了解，Java API调用目前也不是很了解





