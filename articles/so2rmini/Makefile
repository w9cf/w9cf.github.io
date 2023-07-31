FILE=auxiliary_output

SRC=\
   $(FILE).tex

$(FILE).pdf: $(SRC)
	pdflatex $(FILE)
	pdflatex $(FILE)

clean:
	rm -f *.nav *.toc *.out *.snm *.aux *.log

distclean:
	rm -f *.nav *.toc *.out *.snm *.aux *.log $(FILE).pdf
