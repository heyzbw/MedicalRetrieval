#### 解决编码解码问题

##### 1. 定义文本抽取管道

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

在`attachment`中指定content为要过滤的字段，写入es时需要将文档内容放在content字段中

##### 2. 建立文档结构映射

建立文档结构映射来定义，文本文件通过预处理器上传后以何种形式存储

PUT定义文档结构映射的时候就会自动创建索引，所以我们先创建一个`docwrite`的索引，用于测试。

在kibana中运行上一段之后再运行这一段

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

* 在 `ElasticSearch` 中增加了`attachment`字段，这个字段是`attachment`命名`pipeline `抽取文档附件中文本后自动附加的字段。这是一个嵌套字段，其包含多个子字段，包括抽取文本 content 和一些文档信息元数据。

* 同时对文件的名字name指定分析器`analyzer`为 `ik_max_word`，以让 `ElasticSearch `在建立全文索引时对它们进行中文分词。

  这样es中的文件就能够被解码存在es中了。

##### 然后再执行下面这一段

```json
POST /docwrite/_doc?pipeline=attachment
{
  "name":"人工智能实验安排",
  "type":"pdf",
"content":"JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURlY29kZT4+CnN0cmVhbQp4nK1YS4skNwy+96

}
```

##### 最后用下面这一段查一下可不可以找到解码后的这一段文字

```json
GET /docwrite/_search
```

之后再上传文件应该就可以自动解码了