#ifndef _BI_LINKED_LIST_H_
#define _BI_LINKED_LIST_H_

#include <stdlib.h>

struct bi_list_node {
    int value;
    struct bi_list_node *prev;
    struct bi_list_node *next;
};

typedef struct bi_list_node bi_list_node;

bi_list_node *bi_list_create(int value);

bi_list_node *bi_list_node_at(bi_list_node *list, size_t index);

bi_list_node *bi_list_add_after(bi_list_node *node, int value);

void bi_remove_node(bi_list_node *node);

bi_list_node *bi_iterate(int value, size_t length, int (*operator)(int));

void bi_list_free(bi_list_node *list);

#endif
