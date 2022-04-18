include util.make

NAME := dict

SOURCES  := $(wildcard src/*.asm)
OBJECTS  := $(SOURCES:src/%.asm=build/%.o)
DEPFILES := $(SOURCES:src/%.asm=build/%.d)

ASM := nasm
LD := ld
RM := rm -rf

ASMFLAGS := -g -felf64 -Iinclude/ -Wno-macro-params

all: $(NAME)

$(NAME): $(OBJECTS)
	$(LD) $^ -o $@

build/%.o: src/%.asm Makefile | build
	$(ASM) $(ASMFLAGS) -DBUILDING_$(call to_uppercase,$*)=1 -MP -MD build/$*.d $< -o $@

build:
	mkdir -p $@

.PHONY: clean gdb

clean:
	$(RM) build $(NAME)

gdb: $(NAME)
	gdb $(NAME) -iex "layout regs"

-include $(DEPFILES)
