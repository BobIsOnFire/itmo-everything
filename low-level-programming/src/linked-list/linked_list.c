#include <stdio.h>
#include <errno.h>

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

list_node *list_add_after(list_node *node, int value) {
    list_node* new_node = list_create(value);
    if (node != NULL) {
        new_node->next = node->next;
        node->next = new_node;
    }
    return new_node;
}

list_node *list_node_at(list_node *list, size_t index) {
    size_t i;
    list_node *iter = list;
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

int list_get(list_node *list, size_t index) {
    list_node *iter = list_node_at(list, index);
    if (iter == NULL) return -1;
    return iter->value;
}

void list_free(list_node *list) {
    list_node *iter = list;
    while(iter != NULL) {
        list_node *prev = iter;
        iter = iter->next;
        free(prev);
    }
}

size_t list_length(list_node *list) {
    list_node *iter;
    size_t len = 0;
    for (iter = list; iter != NULL; iter = iter->next)
        ++len;
    return len;
}

long list_sum(list_node *list) {
    list_node *iter;
    long sum = 0;
    for (iter = list; iter != NULL; iter = iter->next)
        sum += (long) iter->value;
    return sum;
}

void foreach(list_node *list, void (*consumer)(int)) {
    list_node *iter;
    for (iter = list; iter != NULL; iter = iter->next)
        consumer(iter->value);
}

list_node *map(list_node *list, int (*operator)(int)) {
    list_node *iter, *new_iter = NULL, *start = NULL;

    for (iter = list; iter != NULL; iter = iter->next) {
        new_iter = list_add_after(new_iter, operator(iter->value));
        if (start == NULL) start = new_iter;
    }

    return start;
}

list_node *map_mut(list_node **list, int (*operator)(int)) {
    list_node *iter;
    for (iter = *list; iter != NULL; iter = iter->next)
        iter->value = operator(iter->value);

    return *list;
}

int foldl(list_node *list, int acc, int (*operator)(int, int)) {
    list_node *iter;
    for (iter = list; iter != NULL; iter = iter->next)
        acc = operator(iter->value, acc);
    
    return acc;
}

list_node *iterate(int value, size_t length, int (*operator)(int)) {
    size_t i;
    list_node *start, *iter;

    start = list_create(value);
    iter = start;

    for (i = 1; i < length; i++) {
        value = operator(value);
        iter = list_add_after(iter, value);
    }

    return start;
}

int save(list_node *list, const char *filename) {
    list_node *iter;
    FILE *f;
    errno = 0;
    f = fopen(filename, "w");
    if (errno) return 0;

    for (iter = list; iter != NULL; iter = iter->next) {
        fprintf(f, "%d ", iter->value);
        if (errno || ferror(f)) {
            fclose(f);
            return 0;
        }
    }

    fclose(f);
    if (errno) return 0;
    return 1;
}

int load(list_node **list, const char *filename) {
    list_node *iter = NULL, *start = NULL;
    int value;
    FILE *f;
    errno = 0;
    f = fopen(filename, "r");
    if (errno) return 0;
    
    while (1) {
        fscanf(f, "%d", &value);
        if (feof(f)) break;

        if (errno || ferror(f)) {
            fclose(f);
            return 0;
        }

        iter = list_add_after(iter, value);
        if (start == NULL) start = iter;
    }

    *list = start;
    fclose(f);
    if (errno) return 0;
    return 1;
}

int serialize(list_node *list, const char *filename) {
    list_node *iter;
    FILE *f;
    errno = 0;
    f = fopen(filename, "wb");
    if (errno) return 0;

    for (iter = list; iter != NULL; iter = iter->next) {
        fwrite(&iter->value, sizeof(int), 1, f);
        if (errno || ferror(f)) {
            fclose(f);
            return 0;
        }
    }

    fclose(f);
    if (errno) return 0;
    return 1;
}

int deserialize(list_node **list, const char *filename) {
    list_node *iter = NULL, *start = NULL;
    int value;
    FILE *f;
    errno = 0;
    f = fopen(filename, "r");
    if (errno) return 0;
    
    while (1) {
        fread(&value, sizeof(int), 1, f);
        if (feof(f)) break;

        if (errno || ferror(f)) {
            fclose(f);
            return 0;
        }

        iter = list_add_after(iter, value);
        if (start == NULL) start = iter;
    }

    *list = start;
    fclose(f);
    if (errno) return 0;
    return 1;
}
