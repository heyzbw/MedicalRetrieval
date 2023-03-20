# -*- coding: utf-8 -*-
"""
Created on Tue Mar 14 22:26:46 2023

@author: 18444
"""

import ocrmypdf as omp

        # 两个路径分别是输入和输出的
omp.ocr(r'C:\Users\22533\Desktop\notingDQX\pdf\amknv-jhvtg.pdf', r'C:\Users\22533\Desktop\notingDQX\pdf\1234545555.pdf',
        deskew=True,
        language=['chi_sim', 'eng'],
        image_dpi=300)

