NAME := lab6
ROOT := ../..

SOURCES  := $(wildcard *.c)
OBJECTS  := $(addsuffix .o,$(SOURCES))

CC := gcc
CFLAGS := -g -pedantic-errors
LIBS := -lm

CWARNINGS := all error
CFLAGS += $(addprefix -W,$(CWARNINGS))

all: $(NAME)

$(NAME): $(OBJECTS)
	$(CC) $^ -o $@ $(LIBS)

%.c.o: %.c Makefile image_utils.h
	$(CC) $(CFLAGS) $< -c -o $@

.PHONY: install clean
install: $(NAME)
	mkdir -p $(ROOT)/bin
	cp $^ $(ROOT)/bin

clean:
	$(RM) $(OBJECTS) $(NAME)
