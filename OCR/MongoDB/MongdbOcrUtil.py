from uuid import uuid1

import gridfs
import pymongo
import json

host_address = "127.0.0.1"


class MongdbOcrUtil(object):
    def __init__(self):
        self.host = host_address
        self.port = 27017
        self.client = pymongo.MongoClient(host=self.host, port=self.port)
        self.db = self.client.sqrrow
        self.collections = self.db.ocr_result
        self.fs = gridfs.GridFS(self.db)

    def upload_image_to_mongodb(self, image):
        filename = "png_"+str(uuid1())
        self.fs.put(image, filename=filename, contentType="png")
        return filename


    def write_result(self, result_text):
        try:
            self.collections.insert_one(result_text)
            print('写入成功')
        except Exception as e:
            print(e)

    def search_datebase(self, query_str):
        try:
            # 待查询的文本条件
            # query_str = 'URA'
            # 查询条件与投影
            search_query = {"ocrText": {'$regex': '.*' + query_str + '.*'}}
            projection = {"_id": 0, "image": 0}
            # 进行查询并将结果放回到search_results
            # search_results = self.collections.find(search_query)
            search_results = self.collections.find(search_query, projection)

            text_result_new = []
            text_result_return = []
            # 可能包含很多个结果
            for search_result in search_results:
                # 对每一个结果进行处理，如果结果图片中的文字包括了查询条件，则保留，否则过滤
                for result in search_result['textResult']:
                    # 如果包含了文字，则保留
                    if query_str in result['text']:
                        text_result_new.append(result)
                search_result['textResult'] = text_result_new
                text_result_return.append(search_result)
            return text_result_return
        # catch块
        except Exception as e:
            print(e)

    # def getFileByMD5(self,md5):
        # try:



if __name__ == '__main__':
    jm = MongdbUtil()
    # jm.write_database()
    outcome = jm.search_datebase(query_str="URA")
    print("outcome", outcome)
