#include "mem.h"

static struct mem *heap_start = NULL;

static void *page_init(void *address, uint64_t size, bool fixed) {
    return mmap(
        address,
        size,
        PROT_READ | PROT_WRITE,
        MAP_PRIVATE | MAP_ANONYMOUS | (fixed ? MAP_FIXED_NOREPLACE : 0),
        -1,
        0
    );
}

static void block_split(struct mem *block, uint64_t query) {
    struct mem *new_block = (struct mem*) ((char*) block + sizeof(struct mem) + query);
    new_block->capacity = block->capacity - sizeof(struct mem) - query;
    new_block->is_free = true;
    new_block->next = block->next;

    block->capacity = query;
    block->next = new_block;
}

static void blocks_merge(struct mem *prev, struct mem *next) {
    prev->capacity += sizeof(struct mem) + next->capacity;
    prev->next = next->next;
}

static bool blocks_sequential(struct mem *prev, struct mem *next) {
    return (char*) next == (char*) prev + sizeof(struct mem) + prev->capacity;
}

static bool block_splittable(struct mem *block, uint64_t query) {
    return block->capacity >= query + sizeof(struct mem) + BLOCK_MIN_SIZE;
}

void *heap_init(uint64_t initial_size) {
    if (initial_size < HEAP_PAGE_SIZE) initial_size = HEAP_PAGE_SIZE;
    heap_start = page_init(HEAP_START, initial_size, false);
    heap_start->capacity = initial_size - sizeof(struct mem);
    heap_start->is_free = true;
    heap_start->next = NULL;
    printf("Initialized heap at %p, size %lu\n", (void*) heap_start, initial_size);
    return heap_start;
}

void *mem_alloc(uint64_t query) {
    struct mem *block, *prev, *retval;
    void *page_end;
    uint64_t page_size;

    if (!heap_start) heap_init(HEAP_PAGE_SIZE);
    if (query == 0) return NULL;
    if (query < BLOCK_MIN_SIZE) query = BLOCK_MIN_SIZE;
    
    for (block = heap_start; block; block = block->next) {
        prev = block;

        if (!block->is_free) continue;

        /* use a free block if it is the same size as query or split it if possible */
        if (block->capacity != query) {
            if (!block_splittable(block, query)) continue;
            block_split(block, query);
        }

        block->is_free = false;
        return (char*) block + sizeof(struct mem);
    }

    page_size = query + sizeof(struct mem);
    if (page_size < HEAP_PAGE_SIZE) page_size = HEAP_PAGE_SIZE;
    page_end = (char*) prev + sizeof(struct mem) + prev->capacity;

    /* the block is not found - creating a new page at the end of chain */
    retval = page_init(page_end, page_size, true);
    if (retval == MAP_FAILED) {
        /* cannot create a new page at the end of chain - creating somewhere */
        retval = page_init(page_end, page_size, false);
        if (retval == MAP_FAILED) return NULL;
    }

    prev->next = retval;
    retval->capacity = page_size - sizeof(struct mem);
    retval->is_free = false;
    retval->next = NULL;
    
    if (block_splittable(retval, query)) block_split(retval, query);

    return (char*) retval + sizeof(struct mem);
}

void mem_free(void *mem) {
    struct mem *block = (struct mem*) ((char*) mem - sizeof(struct mem));
    struct mem *next, *prev;

    /* mimic free() behaviour for NULL and non-heap addresses */
    if (!mem) return;
    if (block != heap_start) {
        for (prev = heap_start; prev->next && prev->next != block; prev = prev->next);
        if (prev->next != block) return;
    }

    block->is_free = true;

    /* merge with adjacent blocks if possible */
    next = block->next;
    if (next && next->is_free && blocks_sequential(block, next)) blocks_merge(block, next);
    if (block != heap_start && prev->is_free && blocks_sequential(prev, block)) blocks_merge(prev, block);
}

void memalloc_debug_struct_info(FILE *f, struct mem const * const address) {
    uint64_t i;
    char *ptr = (char*) address + sizeof(struct mem);

    fprintf(f, "start: %p, size: %4lu, is_free: %d, bytes:", (void*) address, address->capacity, address->is_free);
    for (i = 0; i < DEBUG_FIRST_BYTES && i < address->capacity; i++, ptr++) {
        fprintf(f, " %2hhX", *ptr);
    }
    putc('\n', f);
}

void memalloc_debug_heap(FILE *f) {
    struct mem *ptr = heap_start;
    for ( ; ptr; ptr = ptr->next) memalloc_debug_struct_info(f, ptr);
}