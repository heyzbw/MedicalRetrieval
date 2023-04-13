from flask import Flask, request, jsonify  # flask库
from flask_cors import CORS

from FromPCY.scan.ocrScan import getScaner
from pdf2pic import *
# from Bio import Entrez
# from Bio import Medline
import json
import os
import re
import urllib.request
import requests
from flask import Flask, send_from_directory, send_file, make_response
from Bio import Entrez
from Bio import Medline

# from FromPCY.scan.ocrScan import getScaner

# 创建一个服务，赋值给APP
app = Flask(__name__)
CORS(app, resource=r'/*')

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36',
}


@app.route('/pdf2pic', methods=['POST'])  # 指定接口访问的路径，支持什么请求方式get，post
def call_pdf2pic():
    print("进入了pdf2pic方法")
    # 通过MD5来传递文件的标识
    data = request.get_json()
    md5 = data.get("md5")
    # 读取文件，并进行处理
    texts = fromMD5(md5)

    json_data = [{'ocrText': text['ocrText'],
                  'recordId': str(text['_id'])}
                 for text in texts]

    # 返回成功
    json_obj = {"data": json_data}
    return json_obj


@app.route('/scanPDF', methods=['POST'])
def scaner():
    data = request.get_json()
    filename = data.get("filename")
    filePath = data.get("filePath")

    fileToSave = getScaner(filePath, filename)

    json_obj = {"data": fileToSave}

    # 进行文件处理

    # 返回成功

    return json_obj


@app.route('/getpubmed', methods=['POST'])
def call_getpubmed():
    Entrez.email = 'p1844483019@outlook.com'
    data = request.get_json(silent=True)
    print(request.get_json(silent=True))
    terms = data['keyword']
    handle = Entrez.esearch(db='pubmed', term=terms, sort='relevance')
    record = Entrez.read(handle)
    idlist = record['IdList']
    handle1 = Entrez.efetch(db='pubmed', id=idlist,
                            rettype='medline', retmode='text')
    record1 = Medline.parse(handle1)

    text = {}
    text['Papers'] = []

    for i in record1:
        textarr = {}
        textarr['Title'] = i.get('TI')
        textarr['Author'] = i.get('AU')
        textarr['Journal'] = i.get('JT')
        textarr['ISSN'] = i.get('IS')
        textarr['Source'] = i.get('SO')
        textarr['doi'] = re.findall(r"10\.[0-9]+/\S+", i.get('SO'))[0][:-1]
        textarr['Abstract'] = i.get('AB')
        text['Papers'].append(textarr)
    jtext = json.dumps(text, indent=4, ensure_ascii=False)
    return jtext


@app.route('/PDFdownload', methods=['POST'])
def pdfdownload():
    sci_Hub_Url = "https://sci-hub.ren/"
    data = request.get_json(silent=True)
    print(data)
    doi = data['doi']
    print(doi)
    Title = data['Title']
    url = sci_Hub_Url + doi
    pattern = '/.*?\.pdf'
    content = requests.get(url, headers=headers)
    download_url = re.findall(pattern, content.text)
    print(download_url)
    download_url[1] = "https:" + download_url[1]
    print(download_url[1])
    path = r"papers"
    # 使用 urllib.request 来包装请求
    req = urllib.request.Request(download_url[1], headers=headers)
    # 使用 urllib.request 模块中的 urlopen方法获取页面
    u = urllib.request.urlopen(req, timeout=5)

    file_name = Title + '.pdf'
    f = open(path + '/' + file_name, 'wb')

    block_sz = 8192
    while True:
        buffer = u.read(block_sz)
        if not buffer:
            break
        f.write(buffer)
    f.close()
    print(type(f))
    downloadpath = 'papers/' + file_name
    print("Sucessful to download" + " " + file_name)
    response = make_response(
        send_file(downloadpath, as_attachment=True))
    response.headers['Content-Type'] = 'arraybuffer'
    # print(type(send_file(downloadpath,
    #       mimetype='application/pdf;chartset=UTF-8', as_attachment=True)))
    return response


@app.route('/')  # 这个路由将根URL映射到了hello——world函数上
def hello_world():  # 定义视图函数
    return 'Hello World!'  # 返回响应对象


if __name__ == '__main__':
    # 这个host：windows就一个网卡，可以不写，而linux有多个网卡，写成0.0.0.0可以接受任意网卡信息
    # 端口号默认5000，可以手动设置，这里我设置成了8803
    app.run(host='0.0.0.0', port=8083, debug=False)
