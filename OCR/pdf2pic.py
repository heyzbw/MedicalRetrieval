# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 20:04:35 2023

@author: 18444
"""
import base64
import cv2
import fitz
import easyocr
import numpy as np
import io
from PIL import Image

from MongoDB.MongdbOcrUtil import MongdbOcrUtil
from MongoDB.MongoFileDataUtil import MongoFileDataUtil
from testForAPI import CommonOcr

mongdbOcrUtil = MongdbOcrUtil()
commonOcr = CommonOcr()

class PicInfo:
    def __init__(self, image_np, pageNum, filename):
        self.image_np = image_np
        self.pageNum = pageNum
        self.filename = filename


def pdf2pic(doc):
    """
    # 从pdf中提取图片
    :param doc: pdf的文档文件
    :return:提取出来的图像的numpy数组list
    """
    filename = doc.name
    images = []
    page_images = []
    for p in range(len(doc)):
        page = doc.load_page(p)
        pageimg = page.get_images()  # 得到确定为图片的Object列表
        num = len(pageimg)
        for i in range(num):
            pix = fitz.Pixmap(doc, pageimg[i][0])  # 遍历object，每个object信息也是单独列表，其中第一个元素是xref
            if pix.w * pix.h < 50000:  # 去掉尺寸太小的图片
                continue
            if (pix.colorspace != None):  # 调整图片的色彩空间
                if not pix.colorspace.name in (fitz.csGRAY.name, fitz.csRGB.name):
                    pix = fitz.Pixmap(fitz.csRGB, pix)

            # 如果pix.n<5,可以直接存
            if pix.n < 5:
                img_bytes = pix.tobytes()
                img_base64 = base64.b64encode(img_bytes).decode('utf-8')

            else:
                pix0 = fitz.Pixmap(fitz.csRGB, pix)
                img_bytes = pix0.tobytes()
                img_base64 = base64.b64encode(img_bytes).decode('utf-8')

            img_array = np.frombuffer(base64.b64decode(img_base64), np.uint8)
            img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

            picInfo = PicInfo(img, p, filename)
            images.append(picInfo)
            page_images.append(pageimg[i])

    return images, page_images


# def pic2json(image_np, pageNum, pdfname, image_page):
#     print("image_np:", image_np)
#     img_base64 = numpy_to_base64(image_np)
#     reader = easyocr.Reader(['ch_sim', 'en'])
#
#     result = reader.readtext(image_np)
#     text = {}
#     text["ocrText"] = ""
#     text["pdfURL"] = pdfname
#     text["pdfPage"] = pageNum
#     text["textResult"] = []
#     # text["image"] = img_base64
#
#     for se in result:
#         sem = list(se)
#         textarr = {}
#         text["ocrText"] += sem[1]
#         textarr["charNum"] = len(sem[1])
#         textarr["isHandwritten"] = "false"
#         textarr["leftBottom"] = "{x},{y}".format(x=sem[0][3][0], y=sem[0][3][1])
#         textarr["leftTop"] = "{x},{y}".format(x=sem[0][0][0], y=sem[0][0][1])
#         textarr["rightBottom"] = "{x},{y}".format(x=sem[0][2][0], y=sem[0][2][1])
#         textarr["rightTop"] = "{x},{y}".format(x=sem[0][1][0], y=sem[0][1][1])
#         textarr["text"] = sem[1]
#         text["textResult"].append(textarr)
#     if len(text["ocrText"]) > 0:
#         img = Image.fromarray(image_np)
#         with io.BytesIO() as output:
#             img.save(output, format='JPEG')
#             image_data = output.getvalue()
#
#         gridfs_id = mongdbOcrUtil.upload_image_to_mongodb(image_data)
#         print("gridfs_id",gridfs_id)
#         text["image"] = gridfs_id
#         mongdbOcrUtil.write_result(text)
#
#         return text
#     else:
#         return False


def pic2json(image_np, pageNum, pdfname, image_page):
    # print("image_np:", image_np)
    img_base64 = numpy_to_base64(image_np)
    reader = easyocr.Reader(['ch_sim', 'en'])
    image_bytes = image_np.tobytes()
    result = commonOcr.getRecognize(image_bytes)
    print(result)

    # result = reader.readtext(image_np)
    # text = {}
    # text["ocrText"] = ""
    # text["pdfURL"] = pdfname
    # text["pdfPage"] = pageNum
    # text["textResult"] = []
    # # text["image"] = img_base64
    #
    # for se in result:
    #     sem = list(se)
    #     textarr = {}
    #     text["ocrText"] += sem[1]
    #     textarr["charNum"] = len(sem[1])
    #     textarr["isHandwritten"] = "false"
    #     textarr["leftBottom"] = "{x},{y}".format(x=sem[0][3][0], y=sem[0][3][1])
    #     textarr["leftTop"] = "{x},{y}".format(x=sem[0][0][0], y=sem[0][0][1])
    #     textarr["rightBottom"] = "{x},{y}".format(x=sem[0][2][0], y=sem[0][2][1])
    #     textarr["rightTop"] = "{x},{y}".format(x=sem[0][1][0], y=sem[0][1][1])
    #     textarr["text"] = sem[1]
    #     text["textResult"].append(textarr)
    # if len(text["ocrText"]) > 0:
    #     img = Image.fromarray(image_np)
    #     with io.BytesIO() as output:
    #         img.save(output, format='JPEG')
    #         image_data = output.getvalue()
    #
    #     gridfs_id = mongdbOcrUtil.upload_image_to_mongodb(image_data)
    #     print("gridfs_id",gridfs_id)
    #     text["image"] = gridfs_id
    #     mongdbOcrUtil.write_result(text)
    #
    #     return text
    # else:
    #     return False

# numpy 转 base64
def numpy_to_base64(image_np):
    data = cv2.imencode('.jpg', image_np)[1]
    image_bytes = data.tobytes()
    image_base4 = base64.b64encode(image_bytes).decode('utf8')
    return image_base4


def fromMD5(md5):
    mongoFIleDataUtil = MongoFileDataUtil()
    doc = mongoFIleDataUtil.getFileByMD5(md5)
    images, page_images = pdf2pic(doc)
    texts = []
    for i in range(0, len(images)):

        image = images[i]
        page_image = page_images[i]

        text = pic2json(image.image_np, image.pageNum, image.filename, page_image)
        if text != False:
            texts.append(text)

    # for image in images:
    #     text = pic2json(image.image_np, image.pageNum, image.filename)
    #     if text != False:
    #         texts.append(text)

    return texts


if __name__ == '__main__':
    md5 = "6da81f0a5750577291276861de36baac"
    fromMD5(md5)
