## 依赖库

- json
- random
- numpy
- torch
- transformers
- gc
- re
- time
- tqdm

## 模型使用

### 医学实体识别

调整的参数和模型在ner_constant.py中

**训练**

python3 train_ner.py


**使用示例**


medical_ner 类提供两个接口测试函数

- predict_sentence(sentence): 测试单个句子，返回:{"实体类别"：“实体”},不同实体以逗号隔开
- predict_file(input_file, output_file): 测试整个文件
文件格式每行待提取实体的句子和提取出的实体{"实体类别"：“实体”},不同实体以逗号隔开

> 运行`run_ner.py`


