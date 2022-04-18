%include "dict.inc"

%include "definitions.inc"
%include "lib.inc"

section .text

; rdi - pointer to string key to find
; rsi - pointer to the list beginning
find_word:
.loop:
    test        rsi, rsi
    jz          .end

    add         rsi, 8
    push        rdi, rsi
    call        string_equals, rdi, rsi
    pop         rdi, rsi
    test        rax, rax
    jnz         .end
    
    mov         rsi, [rsi - 8]
    jmp         .loop

.end:
    ret         rsi
