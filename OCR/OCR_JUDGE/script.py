import PyPDF2
import pytesseract
from PIL import Image

def is_scanned_pdf(pdf_file_path):
    pdf_reader = PyPDF2.PdfReader(open(pdf_file_path, 'rb'))
    num_pages = len(pdf_reader.pages)
    for page_num in range(num_pages):
        page = pdf_reader.pages[page_num]
        try:
            text = page.extract_text()
        except:
            # 如果使用PyPDF2库无法提取文字，则使用OCR技术来提取
            img = Image.frombytes('RGB', page.mediabox.getSize(), page.getContentStream().read())
            text = pytesseract.image_to_string(img)

        # 判断是否为扫描件
        if not text:
            return True

    return False

# 测试程序
pdf_file_path = 'yes2.pdf'
if is_scanned_pdf(pdf_file_path):
    print('This is a scanned PDF.')
else:
    print('This is not a scanned PDF.')
