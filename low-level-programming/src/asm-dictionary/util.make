# Go away! Nothing to see here.

# Operates on $1 as a deque
push_front = $(strip $2 $1)
push_back =  $(strip $1 $2)

pop_front = $(strip $(wordlist 2,$(words $1),$1))
pop_back = $(strip $(call pop_front,$(wordlist 1,$(words $1),a $1)))

peek_front = $(firstword $1)
peek_back = $(lastword $1)


alphabet_lower_case := a b c d e f g h i j k l m n o p q r s t u v w x y z
alphabet_upper_case := A B C D E F G H I J K L M N O P Q R S T U V W X Y Z

# $1 - sentence to substitute, $2 - lowercase alphabet, $3 - uppercase alphabet
do_to_lowercase = $(if $(strip $2),                              \
	$(call do_to_lowercase,                                      \
		$(subst $(call peek_front,$3),$(call peek_front,$2),$1), \
		$(call pop_front,$2),                                    \
		$(call pop_front,$3)                                     \
	),                                                           \
	$1                                                           \
)

to_lowercase = $(strip $(call do_to_lowercase,$1,$(alphabet_lower_case),$(alphabet_upper_case)))

# $1 - sentence to substitute, $2 - lowercase alphabet, $3 - uppercase alphabet
do_to_uppercase = $(if $(strip $2),                              \
	$(call do_to_uppercase,                                      \
		$(subst $(call peek_front,$2),$(call peek_front,$3),$1), \
		$(call pop_front,$2),                                    \
		$(call pop_front,$3)                                     \
	),                                                           \
	$1                                                           \
)

to_uppercase = $(strip $(call do_to_uppercase,$1,$(alphabet_lower_case),$(alphabet_upper_case)))
