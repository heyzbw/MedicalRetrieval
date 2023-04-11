import pymongo

# host_address = '121.36.201.185'
host_address = 'localhost'

class GetMongoDBUtil(object):
    def __init__(self):
        self.host = host_address
        self.port = 27017
        self.client = pymongo.MongoClient(host=self.host, port=self.port)
        self.db = self.client.sqrrow
        print("serverInfo:", self.client.server_info())

    def getDB(self):
        return self.db
