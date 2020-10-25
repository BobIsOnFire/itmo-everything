%define g global

g exit
g string_length

g write_string
g print_string
g err_string

g write_char
g print_char
g print_newline

g print_int
g print_uint

g read_char
g read_word
g read_line

g parse_uint
g parse_int

g string_equals
g string_copy

section .text

exit:
    mov rax, 60
    syscall

string_length:
    xor rax, rax
.loop:
    cmp byte[rdi + rax], 0
    je .end

    inc rax
    jmp .loop

.end:
    ret

write_string:
    push rsi
    call string_length
    mov rsi, rdi
    mov rdx, rax

    mov rax, 1
    pop rdi
    syscall
    ret

print_string:
    mov rsi, 1
    jmp write_string

err_string:
    mov rsi, 2
    jmp write_string

write_char:
    push rdi

    mov rdi, rsi
    mov rsi, rsp
    mov rdx, 1
    mov rax, 1

    syscall
    pop rax
    ret

print_char:
    mov rsi, 1
    jmp write_char

print_newline:
    mov rdi, 10
    jmp print_char

err_char:
    mov rsi, 2
    jmp write_char

print_int:
    mov rcx, 0x8000000000000000
    jmp print_uint.start

print_uint:
    xor rcx, rcx
.start:
    mov rsi, rsp
    dec rsp
    mov byte[rsp], 0x0

    mov rax, rdi
    mov r8, 10

    test rdi, rcx
    jz .loop
    neg rax

.loop:
    xor rdx, rdx
    div r8
    add rdx, 0x30
    dec rsp
    mov byte[rsp], dl

    test rax, rax
    jnz .loop

    test rdi, rcx
    jz .end

    dec rsp
    mov byte[rsp], 0x2D
.end:
    mov rdi, rsp
    push rsi
    call print_string
    pop rsi
    mov rsp, rsi
    ret

read_char:
    xor rdi, rdi
    mov rdx, 1
    xor rax, rax
    push rax
    mov rsi, rsp
    syscall

    test rax, rax
    jnz .end

    mov byte[rsi], 0

.end:
    mov al, byte[rsi]
    pop rdi
    ret

%macro read_template 2
%1:
    xor rcx, rcx
.loop:
    cmp rcx, rsi
    jg .fail
    
    push rcx
    push rsi
    push rdi
    call read_char
    pop rdi
    pop rsi
    pop rcx

%if %2 == 1
    cmp al, 0x20
    je .success
    cmp al, 0x9
    je .success
%endif

    cmp al, 0xA
    je .success
    cmp al, 0       ; EOF
    jle .end

    mov byte[rdi + rcx], al
    inc rcx
    jmp .loop

.fail:
    mov rax, 0
    ret

.success:
    test rcx, rcx
    jz .loop
.end:
    mov byte[rdi + rcx], 0x0
    mov rax, rdi
    mov rdx, rcx
    ret
%endmacro

read_template read_word, 1
read_template read_line, 0

; rdi points to a string
; returns rax: number, rdx: length
parse_uint:
    xor rcx, rcx
    xor rax, rax
    xor rsi, rsi
    mov r8, 10
.loop:
    mov sil, byte[rdi + rcx]
    
    test rsi, rsi
    jz .end
    
    sub rsi, 0x30

    cmp rsi, 0
    jl .end
    cmp rsi, 9
    jg .end

    mul r8
    add rax, rsi

    inc rcx
    jmp .loop

.end:
    mov rdx, rcx
    ret

; rdi points to a string
; returns rax: number, rdx : length
parse_int:
    xor rcx, rcx
    cmp byte[rdi], 0x2D       ; dash '-'
    jne .uint

    inc rcx
    inc rdi
.uint:
    push rcx
    call parse_uint
    pop rcx

    test rcx, rcx
    jz .end

    neg rax
    inc rdx

.end:
    ret

string_equals:
    xor rcx, rcx
    xor rax, rax
    xor rdx, rdx

.loop:    
    mov al, byte[rsi + rcx]
    mov dl, byte[rdi + rcx]

    cmp al, dl
    jne .fail

    test al, al
    jz .success

    inc rcx
    jmp .loop

.fail:
    mov rax, 0
    ret

.success:
    mov rax, 1
    ret

string_copy:    
    push rdi
    call string_length
    inc rax
    pop rdi

    cmp rax, rdx
    jg .fail

    xor rcx, rcx
    xor rax, rax
.loop:
    mov al, byte[rdi + rcx]
    mov byte[rsi + rcx], al

    test rax, rax
    jz .success

    inc rcx
    jmp .loop

.fail:
    mov rax, 0
    ret
.success:
    mov rax, rsi
    ret
