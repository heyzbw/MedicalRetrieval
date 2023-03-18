# -*- coding: utf-8 -*-
"""
Created on Sun Mar 12 09:53:31 2023

@author: 18444
"""

from pdf2docx import Converter
pdf_file = './pdf/数字经济对制造业高质量发展...度分析——以长三角地区为例_杨金海.pdf'
docx_file = './pdf/test.docx'
cv = Converter(pdf_file)
cv.convert(docx_file, start=0, end=None)
cv.close()
