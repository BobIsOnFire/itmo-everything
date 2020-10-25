NAME := lab5
ROOT := ../..

SOURCES := $(wildcard *.c)
OBJECTS := $(addsuffix .o,$(SOURCES))

CC := gcc
CFLAGS := -g -pedantic-errors -ansi

CWARNINGS := all error
CFLAGS += $(addprefix -W,$(CWARNINGS))

$(NAME): $(OBJECTS)
	$(CC) $^ -o $@

%.c.o: %.c Makefile
	$(CC) $(CFLAGS) $< -c -o $@

linked_list.c: linked_list.h
main.c: linked_list.h
bi_linked_list.c: bi_linked_list.h

TMP_FILES := list.txt list.bin

.PHONY: install clean
install: $(NAME)
	mkdir -p $(ROOT)/bin
	cp $^ $(ROOT)/bin

clean:
	$(RM) $(OBJECTS) $(NAME) $(TMP_FILES)
