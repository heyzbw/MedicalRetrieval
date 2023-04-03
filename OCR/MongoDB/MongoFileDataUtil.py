import io

from fitz import fitz

from MongoDB.GetMongoDBUtil import GetMongoDBUtil
from gridfs import GridFS, GridFSBucket

getMongoDBUtil = GetMongoDBUtil()


class MongoFileDataUtil(object):
    def __init__(self):
        self.db = getMongoDBUtil.getDB()
        self.collections = self.db.fileDatas

    def getFileByMD5(self, md5):
        '''
        # 从mongodb中获取文件，并以fitz.document的方式返回
        :param md5: 文件的md5的值
        :return:
        '''

        print("md5",md5)
        myquery = {"md5": md5}  # 查询条件
        projection = {"_id": 1, "gridfsId": 1, "name": 1}
        search_results = self.collections.find_one(myquery, projection)
        if  search_results != None:
            gridfsId = search_results["gridfsId"]
            file_name = search_results["name"]
            bucket = GridFSBucket(self.db)
            file_mongoDB = bucket.open_download_stream_by_name(gridfsId)
            fileData = io.BytesIO(file_mongoDB.read())
            document = fitz.open(stream=fileData, filetype='pdf')
            document.name = file_name

            return document

        else:
            return None

    def getFileByGridID(self, gridfsId):
        bucket = GridFSBucket(self.db)
        file_mongoDB = bucket.open_download_stream_by_name(gridfsId)
        fileData = io.BytesIO(file_mongoDB.read())
        document = fitz.open(stream=fileData, filetype='pdf')
        # document.name = file_name
        return document



if __name__ == '__main__':
    mongoFileDataUtil = MongoFileDataUtil()
    mongoFileDataUtil.getFileByMD5("4f9f2465ced9f4b3bf9dda85a040abce")
