global packed_mul

section .text

; rdi - pointer to result data - 12 floats
; rsi - pointer to mul1 (source data) - 36 floats
; rdx - pointer to mul2 (constants) - 36 floats
packed_mul:
    mov rcx, 4

.loop:
    movdqa xmm0, [rsi]
    movdqa xmm1, [rsi + 48]
    movdqa xmm2, [rsi + 96]

    mulps xmm0, [rdx]
    mulps xmm1, [rdx + 48]
    mulps xmm2, [rdx + 96]

    addps xmm0, xmm1
    addps xmm0, xmm2
    
    movdqa [rdi], xmm0
    add rdi, 16
    add rsi, 16
    add rdx, 16

    dec rcx
    test rcx, rcx
    ja .loop
    
    ret
