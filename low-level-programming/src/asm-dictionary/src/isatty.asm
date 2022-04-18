%include "isatty.inc"

%include "definitions.inc"

%define BUFFER_SIZE 256

section .text
isatty:
    sub         rsp, BUFFER_SIZE
    ioctl       rdi, TCGETS, rsp
    add         rsp, BUFFER_SIZE

    test        rax, rax
    setz        al
    and         rax, 0xFF
    ret         rax
