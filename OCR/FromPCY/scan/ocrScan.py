# -*- coding: utf-8 -*-
"""
Created on Tue Mar 14 22:26:46 2023

@author: 18444
"""
import os
import tempfile

import ocrmypdf as omp



def getScaner(filePath, filename):
    temp_dir = tempfile.gettempdir()
    pathToSave = temp_dir + os.sep
    print("文件名为：", filename)
    fileToSave = pathToSave + filename
    omp.ocr(filePath, fileToSave,
            deskew=True,
            language=['chi_sim', 'eng'],
            image_dpi=300)

    return fileToSave

# 创建临时文件
# with tempfile.NamedTemporaryFile(delete=False) as temp_file:
#     temp_file.write(file.read())
#     temp_file_path = temp_file.name
#
# path_to_save = "C:/Users/22533/Desktop/notingDQX/pdf/" + filename
# # 进行识别
# result = omp.ocr(temp_file_path, path_to_save, deskew=True,
#                  language=['chi_sim', 'eng'], image_dpi=300)
# # 删除临时文件
# os.remove(temp_file_path)
#
# return result

# 两个路径分别是输入和输出的
# omp.ocr(r'C:\Users\22533\AppData\Local\Temp\output3.pdf', r'C:\Users\22533\Desktop\notingDQX\pdf\1234545555.pdf',
#         deskew=True,
#         language=['chi_sim', 'eng'],
#         image_dpi=300)
