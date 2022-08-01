all: 
	cd javasrc; $(MAKE); $(MAKE) classes; $(MAKE) clean
	cd ..; tar -cvf YagiModel.tar yagipub; gzip YagiModel.tar
