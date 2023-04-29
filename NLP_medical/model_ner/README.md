### 模型下载

由于依赖和训练好的的模型较大，将模型放到了百度网盘中，链接如下，按需下载。

NER: 链接：https://pan.baidu.com/s/16TPSMtHean3u9dJSXF9mTw  密码:shwh

下载解压到`medical_ner`中，最后`MODEL_NER`的目录结构如下所示：

```
MODEL_NER
|   bert_lstm_crf.py
|   crf.py
|   list.txt
|   __init__.py
|   
+---medical_ner
|       ._config.json
|       config.json
|       model.pkl
|       pytorch_model.bin
|       vocab.txt
|       
\---__pycache__
```