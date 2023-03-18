1. ##### 运行代码需要安装PyPDF2和pytesseract库

   直接在环境里面pip就行了

   安装好了就可以运行script.py代码

   目录中pdf文件用于测试，其中no.pdf表示非扫描件，yes.pdf表示扫描件

   

2. ##### 实现思路：

首先使用PyPDF2库来提取PDF中的文字层。如果PyPDF2无法提取文字，则使用OCR技术来提取文字。如果提取到的文字为空，则说明PDF是一个扫描件，否则说明PDF不是扫描件。程序返回True表示PDF是扫描件，返回False表示PDF不是扫描件