NAME := lab7
ROOT := ../..

SOURCES  := $(wildcard *.c)
OBJECTS  := $(addsuffix .o,$(SOURCES))

CC := gcc
CFLAGS := -g -pedantic-errors

CWARNINGS := all error
CFLAGS += $(addprefix -W,$(CWARNINGS))

all: $(NAME)

$(NAME): $(OBJECTS)
	$(CC) $^ -o $@ $(LIBS)

%.c.o: %.c Makefile mem.h
	$(CC) $(CFLAGS) $< -c -o $@

.PHONY: install clean
install: $(NAME)
	mkdir -p $(ROOT)/bin
	cp $^ $(ROOT)/bin

clean:
	$(RM) $(OBJECTS) $(NAME)
