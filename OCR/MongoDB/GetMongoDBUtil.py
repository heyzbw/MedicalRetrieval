import pymongo

host_address = '127.0.0.1'


class GetMongoDBUtil(object):
    def __init__(self):
        self.host = host_address
        self.port = 27017
        self.client = pymongo.MongoClient(host=self.host, port=self.port)
        self.db = self.client.sqrrow

    def getDB(self):
        return self.db
