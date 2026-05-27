from zipfile import ZipFile, ZIP_DEFLATED
from io import BytesIO

bio = BytesIO()
with ZipFile(bio, 'w', ZIP_DEFLATED) as z:
    z.writestr('[Content_Types].xml', '''<?xml version="1.0" encoding="UTF-8"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
    <Default Extension="xml" ContentType="application/xml"/>
    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>''')
    z.writestr('_rels/.rels', '''<?xml version="1.0" encoding="UTF-8"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>''')
    z.writestr('word/document.xml', '''<?xml version="1.0" encoding="UTF-8"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body><w:p><w:r><w:t>Test Resume Document</w:t></w:r></w:p></w:body></w:document>''')

docx_bytes = bio.getvalue()

pdf_text = 'Test Resume PDF Document'
pdf_body = f"BT /F1 24 Tf 72 720 Td ({pdf_text}) Tj ET"
pdf = []
pdf.append(b'%PDF-1.4\n')
pdf.append(b'1 0 obj<< /Type /Catalog /Pages 2 0 R>>\nendobj\n')
pdf.append(b'2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1>>\nendobj\n')
pdf.append(b'3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n')
pdf.append(b'4 0 obj<< /Length %d >>\nstream\n' % len(pdf_body.encode('utf-8')))
pdf.append(pdf_body.encode('utf-8'))
pdf.append(b'\nendstream\nendobj\n')
pdf.append(b'5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n')

# build xref table
xref_offset = sum(len(part) for part in pdf)
pdf.append(b'xref\n0 6\n0000000000 65535 f \n')
current = 0
for part in pdf[:-1]:
    current += len(part)
    pdf.append(f'{current:010d} 00000 n \n'.encode('ascii'))

pdf.append(b'trailer<< /Size 6 /Root 1 0 R>>\nstartxref\n')
pdf.append(str(xref_offset).encode('ascii') + b'\n%%EOF')

pdf_bytes = b''.join(pdf)

with open('src/test/resources/sample_resume.docx', 'wb') as f:
    f.write(docx_bytes)
with open('src/test/resources/sample_resume.pdf', 'wb') as f:
    f.write(pdf_bytes)
print('Wrote sample_resume.docx and sample_resume.pdf')
