NAME := lab3

SOURCES := $(wildcard *.c) $(wildcard *.asm)
OBJECTS := $(addsuffix .o,$(SOURCES))

ASM := nasm
LD := ld
ASMFLAGS := -g -felf64

CC := gcc
CFLAGS := -g -pedantic-errors

CWARNINGS := all error
CFLAGS += $(addprefix -W,$(CWARNINGS))

$(NAME): $(OBJECTS)
	$(CC) $^ -o $@

%.asm.o: %.asm Makefile
	$(ASM) $(ASMFLAGS) $< -o $@

%.c.o: %.c Makefile
	$(CC) $(CFLAGS) $< -c -o $@

ulong_ops.c: ulong_ops.h
main.c: ulong_ops.h

clean:
	$(RM) $(OBJECTS) $(NAME)
