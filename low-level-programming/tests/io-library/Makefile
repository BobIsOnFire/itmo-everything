PYTHON := python2
CP     := cp
RM     := rm -f
ROOT   := ../..

all: test

.PHONY: test clean
test:
	$(CP) $(ROOT)/src/io-library/lib.inc .
	$(PYTHON) test.py

SOURCES := $(wildcard *.asm)
clean:
	$(RM) lib.inc $(SOURCES) $(patsubst %.asm,%.o,$(SOURCES)) $(patsubst %.asm,%,$(SOURCES))

