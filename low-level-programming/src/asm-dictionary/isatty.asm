global isatty

section .text
isatty:
    sub rsp, 256
    mov rax, 16         ; ioctl syscall
    mov rsi, 0x5401     ; get attributes
    mov rdx, rsp        ; argp*, will not need it though
    syscall
    add rsp, 256

    test rax, rax
    jz .success

    mov rax, 0
    ret

.success:
    mov rax, 1
    ret
