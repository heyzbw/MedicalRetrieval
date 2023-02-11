# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 20:04:35 2023

@author: 18444
"""

import fitz
import os
import shutil
import easyocr
import json
from PIL import Image


def pdf2pic(path, pic_path, filename):
    '''
    # 从pdf中提取图片
    :param path: pdf的路径
    :param pic_path: 图片保存的路径
    :return:
    '''
    # 打开pdf
    doc = fitz.open(path)
    #遍历page
    for p in range(len(doc)):
        page = doc.load_page(p)
        pageimg = page.get_images()  #得到确定为图片的Object列表
        num = len(pageimg)
        for i in range(num):
            pix = fitz.Pixmap(doc, pageimg[i][0])   #遍历object，每个object信息也是单独列表，其中第一个元素是xref
            img_name = filename.strip('.pdf')+"-{}-{}.jpg".format(p+1, i+1)
            if(pix.colorspace != None):     #调整图片的色彩空间
                if not pix.colorspace.name in (fitz.csGRAY.name, fitz.csRGB.name):
                    pix = fitz.Pixmap(fitz.csRGB, pix)
            
            # 如果pix.n<5,可以直接存
            if pix.n < 5:
                pix.save(os.path.join(pic_path, img_name))
                pix = None
            else:
                pix0 = fitz.Pixmap(fitz.csRGB, pix)
                pix0.save(os.path.join(pic_path, img_name))
                pix0 = None
            fp = open(os.path.join(pic_path, img_name),'rb')
            fp.close()
    # for i in range(1, nums):
    #     text = doc.xref_object(i)
    #     # print(i, text)
    #     # 过滤无用图片
    #     # if ('Width 2550' in text) and ('Height 3300' in text) or ('thumbnail' in text):
    #     #     continue
    	    
    #     isXObject = re.search(checkXO, text)
    #     isImage = re.search(checkIM, text)
        
    #     # 不符合条件, continue
    #     if not isXObject or not isImage:
    #         continue
    #     imgcount += 1
    
    #     # 生成图像
    #     pix = fitz.Pixmap(doc, i)
    		
    # 		# 保存图像名
    #     img_name = filename.strip('.pdf')+"-第{}张.jpg".format(imgcount)
        
    #     if(pix.colorspace != None):
    #         if not pix.colorspace.name in (fitz.csGRAY.name, fitz.csRGB.name):
    #             pix = fitz.Pixmap(fitz.csRGB, pix)
        
    #     # 如果pix.n<5,可以直接存为PNG
    #     if pix.n < 5:
    #         pix.save(os.path.join(pic_path, img_name))
    #         pix = None
    #     else:
    #         pix0 = fitz.Pixmap(fitz.csRGB, pix)
    #         pix0.save(os.path.join(pic_path, img_name))
    #         pix0 = None
    
def pic2json(pic, path, pdfname, result_path):
    reader = easyocr.Reader(['ch_sim','en'])
    picpath = os.path.join(path, pic)
    
    #result下新建图片同名文件夹，并将图片移至其下
    result_dir = os.path.join(result_path, pic.strip('.jpg'))
    if not os.path.exists(result_dir):
        os.mkdir(result_dir)
    shutil.move(picpath, result_dir)
    
    newpath = os.path.join(result_dir,pic)
    
    result = reader.readtext(newpath)
    text = {}
    text["ocrText"] = ""
    text["pdfURL"] = pdfname
    text["textResult"] = []

    for se in result:
        sem = list(se)
        textarr = {}
        text["ocrText"] += sem[1]
        textarr["charNum"] = len(sem[1])
        textarr["isHandwritten"] = "false"
        textarr["leftBottom"] = "{x},{y}".format(x=sem[0][3][0],y=sem[0][3][1])
        textarr["leftTop"] = "{x},{y}".format(x=sem[0][0][0],y=sem[0][0][1])
        textarr["rightBottom"] = "{x},{y}".format(x=sem[0][2][0],y=sem[0][2][1])
        textarr["rightTop"] = "{x},{y}".format(x=sem[0][1][0],y=sem[0][1][1])
        textarr["text"] = sem[1]
        text["textResult"].append(textarr)
        
    jtext = json.dumps(text,indent=4)
    
    #同一文件夹下保存同名json文件
    f = open(os.path.join(result_dir, pic).strip('.jpg')+'.json','w')
    f.write(jtext)
    f.close()

if __name__ == '__main__':
    # pdf路径
    path = 'pdf'
    pic_path = 'pic'
    result_path = 'result'
    shutil.rmtree('pic')  
    os.mkdir('pic') 
    filename = os.listdir(path)
    for file in filename:
        filepath = os.path.join(path, file)
        pdf2pic(filepath, pic_path, file)
        pics = os.listdir(pic_path)
        for pic in pics:
            pic2json(pic, pic_path, file, result_path)
            
            
            