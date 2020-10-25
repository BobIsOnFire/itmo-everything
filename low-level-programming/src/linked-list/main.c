#include <stdio.h>

#include "linked_list.h"

int main( void ) {
    int value;
    size_t index = 4;
    list_node *iter = NULL;
    list_node **list = &iter;

    while(scanf("%d", &value) != EOF) list_add_front(list, value);

    printf("List sum: %ld\n", list_sum(list));
    value = list_get(list, index);
    if (value == -1) {
        printf("Cannot get element at index %lu: list is too short\n", index);
    } else {
        printf("Value of element at index %lu is %d\n", index, value);
    }

    list_free(list);
    return 0;
}
