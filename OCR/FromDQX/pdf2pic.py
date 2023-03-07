# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 20:04:35 2023

@author: 18444
"""
import base64
import io
import fitz
import easyocr
import json
import numpy as np
from PIL import Image

from MongoDB.MongdbOcrUtil import MongdbUtil
from MongoDB.MongoFileDataUtil import MongoFileDataUtil

mongo_util = MongdbUtil()


class ImageToken:
    def __init__(self, image_name, image_np):
        self.image_name = image_name
        self.image_np = image_np


def pdf2pic(doc):
    '''
    # 从pdf中提取图片
    :param path: pdf的路径
    :param pic_path: 图片保存的路径
    :return:
    '''
    # 打开pdf
    # doc = fitz.open(path)
    images = []
    file_name = doc.name
    # 遍历page
    for p in range(len(doc)):
        page = doc.load_page(p)
        pageimg = page.get_images()  # 得到确定为图片的Object列表
        num = len(pageimg)
        for i in range(num):
            pix = fitz.Pixmap(doc, pageimg[i][0])  # 遍历object，每个object信息也是单独列表，其中第一个元素是xref
            if pix.w * pix.h < 50000:  # 去掉尺寸太小的图片
                continue
            img_name = file_name.strip('.pdf') + "-{}.jpg".format(p + 1)
            if (pix.colorspace != None):  # 调整图片的色彩空间
                if not pix.colorspace.name in (fitz.csGRAY.name, fitz.csRGB.name):
                    pix = fitz.Pixmap(fitz.csRGB, pix)

            # 如果pix.n<5,可以直接存
            if pix.n < 5:
                img_np = np.frombuffer(pix.samples, dtype=np.uint8).reshape(pix.width, pix.height, pix.n)
                # img_data = pix.getImageData(output='raw')
                # img_np = np.frombuffer(img_data, dtype=np.uint8).reshape(pix.h, pix.w, 4)
                # pix.save(os.path.join(pic_path, img_name))
                # pix = None
            else:
                pix0 = fitz.Pixmap(fitz.csRGB, pix)
                img_np = np.frombuffer(pix0.samples, dtype=np.uint8).reshape(pix0.width, pix0.height, pix0.n)
                # img_data = pix0.getImageData(output='raw')
                # img_np = np.frombuffer(img_data, dtype=np.uint8).reshape(pix0.h, pix0.w, 4)

            # if img_np.shape[-1] == 1:
            #     img_np = np.concatenate([img_np] * 3, axis=-1)

            imageToken = ImageToken(file_name, img_np)
            images.append(imageToken)
            # fp = open(os.path.join(pic_path, img_name), 'rb')
            # fp.close()
    return images


def pic2json(image):
    reader = easyocr.Reader(['ch_sim', 'en'])
    result = reader.readtext(image.image_np)
    print("result", result)
    text = {}
    text["ocrText"] = ""
    text["pdfURL"] = image.image_name
    text["pdfPage"] = image.image_name.strip('.jpg').split('-')[-1]
    text["textResult"] = []
    image_file = Image.fromarray(image.image_np)

    img_buffer = io.BytesIO()
    image_file.save(img_buffer, format='JPEG')
    img_str = base64.b64encode(img_buffer.getvalue()).decode('utf-8')
    text["image"] = img_str

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
        jtext = json.dumps(text, indent=4, ensure_ascii=False)
        return jtext
    else:
        return ""


def pdf2char(md5):
    mongoFIleDataUtil = MongoFileDataUtil()
    doc = mongoFIleDataUtil.getFileByMD5(md5)

    pics = pdf2pic(doc)
    for pic in pics:
        jText = pic2json(pic)
        if jText != "":
            jText_dict = json.loads(jText)
            mongdbUtil.write_result(jText_dict)


if __name__ == '__main__':
    mongdbUtil = MongdbUtil()
    mongoFileDataUtil = MongoFileDataUtil()
    doc = mongoFileDataUtil.getFileByMD5("4f9f2465ced9f4b3bf9dda85a040abce")
    images = pdf2pic(doc)
    for image in images:
        jText = pic2json(image)

        if jText != "":
            print("找到一个")
            jText_dict = json.loads(jText)
            mongdbUtil.write_result(jText_dict)
    print(len(images))
