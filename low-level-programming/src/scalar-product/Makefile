CC := gcc
CFLAGS := -ansi -pedantic-errors
ROOT := ../..

CWARNINGS := all error
CFLAGS += $(addprefix -W,$(CWARNINGS))

SOURCES := $(wildcard *.c)
OBJECTS := $(patsubst %.c,%,$(SOURCES))

all: $(OBJECTS)

%: %.c
	$(CC) $(CFLAGS) $^ -o $@

.PHONY: clean install
install: main conscious_main
	mkdir -p $(ROOT)/bin/lab3
	cp $^  $(ROOT)/bin/lab3

clean:
	$(RM) $(OBJECTS)
