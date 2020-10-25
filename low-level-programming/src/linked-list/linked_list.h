#ifndef _LINKED_LIST_H_
#define _LINKED_LIST_H_

#include <stdlib.h>

struct list_node {
    int value;
    struct list_node* next;
};

typedef struct list_node list_node;

/*
 * list_create - create a new list_node.
 * 
 * value: a value to assign to the node.
 * 
 * This function allocates memory for the node and assigns a specified value.
 * Memory for this node should be cleaned manually when not used.
 * 
 * Returns a pointer to the new list node.
 */
list_node *list_create(int value);

/*
 * list_add_front - add a new node to the beginning of the list.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * value: a value to assign to the new node.
 * 
 * Example of usage:
 * Before: {1}->{2}->{3}, **list points to &{1}.
 * list_add_front(**list, 10) creates a link {10}->{1} and updates **list.
 * After: {10}->{1}->{2}->{3}, **list points to &{10}.
 * 
 * Returns a pointer to the new list node.
 */
list_node *list_add_front(list_node **list, int value);

/*
 * list_add_back - add a new node to the end of the list.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * value: a value to assign to the new node.
 * 
 * Example of usage:
 * Before: {1}->{2}->{3}, **list points to &{1}.
 * list_add_back(**list, 10) creates a link {3}->{10}.
 * After: {1}->{2}->{3}->{10}, **list points to &{10}.
 * 
 * Returns a pointer to the new list node.
 */
list_node *list_add_back(list_node **list, int value);

/*
 * list_node_at - get a node at a specified index.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * index: index of the node to search.
 * 
 * Index of the node is a number of links to follow from the start of the list. 
 * 
 * Returns a found node, or NULL if index is greater than list length.
 */
list_node *list_node_at(list_node **list, size_t index);

/*
 * list_get - get a node value at a specified index.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * index: index of the node to search.
 * 
 * Index of the node is a number of links to follow from the start of the list.
 * 
 * Returns a found node value, or -1 if index is greater than list length.
 */
int list_get(list_node **list, size_t index);

/*
 * list_free - free all memory allocated for the list nodes.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * 
 * Pointer to the first element is not freed - only memory allocated for nodes
 * themselves.
 */
void list_free(list_node **list);

/*
 * list_length - get the length of the linked list.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * 
 * Returns the length of the list.
 */
size_t list_length(list_node **list);

/*
 * list_sum - get the sum of all node values in the linked list.
 * 
 * **list: pointer to the start of the list (start is a pointer to the first
 * element).
 * 
 * Returns the sum of values.
 */
long list_sum(list_node **list);

#endif
