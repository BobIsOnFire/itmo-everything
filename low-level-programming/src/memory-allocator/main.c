#include "mem.h"

int main(void) {
    heap_init(HEAP_PAGE_SIZE);
    puts("0"); memalloc_debug_heap(stdout);

    char *str = mem_alloc(2000);
    puts("1b"); memalloc_debug_heap(stdout);
    char *str2 = mem_alloc(2000);
    puts("2b"); memalloc_debug_heap(stdout);
    char *str3 = mem_alloc(2000);
    puts("3b"); memalloc_debug_heap(stdout);

    scanf("%s", str);
    printf("%s\n", str);
    puts("4"); memalloc_debug_heap(stdout);

    mem_free(str);
    puts("1a"); memalloc_debug_heap(stdout);
    mem_free(str3);
    puts("2a"); memalloc_debug_heap(stdout);
    mem_free(str2);
    puts("3a"); memalloc_debug_heap(stdout);
}
