global find_word
extern string_equals

section .text

; rdi - pointer to string key to find
; rsi - pointer to the list beginning
find_word:
.loop:
    test rsi, rsi
    jz .end

    add rsi, 8
    call string_equals
    test rax, rax
    jnz .end
    
    mov rsi, [rsi - 8]
    jmp .loop

.end:
    mov rax, rsi
    ret
