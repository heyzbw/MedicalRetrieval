# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 20:04:35 2023

@author: 18444
"""
import base64
from io import BytesIO

import cv2
import fitz
import os
import shutil
import easyocr
import json

import numpy as np
from PIL import Image

from MongoDB.MongdbOcrUtil import MongdbOcrUtil
from MongoDB.MongoFileDataUtil import MongoFileDataUtil

mongdbOcrUtil = MongdbOcrUtil()


# def pdf2pic(doc, pic_path='pic'):
#     '''
#     # 从pdf中提取图片
#     :param path: pdf的路径
#     :param pic_path: 图片保存的路径
#     :return:
#     '''
#     # 打开pdf
#     # doc = fitz.open(path)
#     # 遍历page
#     filename = doc.name
#     for p in range(len(doc)):
#         page = doc.load_page(p)
#         pageimg = page.get_images()  # 得到确定为图片的Object列表
#         num = len(pageimg)
#         for i in range(num):
#             pix = fitz.Pixmap(doc, pageimg[i][0])  # 遍历object，每个object信息也是单独列表，其中第一个元素是xref
#             if pix.w * pix.h < 50000:  # 去掉尺寸太小的图片
#                 continue
#             img_name = filename.strip('.pdf') + "-{}.jpg".format(p + 1)
#             if (pix.colorspace != None):  # 调整图片的色彩空间
#                 if not pix.colorspace.name in (fitz.csGRAY.name, fitz.csRGB.name):
#                     pix = fitz.Pixmap(fitz.csRGB, pix)
#
#             if os.path.exists(os.path.join(pic_path, img_name)):
#                 continue
#
#             # 如果pix.n<5,可以直接存
#             if pix.n < 5:
#                 pix.save(os.path.join(pic_path, img_name))
#                 pix = None
#             else:
#                 pix0 = fitz.Pixmap(fitz.csRGB, pix)
#                 pix0.save(os.path.join(pic_path, img_name))
#                 pix0 = None
#             fp = open(os.path.join(pic_path, img_name), 'rb')
#             fp.close()

class PicInfo:
    def __init__(self, image_np, pageNum, filename):
        self.image_np = image_np
        self.pageNum = pageNum
        self.filename = filename

def pdf2pic(doc, pic_path='pic'):
    '''
    # 从pdf中提取图片
    :param path: pdf的路径
    :param pic_path: 图片保存的路径
    :return:
    '''
    filename = doc.name
    images = []
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
            pix0 = None

    return images


# def pic2json(pic, path, pdfname, result_path):
#     reader = easyocr.Reader(['ch_sim', 'en'])
#     picpath = os.path.join(path, pic)
#
#     with open('pic/' + pic, "rb") as image_file:
#         image_data = image_file.read()
#     image_base64 = base64.b64encode(image_data).decode("utf-8")
#
#     # result下新建图片同名文件夹，并将图片移至其下
#     result_dir = os.path.join(result_path, pic.strip('.jpg'))
#
#     if not os.path.exists(result_dir):
#         os.mkdir(result_dir)
#
#     newpath = os.path.join(result_dir, pic)
#
#     if not os.path.exists(newpath):
#         shutil.move(picpath, result_dir)
#
#     result = reader.readtext(newpath)
#     text = {}
#     text["ocrText"] = ""
#     text["pdfURL"] = pdfname
#     text["pdfPage"] = pic.strip('.jpg').split('-')[-1]
#     text["textResult"] = []
#     text["image"] = image_base64
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
#         mongdbOcrUtil.write_result(text)
#
#     # 同一文件夹下保存同名json文件
#     # jtext = json.dumps(text)
#     # f = open(os.path.join(result_dir, pic).strip('.jpg') + '.json', 'w', encoding='utf8')
#     # f.write(jtext)
#     # f.close()

def pic2json(image_np, pageNum, pdfname):
    # print("type:", type(image_base64))
    # image_base64 = image_base64.encode('utf-8')
    # print("type:", type(image_base64))
    reader = easyocr.Reader(['ch_sim', 'en'])
    # image_io = Image.open(BytesIO(base64.b64decode(image_base64)))
    result = reader.readtext(image_np)
    print("len:", len(result))
    text = {}
    text["ocrText"] = ""
    text["pdfURL"] = pdfname
    text["pdfPage"] = pageNum
    # text["pdfPage"] = pic.strip('.jpg').split('-')[-1]
    text["textResult"] = []
    # text["image"] = image_base64

    for se in result:
        sem = list(se)
        textarr = {}
        text["ocrText"] += sem[1]
        textarr["charNum"] = len(sem[1])
        textarr["isHandwritten"] = "false"
        textarr["leftBottom"] = "{x},{y}".format(x=sem[0][3][0], y=sem[0][3][1])
        textarr["leftTop"] = "{x},{y}".format(x=sem[0][0][0], y=sem[0][0][1])
        textarr["rightBottom"] = "{x},{y}".format(x=sem[0][2][0], y=sem[0][2][1])
        textarr["rightTop"] = "{x},{y}".format(x=sem[0][1][0], y=sem[0][1][1])
        textarr["text"] = sem[1]
        text["textResult"].append(textarr)
    if len(text["ocrText"]) > 0:
        mongdbOcrUtil.write_result(text)


# def fromMD5(md5):
#     pic_path = 'pic'
#     result_path = 'result'
#     # if os.path.exists(pic_path):
#     #     shutil.rmtree(pic_path)
#     # os.mkdir(pic_path)
#
#     mongoFIleDataUtil = MongoFileDataUtil()
#     doc = mongoFIleDataUtil.getFileByMD5(md5)
#
#     images = pdf2pic(doc)
#     print(images)
#     for image in images:
#         # print(type(image.base64))
#         pic2json(image.base64, image.pageNum, image.filename)
#     # pics = os.listdir(pic_path)
#     # for pic in pics:
#     #     pic2json(pic, pic_path, doc.name, result_path)
#     #
#     return 1


def fromMD5(md5):
    mongoFIleDataUtil = MongoFileDataUtil()
    doc = mongoFIleDataUtil.getFileByMD5(md5)
    images = pdf2pic(doc)
    for image in images:
        pic2json(image.image_np, image.pageNum, image.filename)

    return 1


if __name__ == '__main__':
    md5 = "31c0020f1fe46009a1ab42559d42e685"
    fromMD5(md5)
