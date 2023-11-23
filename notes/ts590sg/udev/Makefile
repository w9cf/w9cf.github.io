FILE=ts590sg_udev

SRC=\
   pulse_volume.png\
   $(FILE).tex

$(FILE).pdf: $(SRC)
	pdflatex $(FILE)
	pdflatex $(FILE)

clean:
	rm -f *.nav *.toc *.out *.snm *.aux *.log

distclean:
	rm -f *.nav *.toc *.out *.snm *.aux *.log $(FILE).pdf
