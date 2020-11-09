#ifndef _MEM_H
#define _MEM_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <sys/mman.h>

#define HEAP_START ((void*)0x04040000)
#define BLOCK_MIN_SIZE 32
#define HEAP_PAGE_SIZE (4 * 1024)

#pragma pack(push, 1)
struct mem {
    struct mem* next;
    uint64_t capacity;
    bool is_free;
};
#pragma pack(pop)

void *heap_init(uint64_t initial_size);
void *mem_alloc(uint64_t query);
void mem_free(void *mem);

#define DEBUG_FIRST_BYTES 4

void memalloc_debug_struct_info(FILE *f, struct mem const * const address);
void memalloc_debug_heap(FILE *f);

#endif