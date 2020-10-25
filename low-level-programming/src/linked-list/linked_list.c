#include "linked_list.h"

list_node *list_create(int value) {
    list_node *node = malloc(sizeof(list_node));
    node->value = value;
    node->next = NULL;
    return node;
}

list_node *list_add_front(list_node **list, int value) {
    list_node *node = list_create(value);
    node->next = *list;
    *list = node;

    return node;
}

list_node *list_add_back(list_node **list, int value) {
    list_node* node = list_create(value);

    if (*list == NULL) {
        *list = node;
    } else {
        list_node *iter = *list;
        while(iter->next != NULL) iter = iter->next;
        iter->next = node;
    }

    return node;
}

list_node *list_node_at(list_node **list, size_t index) {
    size_t i;
    list_node *iter = *list;
    if (iter == NULL) return NULL;
    
    for (i = 0; i < index; i++) {
        if (iter->next == NULL) return NULL;
        iter = iter->next;
    }

    return iter;
}

int list_get(list_node **list, size_t index) {
    list_node *iter = list_node_at(list, index);
    if (iter == NULL) return -1;
    return iter->value;
}

void list_free(list_node **list) {
    list_node *iter = *list;
    while(iter != NULL) {
        list_node *prev = iter;
        iter = iter->next;
        free(prev);
    }
}

size_t list_length(list_node **list) {
    list_node *iter = *list;
    size_t len = 0;
    while (iter != NULL) {
        ++len;
        iter = iter->next;
    }
    return len;
}

long list_sum(list_node** list) {
    list_node *iter = *list;
    long sum = 0;
    while (iter != NULL) {
        sum += (long) iter->value;
        iter = iter->next;
    }
    return sum;
}
