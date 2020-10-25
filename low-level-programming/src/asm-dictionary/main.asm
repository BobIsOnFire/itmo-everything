%include "words.inc"
%define BUFFER_SIZE 255
%define g global
%define e extern

g _start
e read_line
e find_word
e string_length
e print_string
e print_newline
e exit
e err_string
e isatty

section .data
err_msg_colored: db 0x1B, '[31m', 'ERROR', 0x1B, '[0m', ': Cannot find element in list', 10, 0
err_msg: db 'ERROR: Cannot find element in list', 10, 0


section .text
_start:
    sub rsp, BUFFER_SIZE
    mov rdi, rsp
    mov rsi, BUFFER_SIZE
    call read_line

    mov rsi, last
    mov rdi, rax
    call find_word

    add rsp, BUFFER_SIZE

    test rax, rax
    jz .fail

    mov rdi, rax
    call string_length

    add rdi, rax
    inc rdi
    call print_string
    call print_newline

    mov rdi, 0
    call exit

.fail:
    mov rdi, 2
    call isatty
    
    test rax, rax
    jnz .fail_color
    mov rdi, err_msg
    jmp .fail_print

.fail_color:
    mov rdi, err_msg_colored

.fail_print:
    call err_string
    
    mov rdi, 1
    call exit

; 1. escape codes handle
; 2. save a list to a file, get it from and make it working
; 3. ret at the end of _start