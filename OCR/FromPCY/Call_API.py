import os
import requests
import json

def get_system_proxies():
    proxies = {}
    for name in ['http', 'https', 'ftp', 'no']:
        if name + '_proxy' in os.environ:
            proxies[name] = os.environ[name + '_proxy']
    return proxies if proxies else None

def get_file_content(filePath):
    with open(filePath, 'rb') as fp:
        return fp.read()

class CommonOcr(object):
    def __init__(self):
        # 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-app-id
        # 示例代码中 x-ti-app-id 非真实数据
        self._app_id = 'd391fdd0940627b3afb4975245e3e390'
        # 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-secret-code
        # 示例代码中 x-ti-secret-code 非真实数据
        self._secret_code = 'ab49069821971eacc812fba88ce3b9ae'

        self._proxies = get_system_proxies()

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
            # result = requests.post(url, data=image, headers=head, proxies=self._proxies)
            return result.text
        except Exception as e:
            return e

    def getRecognize(self, image):
        response = self.recognize(image)
        print("response", response)
        return json.loads(response)['result']

if __name__ == "__main__":
    with open(r'C:\Users\22533\Desktop\notingDQX\tmp63nk7970', 'rb') as fp:
        image = fp.read()

    result = CommonOcr().getRecognize(image)
    print("result", result)

    lines = result['lines']
    for line in lines:
        print("text:", line["text"])
        print("position", line["position"])

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
