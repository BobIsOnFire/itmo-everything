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
 * list: pointer to the start of the list (start is a pointer to the first
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
 * list: pointer to the start of the list (start is a pointer to the first
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
 * list_add_after - create a node and insert it after a specified one.
 * 
 * node: pointer to the list node.
 * value: a value to assign to the new node.
 * 
 * If a specified node pointer is NULL, the result is the same as list_create.
 * 
 * Returns a pointer to the new list node.
 */
list_node *list_add_after(list_node *node, int value);

/*
 * list_node_at - get a node at a specified index.
 * 
 * list: start of the list (start is a pointer to the first element).
 * index: index of the node to search.
 * 
 * Index of the node is a number of links to follow from the start of the list. 
 * 
 * Returns a found node, or NULL if index is greater than list length. If there
 * is any error, errno is provided respectively.
 */
list_node *list_node_at(list_node *list, size_t index);

/*
 * list_get - get a node value at a specified index.
 * 
 * list: start of the list (start is a pointer to the first element).
 * index: index of the node to search.
 * 
 * Index of the node is a number of links to follow from the start of the list.
 * 
 * Returns a found node value, or -1 if index is greater than list length. If
 * there is any error, errno is provided respectively.
 */
int list_get(list_node *list, size_t index);

/*
 * list_free - free all memory allocated for the list nodes.
 * 
 * list: start of the list (start is a pointer to the first element).
 * 
 * Pointer to the first element is not freed - only memory allocated for nodes
 * themselves.
 */
void list_free(list_node *list);

/*
 * list_length - get the length of the linked list.
 * 
 * list: start of the list (start is a pointer to the first element).
 * 
 * Returns the length of the list.
 */
size_t list_length(list_node *list);

/*
 * list_sum - get the sum of all node values in the linked list.
 * 
 * list: start of the list (start is a pointer to the first element).
 * 
 * Returns the sum of values.
 */
long list_sum(list_node *list);

/*
 * foreach - launch a function for each node value in the list.
 * 
 * list: start of the list (start is a pointer to the first element).
 * consumer: a function that accepts integer (node value) and returns nothing.
 * 
 * The order of function calls is defined by the order of elements in the list.
 */
void foreach(list_node *list, void (*consumer)(int));

/*
 * map - create a new list, where every node value is the result of operator
 * applied to every node value of original list.
 * 
 * list: start of the list (start is a pointer to the first
 * element).
 * operator: a function that accepts integer (node value) and returns integer
 * (new node value).
 * 
 * The order of nodes in the mapped list is the same as in the original list. 
 * 
 * Returns a pointer to the first element of new list.
 */
list_node *map(list_node *list, int (*operator)(int));

/*
 * map_mut - update values of the list by applying an operator to every node
 * value.
 * 
 * list: pointer to the start of the list (start is a pointer to the first
 * element).
 * operator: a function that accepts integer (node value) and returns integer
 * (new node value).
 * 
 * Returns a pointer to the start of the list (which is essentially *list).
 */
list_node *map_mut(list_node **list, int (*operator)(int));

/*
 * foldl - reduce a node list to the accumulator value using binary operator.
 * 
 * list: start of the list (start is a pointer to the first element).
 * acc: accumulator starting value.
 * operator: a function that accepts two integers (node value and accumulator)
 * and returns integer (new accumulator value).
 * 
 * Returns accumulator value after iterating a whole list.
 */
int foldl(list_node *list, int acc, int (*operator)(int, int));

/*
 * iterate - populate a list with initial value and iteration operator.
 * 
 * value: value of the first node in the new list.
 * length: total size of the new list.
 * operator: a function that accepts integer (current node value)
 * and returns integer (next node value).
 * 
 * Returns a pointer to the first element of the list.
 */
list_node *iterate(int value, size_t length, int (*operator)(int));

/*
 * save - write all node values in the text file consecutively.
 * 
 * list: start of the list (start is a pointer to the first element).
 * filename: name of the file to save the list.
 * 
 * Each value is separated with exactly one space. There is also one space in
 * the end of file, and no newline following it.
 * 
 * Returns 1 if list is successfully saved, 0 otherwise. If there is any error,
 * errno is provided respectively.
 */
int save(list_node *list, const char *filename);

/*
 * load - read the list node values from the text file consecutively.
 * 
 * list: pointer to the start of the list (start is a pointer to the first
 * element).
 * filename: name of the file to load the list.
 * 
 * Each value should be separated by any amount of whitespace characters.
 * 
 * Returns 1 if list is successfully loaded, 0 otherwise. If there is any
 * error, errno is provided respectively.
 */
int load(list_node **list, const char *filename);

/*
 * serialize - write all node values in the binary file consecutively.
 * 
 * list: start of the list (start is a pointer to the first element).
 * filename: name of the file to save the list.
 * 
 * Values are stored as the int array in the file, with no separators etc.
 * 
 * Returns 1 if list is successfully serialized, 0 otherwise. If there is any
 * error, errno is provided respectively.
 */
int serialize(list_node *list, const char *filename);

/*
 * deserialize - read the list node values from the binary file consecutively.
 * 
 * list: pointer to the start of the list (start is a pointer to the first
 * element).
 * filename: name of the file to load the list.
 * 
 * Values are stored as the int array in the file, with no separators etc.
 * 
 * Returns 1 if list is successfully deserialized, 0 otherwise. If there is any
 * error, errno is provided respectively.
 */
int deserialize(list_node **list, const char *filename);

#endif
