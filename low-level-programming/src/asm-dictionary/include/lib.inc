%ifndef LIB_EXPORT
  %ifdef BUILDING_LIB
    %define LIB_EXPORT global
  %else
    %define LIB_EXPORT extern
  %endif
%endif

LIB_EXPORT string_length

LIB_EXPORT print_string
LIB_EXPORT err_string

LIB_EXPORT print_char
LIB_EXPORT print_newline

LIB_EXPORT print_int
LIB_EXPORT print_uint

LIB_EXPORT read_char
LIB_EXPORT read_word

LIB_EXPORT parse_uint
LIB_EXPORT parse_int

LIB_EXPORT string_equals
LIB_EXPORT string_copy

%define EOL 0xA
%define TAB 0x9
