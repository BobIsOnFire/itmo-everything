%define SYS_READ 0
%define SYS_WRITE 1
%define SYS_EXIT 60

%define stdin 0
%define stdout 1

section .text

; Принимает код возврата и завершает текущий процесс
; rdi - код возврата
exit: 
        mov     rax, SYS_EXIT
        syscall


; Принимает указатель на нуль-терминированную строку, возвращает её длину
; rdi - указатель на нуль-терминированную строку
; Идея взята с http://www.int80h.org/strlen/
string_length:
        push    rdi
        xor     rcx, rcx
        not     rcx
        xor     rax, rax
repne   scasb
        not     rcx
        lea     rax, [rcx - 1]
        pop     rdi
        ret


; Принимает указатель на нуль-терминированную строку, выводит её в stdout
; rdi - указатель на нуль-терминированную строку
print_string:
        call    string_length
        mov     rsi, rdi
        mov     rdx, rax
        mov     rax, SYS_WRITE
        mov     rdi, stdout
        syscall
        ret


; Переводит строку (выводит символ с кодом 0xA)
print_newline:
        mov     rdi, 0xA


; Принимает код символа и выводит его в stdout
; rdi - код символа
print_char:
        push    di; 2 байта - с кодом символа и 0x0
        mov     rdi, rsp
        call    print_string
        pop     di
        ret


; Helper: форматирует беззнаковое 8-байтовое число в десятичный
; формат и кладет по заданному адресу
; rdi - беззнаковое 8-байтовое число
; rsi - адрес конца буфера
; По окончании выполнения в rsi адрес начала буфера
_format_uint:
        mov     r8, 10
        mov     rax, rdi
        dec     rsi
        mov     byte[rsi], 0
.loop:
        xor     rdx, rdx
        div     r8
        add     dl, '0'
        dec     rsi
        mov     byte[rsi], dl
        test    rax, rax
        jnz      .loop
        ret


; Выводит беззнаковое 8-байтовое число в десятичном формате 
; Совет: выделите место в стеке и храните там результаты деления
; Не забудьте перевести цифры в их ASCII коды.
; rdi - беззнаковое 8-байтовое число
print_uint:
        mov     rsi, rsp
        sub     rsp, 24
        call    _format_uint
        mov     rdi, rsi
        call    print_string
        add     rsp, 24
        ret


; Выводит знаковое 8-байтовое число в десятичном формате 
; rdi - беззнаковое 8-байтовое число
print_int:
        cmp     rdi, 0
        jge     print_uint
        neg     rdi
        mov     rsi, rsp
        sub     rsp, 24
        call    _format_uint
        dec     rsi
        mov     byte[rsi], '-'
        mov     rdi, rsi
        call    print_string
        add     rsp, 24
        ret


; Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
; rdi - указатель на первую строку
; rsi - указатель на вторую строку
string_equals:
        mov     r9, rdi
        mov     r8, rsi
        call    string_length
        push    rax
        mov     rdi, rsi
        call    string_length
        pop     rcx
        cmp     rax, rcx
        jne     .fail
.quadcmp:
        cmp     rax, 8
        jl      .bytecmp
        mov     rdi, [r8]
        mov     rsi, [r9]
        cmp     rdi, rsi
        jne     .fail
        add     r8, 8
        add     r9, 8
        sub     rax, 8
        jmp     .quadcmp
.bytecmp:
        cmp     rax, 0
        je      .success
        mov     dil, byte[r8]
        mov     sil, byte[r9]
        cmp     dil, sil
        jne     .fail
        inc     r8
        inc     r9
        dec     rax
        jmp     .bytecmp
.success:
        mov     rax, 1
        ret
.fail:
        xor     rax, rax
        ret


; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char:
        push    qword 0
        mov     rsi, rsp
        mov     rdx, 1
        mov     rax, SYS_READ
        mov     rdi, stdin
        syscall
        test    rax, rax
        je      .fail
.success:
        pop     rax
        ret
.fail:
        add     rsp, 8
        xor     rax, rax
        ret


; Принимает: адрес начала буфера, размер буфера
; Читает в буфер слово из stdin, пропуская пробельные символы в начале, .
; Пробельные символы это пробел 0x20, табуляция 0x9 и перевод строки 0xA.
; Останавливается и возвращает 0 если слово слишком большое для буфера
; При успехе возвращает адрес буфера в rax, длину слова в rdx.
; При неудаче возвращает 0 в rax
; Эта функция должна дописывать к слову нуль-терминатор
; rdi - адрес начала буфера
; rsi - размер буфера
read_word:
        push    rdi
        mov     r8, rdi
        mov     r9, rsi
.skip:
        call    read_char
        test    al, al
        jz      .fail
        cmp     al, 0x20
        je      .skip
        cmp     al, 0x9
        je      .skip
        cmp     al, 0xA
        je      .skip
.loop:
        dec     r9
        jz      .fail
        mov     byte[r8], al
        inc     r8
        call    read_char
        test    al, al
        jz      .success
        cmp     al, 0x20
        je      .success
        cmp     al, 0x9
        je      .success
        cmp     al, 0xA
        je      .success
        jmp     .loop
.success:
        mov     byte[r8], 0
        pop     rax
        mov     rdx, r8
        sub     rdx, rax
        ret
.fail:
        add     rsp, 8
        xor     rax, rax
        xor     rdx, rdx
        ret


; Принимает указатель на строку, пытается
; прочитать из её начала беззнаковое число.
; Возвращает в rax: число, rdx : его длину в символах
; rdx = 0 если число прочитать не удалось
; rdi - указатель на строку
parse_uint:
        xor     rdx, rdx
        xor     rax, rax
        mov     r8, 10
.loop:
        xor     rcx, rcx
        mov     cl, byte[rdi]
        sub     cl, '0'
        cmp     cl, 0
        jl      .end
        cmp     cl, 9
        jg      .end
        imul    rax, rax, 10
        add     rax, rcx
        inc     rdi
        inc     rdx
        jmp     .loop
.end:
        ret


; Принимает указатель на строку, пытается
; прочитать из её начала знаковое число.
; Если есть знак, пробелы между ним и числом не разрешены.
; Возвращает в rax: число, rdx : его длину в символах (включая знак, если он был) 
; rdx = 0 если число прочитать не удалось
; rdi - указатель на строку
parse_int:
        mov     cl, byte[rdi]
        cmp     cl, '-'
        jne     parse_uint
        inc     rdi
        call    parse_uint
        test    rdx, rdx
        jz      .end
        inc     rdx
        neg     rax
.end:
        ret 


; Принимает указатель на строку, указатель на буфер и длину буфера
; Копирует строку в буфер
; Возвращает длину строки если она умещается в буфер, иначе 0
; rdi - указатель на строку
; rsi - указатель на буфер
; rdx - длина буфера
string_copy:
        call    string_length
        push    rax
        inc     rax
        cmp     rax, rdx
        jg      .fail
.quadcpy:
        cmp     rax, 8
        jl      .bytecpy
        mov     r8, [rdi]
        mov     [rsi], r8
        add     rdi, 8
        add     rsi, 8
        sub     rax, 8
        jmp     .quadcpy
.bytecpy:
        cmp     rax, 0
        je      .success
        mov     r8b, byte[rdi]
        mov     byte[rsi], r8b
        inc     rdi
        inc     rsi
        dec     rax
        jmp     .bytecpy
.success:
        pop     rax
        ret
.fail:
        add     rsp, 8
        xor     rax, rax
        ret

