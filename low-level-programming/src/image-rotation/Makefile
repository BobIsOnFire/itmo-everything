NAME := lab8
ROOT := ../..

SOURCES.C   := $(wildcard *.c)
OBJECTS.C   := $(addsuffix .o,$(SOURCES.C))

SOURCES.ASM := $(wildcard *.asm)
OBJECTS.ASM := $(addsuffix .o,$(SOURCES.ASM))

OBJECTS     := $(OBJECTS.C) $(OBJECTS.ASM)

CC        := gcc
CFLAGS    := -g -pedantic-errors -Iheaders
CLIBS     := -lm
CWARNINGS := all error
CFLAGS    += $(addprefix -W,$(CWARNINGS))

ASM       := nasm
ASMFLAGS  := -felf64 -g

all: $(NAME)

$(NAME): $(OBJECTS)
	$(CC) $^ -o $@ $(CLIBS)

%.c.o: %.c Makefile
	$(CC) $(CFLAGS) $< -c -o $@

%.asm.o: %.asm Makefile
	$(ASM) $(ASMFLAGS) $< -o $@

image_definitions.c.o: headers/image_definitions.h
bmp_utils.c.o:         headers/image_definitions.h headers/bmp_utils.h
image_transform.c.o:   headers/image_definitions.h headers/image_transform.h
main.c.o:              headers/image_definitions.h headers/bmp_utils.h headers/image_transform.h

.PHONY: install clean
install: $(NAME)
	mkdir -p $(ROOT)/bin
	cp $^ $(ROOT)/bin

clean:
	$(RM) $(OBJECTS) $(NAME)
