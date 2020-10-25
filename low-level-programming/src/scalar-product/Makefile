CC := gcc
CFLAGS := -ansi -pedantic-errors

CWARNINGS := all error
CFLAGS += $(addprefix -W,$(CWARNINGS))

SOURCES := $(wildcard *.c)
OBJECTS := $(patsubst %.c,%,$(SOURCES))

all: $(OBJECTS)

%: %.c
	$(CC) $(CFLAGS) $^ -o $@

.PHONY: clean
clean:
	$(RM) $(OBJECTS)
