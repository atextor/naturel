#!/bin/bash
xalanpath=.
filebase=doku

# pdflatex muss 2 mal ausgefuehrt werden damit die referenzen aufgeloest werden!
java -cp $xalanpath/xalan.jar:$xalanpath/xml-apis.jar:$xalanpath/xercesImpl.jar org.apache.xalan.xslt.Process \
	-in $filebase.xml \
	-xsl pdf.xsl \
	-out $filebase.tex && pdflatex $filebase.tex && pdflatex $filebase.tex
