NAME := lab2
ROOT := ../..

SOURCES := $(wildcard *.asm)
OBJECTS := $(patsubst %.asm,%.o,$(SOURCES))

ASM := nasm
LD := ld

ASMFLAGS := -g -felf64

all: $(NAME)

$(NAME): $(OBJECTS)
	$(LD) $^ -o $@

%.o: %.asm Makefile
	$(ASM) $(ASMFLAGS) $< -o $@

main.o: words.inc colon.inc

.PHONY: install clean
install: $(NAME)
	mkdir -p $(ROOT)/bin
	cp $^ $(ROOT)/bin

clean:
	$(RM) $(OBJECTS) $(NAME)
