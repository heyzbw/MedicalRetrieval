from flask import Flask, request, jsonify  # flask库
from flask_cors import CORS
from pdf2pic import *

# 创建一个服务，赋值给APP
app = Flask(__name__)
CORS(app, resource=r'/*')


@app.route('/pdf2pic', methods=['POST'])  # 指定接口访问的路径，支持什么请求方式get，post
def call_pdf2pic():
    # 通过MD5来传递文件的标识
    data = request.get_json()
    md5 = data.get("md5")
    # 读取文件，并进行处理
    fromMD5(md5)
    # 返回成功
    json_obj = {"response": "success"}
    return json_obj



if __name__ == '__main__':
    # 这个host：windows就一个网卡，可以不写，而linux有多个网卡，写成0.0.0.0可以接受任意网卡信息
    # 端口号默认5000，可以手动设置，这里我设置成了8803
    app.run(host='0.0.0.0', port=8083, debug=True)
