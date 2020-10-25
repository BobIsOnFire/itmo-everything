NAME := lab4

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

clean:
	$(RM) $(OBJECTS) $(NAME)
