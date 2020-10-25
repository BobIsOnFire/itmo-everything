#include <errno.h>

#include "bi_linked_list.h"

bi_list_node *bi_list_create(int value) {
    bi_list_node *node = malloc(sizeof(bi_list_node));
    node->value = value;
    node->next = NULL;
    node->prev = NULL;
    return node;
}

bi_list_node *bi_list_node_at(bi_list_node *list, size_t index) {
    size_t i;
    bi_list_node *iter = list;
    if (iter == NULL) {
        errno = EFAULT;
        return NULL;
    }
    
    for (i = 0; i < index; i++) {
        if (iter->next == NULL) {
            errno = EINVAL;
            return NULL;
        }
        iter = iter->next;
    }

    return iter;
}


bi_list_node *bi_list_add_after(bi_list_node *node, int value) {
    bi_list_node *new_node = bi_list_create(value);
    if (node != NULL) {
        new_node->next = node->next;
        node->next = new_node;
        new_node->prev = node;
        if (new_node->next != NULL) new_node->next->prev = new_node;
    }
    return new_node;
}

void bi_remove_node(bi_list_node *node) {
    bi_list_node *prev = node->prev;
    bi_list_node *next = node->next;
    if (prev != NULL) prev->next = next;
    if (next != NULL) next->prev = prev;
    free(node);
}

bi_list_node *bi_iterate(int value, size_t length, int (*operator)(int)) {
    size_t i;
    bi_list_node *start, *iter;

    start = bi_list_create(value);
    iter = start;

    for (i = 1; i < length; i++) {
        value = operator(value);
        iter = bi_list_add_after(iter, value);
    }

    return start;
}

void bi_list_free(bi_list_node *list) {
    bi_list_node *iter = list;
    while(iter != NULL) {
        bi_list_node *prev = iter;
        iter = iter->next;
        free(prev);
    }
}
