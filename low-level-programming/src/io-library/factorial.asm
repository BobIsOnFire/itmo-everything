%define O_RDONLY 0x0
%define PROT_READ 0x1
%define MAP_PRIVATE 0x2
%define BUFFER_SIZE 255

section .data
msg: db 'Enter file name:', 10, 0
err_msg: db 'ERROR: Invalid number.', 10, 0

section .text
%include "lib.inc"
global _start

read_open:
    mov rax, 2
    mov rsi, O_RDONLY
    mov rdx, 0

    syscall
    ret

read_close:
    mov rax, 3
    syscall
    ret

mmap:
    mov r8, rdi
    mov rax, 9
    mov rdi, 0              ; mapping destination (0 for OS to choose)

    mov rsi, 4096           ; page size
    mov rdx, PROT_READ      ; read-write-execute modes (here read-only)
    mov r10, MAP_PRIVATE    ; shared-private modes (here private)

    mov r9, 0               ; offset inside the map source
    syscall
    ret

factorial:
    mov rdx, 1
.loop:
    test rax, rax
    jz .end
    
    imul rdx, rax
    dec rax
    
    jmp .loop

.end:
    mov rax, rdx
    ret

_start:
    mov rdi, msg
    call print_string

    sub rsp, BUFFER_SIZE
    mov rdi, rsp
    mov rsi, BUFFER_SIZE
    call read_word

    mov rdi, rax
    call read_open

    add rsp, BUFFER_SIZE

    mov rdi, rax
    call mmap

    mov rdi, rax
    call parse_int

    cmp rax, 0
    jl .fail

    cmp rax, 20
    jg .fail

    mov rdi, rax
    call factorial

    mov rdi, rax
    call print_uint
    call print_newline

    mov rdi, 0
    call exit

.fail:
    mov rdi, err_msg
    call print_string

    mov rdi, 1
    call exit
