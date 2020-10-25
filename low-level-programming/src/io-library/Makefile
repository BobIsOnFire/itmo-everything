DEBUG		?= YES
DEBUG_FLAGS.YES := -g

ASM := /usr/bin/nasm
LD := /usr/bin/ld

SOURCES := $(wildcard *.asm)

all: $(patsubst %.asm,%,$(SOURCES))

%.o: %.asm
	$(ASM) $(DEBUG_FLAGS.$(DEBUG)) -felf64 $< -o $@

%: %.o
	$(LD) $< -o $@

factorial.o: lib.inc

.PHONY: clean
clean:
	$(RM) $(patsubst %.asm,%.o,$(SOURCES))
	$(RM) $(patsubst %.asm,%,$(SOURCES))
