# PubMed

> [PubMed](https://pubmed.ncbi.nlm.nih.gov/)是一个免费的[MEDLINE](https://www.nlm.nih.gov/medline/index.html)数据库，提供生物医学和健康科学领域的文献检索服务，其检索设计对我们的系统设计启发很大

### PubMed提供了三种基本的检索方法：

+ 1.直接检索： 在检索界面，直接输入想要检索的内容即可。比如"mutation"。
![\pictures\img.png](pictures\img.png)
    > 这样直接检索的方式，默认的就是在所有的区域(all fields)进行检索。

+ 2.高级检索：点击主页的Advanced就可以进行高级检索。
![\pictures\img_1.png](pictures\img_1.png)
    > 在高级就检索当中可以选择在哪个类型当中进行检索。比如在题目当中检索mutation。就可以选择好标题区域之后进行输入“mutation”。

+ 3.标签检索：标签检索和高级检索是类似的。
    > 高级检索通过鼠标来选择想要检索的类型。而标签则以在检索内容后面输入标签的方式来规定检索范围。pubmed当中支持的标签如下图所示。
 ![\pictures\img_2.png](pictures\img_2.png)

 
### PubMed特有的检索机制：ATM搜索系统

> 熟悉的搜索引擎基本上是根据输入的内容返回和输入内容完全匹配的信息，而PubMed则不是这样的。PubMed的检索是基于自动术语映射(Automatic Term Mapping, ATM)系统来进行检索的。
在ATM系统中，输入的内容不会直接进行检索。而是先在一个包含[Mesh主题词查询数据库](https://www.ncbi.nlm.nih.gov/mesh)，将杂志名以及作者信息的“词典”当中进行匹配。寻找和关键词类似的术语。然后利用匹配到术语进行检索。
例如"mutation (突变)"这个关键词。在输入到PubMed之后，利用ATM系统，会找到很多和mutaion有关的词语。最终检索的其实是下面的内容：
![\pictures\img_3.png](pictures\img_3.png)

### 基本检索要素

+ 输入关键词检索：例如想要检索"mutation"。就直接输入即可。
 ![\pictures\img_4.png](pictures\img_4.png)
+ 布尔符连接：如果有多个关键词，且关键词之间有一些逻辑关系。那么就可以通过布尔值(AND, OR, NOT)来进行连接。例如：想要检索突变和肿瘤。那就可以输入: "mutation AND cancer"
 ![\pictures\img_5.png](pictures\img_5.png)
+ 大小写忽略：在英文检索当中涉及到字母大小写的情况。PubMed会忽略大小写来进行检索。例如: "mutation", "Mutation"以及"MUTATION“检索的结果是一样的。
 ![\pictures\img_6.png](pictures\img_6.png)
+ 符号转换：除了大小写转换，一些没意义的符号例如，逗号，等于号等等。在输入之后也会被强制性的转换为空格。例如"mutation = snp"和"mutation snp“检索的结果是一样的。
 ![\pictures\img_7.png](pictures\img_7.png)
+ 除了上面常见的输入方式。PubMed也可以进行词组检索。可以避免ATM搜索系统过多匹配。
    > 词组检索
   >![\pictures\img_8.png](pictures\img_8.png)
    > > 1.检索词加双引号;
  >
    > > 2.检索词加标签；
  > 
    > > 3.使用“—”链接检索词(这个只适用于多个关键词)
    
### 多词检索

上面说的只是一个单词检索的情况，实际过程中还会有输入多词语检索的情况。

+ 词语在字典中找到对应术语
    > 如果【不使用词组检索】，会拿对应词语术语在特定类型当中进行检索。同时也会把词语拆成单个单词进行检索。例如: "INDEL Mutation"
    ![\pictures\img_9.png](pictures\img_9.png)

    > 如果【使用词组检索】的话，则只会在特定的范围内进行检索。
    ![\pictures\img_10.png](pictures\img_10.png)

+ 词语在字典中找不到对应术语

    > 如果【不使用词组检索】，则会把输入的内容拆成每个单词，先在“词典”中进行匹配然后AND合并。例如: "DNA PDCD1"和"DNA AND PDCD1"的结果是一样的。
 
    > 如果【使用""进行词组检索】，由于词语找不到对应术语，所以还是会拆开进行ATM系统检索，最后AND在一起。但是会提示原来的检索没有结果。
 
    > 如果【使用-进行词组检索】，在找不到对应术语的情况下就返回0个检索结果。


### PubMed的高级检索

> PubMed的高级检索界面主要包括了两个部分：检索栏和历史记录

##### 检索栏当中，可以进行作者检索、杂志检索、日期检索、标题/摘要检索等

    > 进一步详情了解可以参考[Help-PubMed](https://pubmed.ncbi.nlm.nih.gov/help/#advanced-search)

##### 历史记录
+ 高级检索中可以看到之前的检索的历史记录。在历史记录当中可以看到每一个检索的检索式以及有多少相关的文献。 
+ 每一个之前检索的结果，PubMed都会进行编号。如果想要对之前检索结果进行逻辑合并检索的话，在可以直接使用前面的编号即可。例如我们想看第四个检索结果(#4，杂志J HAND SURG-AM的检索结果) 以及第九个检索结果(#9，突变在标题和摘要当中的检索结果) 两者之间的重叠的文献。就可以直接输入 #9 AND #4 即可。 
+ 历史记录的Action包括了三个选项：添加到检索栏、删除检索记录以及创建提醒。 
  + 添加到检索栏：类似于把编号放到检索栏当中一样。只不过通过这个动作更加形象的知道检索的是什么内容。 
  + 创建提醒：可以对检索内容创建一个邮件提醒。如果检索内容有新的文献发表了，就可以发邮件提醒。

