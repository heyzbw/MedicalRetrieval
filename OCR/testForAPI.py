import requests
import json

def get_file_content(filePath):
    with open(filePath, 'rb') as fp:
        return fp.read()

class CommonOcr(object):
    def __init__(self):
        # 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-app-id
        # 示例代码中 x-ti-app-id 非真实数据
        self._app_id = '35e7e083425816abfe4260c1ce2249d7'
        # 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-secret-code
        # 示例代码中 x-ti-secret-code 非真实数据
        self._secret_code = '892a9138e4029d7c8e1f5ee990edbb59'
        # self._img = img

    def recognize(self,image):
        # 通用文字识别
        url = 'https://api.textin.com/ai/service/v2/recognize'
        head = {}
        try:
            # image = get_file_content(self._img_path)
            # print("type:",type(image))
            head['x-ti-app-id'] = self._app_id
            head['x-ti-secret-code'] = self._secret_code
            result = requests.post(url, data=image, headers=head)
            return result.text
        except Exception as e:
            return e

    def getRecognize(self, image):
        response = self.recognize(image)
        print("response", response)
        return json.loads(response)['result']

if __name__ == "__main__":
    with open(r'C:\Users\22533\Desktop\testPaper\test.png', 'rb') as fp:
        image = fp.read()

    result = CommonOcr().getRecognize(image)
    print("result", result)
    # response = CommonOcr(r'C:\Users\22533\Desktop\testPaper\test.png')
    # print(response.recognize())
    # print(type(response.recognize()))
    # result_dict = json.loads(response.recognize())['result']
    # print(type(result_dict))
    # print(result_dict)

    # # 遍历字典
    # for key, value in result_dict.items():
    #     # 如果值是列表，则遍历列表中的每个字典
    #     if isinstance(value, list):
    #         for item in value:
    #             # 遍历每个字典中的键值对
    #             for item_key, item_value in item.items():
    #                 print(f"{item_key}: {item_value}")
    #     else:
    #         print(f"{key}: {value}")
