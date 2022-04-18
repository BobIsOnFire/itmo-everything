%include "main.inc"

%include "definitions.inc"
%include "dict.inc"
%include "isatty.inc"
%include "lib.inc"
%include "words.inc"

%define BUFFER_SIZE 256
%define red_text(x) 0x1B, '[31m', x, 0x1B, '[0m'

section .data
err_msg_colored: db red_text('ERROR'), ': Cannot find element in dictionary', EOL, 0
err_msg: db 'ERROR: Cannot find element in dictionary', EOL, 0


section .text
_start:
    sub         rsp, BUFFER_SIZE
    call        read_word, rsp, BUFFER_SIZE
    call        find_word, rax, head
.break:
    add         rsp, BUFFER_SIZE

    test        rax, rax
    jz          .fail

    push        rax
    call        string_length, rax
    pop         rdi

    add         rdi, rax
    inc         rdi
    call        print_string, rdi
    call        print_newline

    exit        EXIT_SUCCESS

.fail:
    call        isatty, stderr
    
    test        rax, rax
    jnz         .fail_color
    mov         rdi, err_msg
    jmp         .fail_print

.fail_color:
    mov         rdi, err_msg_colored

.fail_print:
    call        err_string, rdi
    exit        EXIT_FAILURE
