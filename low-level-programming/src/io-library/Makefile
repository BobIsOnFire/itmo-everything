DEBUG		?= YES
DEBUG_FLAGS.YES := -g
ROOT		:= ../..

ASM := /usr/bin/nasm
LD := /usr/bin/ld

SOURCES := $(wildcard *.asm)

all: $(patsubst %.asm,%,$(SOURCES))

%.o: %.asm
	$(ASM) $(DEBUG_FLAGS.$(DEBUG)) -felf64 $< -o $@

%: %.o
	$(LD) $< -o $@

factorial.o: lib.inc

.PHONY: install clean
install: factorial
	mkdir -p $(ROOT)/bin
	cp $^ $(ROOT)/bin/lab1

clean:
	$(RM) $(patsubst %.asm,%.o,$(SOURCES))
	$(RM) $(patsubst %.asm,%,$(SOURCES))
