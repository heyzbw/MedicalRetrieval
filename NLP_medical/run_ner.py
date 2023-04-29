from medical_ner import  medical_ner
#使用工具运行
my_pred=medical_ner()
#根据提示输入单句：“高血压病人不可食用阿莫西林等药物”
#高血压病和尿毒症人不可食用阿莫西林、板蓝根、孟婆汤、牛黄解毒丸以及抗病毒口服液等美味佳肴，并通过手术刀和充电线投喂西红柿蛋汤之后切除脑瘤来治愈疾病
sentence=input("输入需要测试的句子:")
print(my_pred.predict_sentence("".join(sentence.split())))

#输入文件(测试文件，输出文件)
#my_pred.predict_file("my_test.txt","outt.txt")