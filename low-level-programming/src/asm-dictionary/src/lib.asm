%include "lib.inc"

%include "definitions.inc"

%define NUMBER_BUFFER_SIZE 24

section .text

; Принимает указатель на нуль-терминированную строку, возвращает её длину
; rdi - указатель на нуль-терминированную строку
; Идея взята с http://www.int80h.org/strlen/
string_length:
        xor     rcx, rcx
        not     rcx
        xor     rax, rax
repne   scasb
        not     rcx
        lea     rax, [rcx - 1]
        ret     rax


; Helper: Принимает указатель на нуль-терминированную строку, пишет её по
; указанному дескриптору
; rdi - указатель на нуль-терминированную строку
; rsi - файл-дескриптор
_write_string:
        push    rdi, rsi
        call    string_length, rdi
        pop     rsi, rdi
        write   rdi, rsi, rax
        ret


; Принимает указатель на нуль-терминированную строку, выводит её в stdout
; rdi - указатель на нуль-терминированную строку
print_string:
        mov     rsi, stdout
        jmp     _write_string


; Принимает указатель на нуль-терминированную строку, выводит её в stderr
; rdi - указатель на нуль-терминированную строку
err_string:
        mov     rsi, stderr
        jmp     _write_string


; Переводит строку (выводит символ с кодом 0xA)
print_newline:
        mov     rdi, EOL
        ; co-routine optimization

; Принимает код символа и выводит его в stdout
; rdi - код символа
print_char:
        and     rdi, 0xFF
        push    di; 2 байта - с кодом символа и 0x0
        write   stdout, rsp, 1
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
        or      dl, '0'
        dec     rsi
        mov     byte[rsi], dl
        test    rax, rax
        jnz     .loop
        ret     rax


; Выводит беззнаковое 8-байтовое число в десятичном формате 
; Совет: выделите место в стеке и храните там результаты деления
; Не забудьте перевести цифры в их ASCII коды.
; rdi - беззнаковое 8-байтовое число
print_uint:
        mov     rsi, rsp
        sub     rsp, NUMBER_BUFFER_SIZE
        call    _format_uint, rdi, rsi
        call    print_string, rsi
        add     rsp, NUMBER_BUFFER_SIZE
        ret     rax


; Выводит знаковое 8-байтовое число в десятичном формате 
; rdi - беззнаковое 8-байтовое число
print_int:
        test    rdi, rdi
        jns     print_uint
        neg     rdi
        mov     rsi, rsp
        sub     rsp, NUMBER_BUFFER_SIZE
        call    _format_uint, rdi, rsi
        dec     rsi
        mov     byte[rsi], '-'
        call    print_string, rsi
        add     rsp, NUMBER_BUFFER_SIZE
        ret     rax


; Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
; rdi - указатель на первую строку
; rsi - указатель на вторую строку
string_equals:
        mov     r9, rdi
        mov     r8, rsi
        push    r8, r9
        call    string_length, rdi
        push    rax
        call    string_length, rsi
        pop     r8, r9, rcx
        cmp     rax, rcx
        jne     .fail
.bytecmp:
        test    rax, rax
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
        ret     1
.fail:
        ret     0


; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char:
        push    qword 0
        read    stdin, rsp, 1
        test    rax, rax
        je      .fail
.success:
        pop     rax
        ret     rax
.fail:
        add     rsp, 8
        ret     0


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
        cmp     al, ' '
        je      .skip
        cmp     al, TAB
        je      .skip
        cmp     al, EOL
        je      .skip
.loop:
        dec     r9
        jz      .fail
        mov     byte[r8], al
        inc     r8
        call    read_char
        test    al, al
        jz      .success
%ifdef READ_WORD_NO_END_ON_SPACE
        cmp     al, ' '
        je      .success
        cmp     al, TAB
        je      .success
%endif
        cmp     al, EOL
        je      .success
        jmp     .loop
.success:
        mov     byte[r8], 0
        pop     rax
        mov     rdx, r8
        sub     rdx, rax
        ret     rax
.fail:
        add     rsp, 8
        xor     rdx, rdx
        ret     0


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
        call    parse_uint, rdi
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
        push    rdi, rsi, rdx
        call    string_length, rdi
        pop     rdi, rsi, rdx
        push    rax
        inc     rax
        cmp     rax, rdx
        jg      .fail
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
        ret     rax
.fail:
        add     rsp, 8
        ret     0
