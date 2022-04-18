; Hey mom, look - I made a libc!



; Small optimization to use in `make_syscall` - push qword %1 only if %1 != %2 
%macro __pushne 2
    %ifnidni %1,%2
        push qword %1
    %endif
%endmacro

; Small optimization to use in `make_syscall` - pop %2 only if %1 != %2 
%macro __popne 2
    %ifnidni %1,%2
        pop %2
    %endif
%endmacro

; General syscall executor, puts syscall arguments into respective registers.
%macro make_syscall 1-7 rdi,rsi,rdx,r10,r8,r9
    push rcx
    lea rcx, [rsp + 8]
    %define rsp rcx
    __pushne %7, r9
    __pushne %6, r8
    __pushne %5, r10
    __pushne %4, rdx
    __pushne %3, rsi
    __pushne %2, rdi
    __pushne %1, rax
    %undef rsp
    __popne  %1, rax
    __popne  %2, rdi
    __popne  %3, rsi
    __popne  %4, rdx
    __popne  %5, r10
    __popne  %6, r8
    __popne  %7, r9
    pop rcx
    syscall

%endmacro



; Syscalls, but they are actually easy to use.

; ssize_t read(int fd, void *buf, size_t count)
;
; Attempts to read up to _count_ bytes from file descriptor _fd_ into the buffer
; starting at _buf_.
;
; On success, the number of bytes read is returned (zero indicates end of file).
; On error, -1 is returned.
%macro read 3
    make_syscall 0, %1, %2, %3
%endmacro

; File descriptors for read(), write(), ioctl()
%define stdin  0
%define stdout 1
%define stderr 2



; ssize_t write(int fd, const void *buf, size_t count)
;
; Writes up to _count_ bytes from the buffer starting at _buf_ to the file
; referred to by the file descriptor _fd_.
;
; On success, the number of bytes written is returned (zero indicates nothing
; was written).
; On error, -1 is returned.
%macro write 3
    make_syscall 1, %1, %2, %3
%endmacro



; int open(const char *pathname, int flags, mode_t mode)
;
; Opens the file specified by _pathname_. If the specified file does not exist,
; it may optionally (if O_CREAT is specified in _flags_) be created by open().
; The _mode_ argument specifies the file mode bits be applied when a new file
; is created.
;
; Returns the new file descriptor, or -1 if an error occurred.
%macro open 3
    make_syscall 2, %1, %2, %3
%endmacro

; Flags for open()
%define O_RDONLY    0o
%define O_WRONLY    1o
%define O_RDWR      2o
%define O_CREAT   100o
%define O_APPEND 2000o

; Modes for open()
%define S_IRWXU 700o
%define S_IRUSR 400o
%define S_IWUSR 200o
%define S_IXUSR 100o
%define S_IRWXG  70o
%define S_IRGRP  40o
%define S_IWGRP  20o
%define S_IXGRP  10o
%define S_IRWXO   7o
%define S_IROTH   4o
%define S_IWOTH   2o
%define S_IXOTH   1o



; int close(int fd)
;
; Closes a file descriptor _fd_.
;
; On success, returns 0.
; On error, -1 is returned.
%macro close 1
    make_syscall 3, %1
%endmacro



; int ioctl(int fd, unsigned long request, char *argp)
;
; Manipulates the underlying device parameters of special files. The argument
; _fd_ is an open file descriptor, _request_ - device-dependent request code,
; _argp_ - untyped pointer to memory, used by some requests.
;
; Usually, on success zero is returned.  A few ioctl() requests use the return
; value as an output parameter and return a nonnegative value on success.
;
; On error, -1 is returned.
%macro ioctl 3
    make_syscall 16, %1, %2, %3
%endmacro

; Requests for ioctl()
; TCGETS - Get the current serial port settings. Returns -1 if _fd_ is not a terminal.
%define TCGETS 0x5401



; void _exit(int status)
;
; Terminates the calling process "immediately" with _status_ exit code.
%macro exit 1
    make_syscall 60, %1
%endmacro

; Exit codes
%define EXIT_SUCCESS 0
%define EXIT_FAILURE 1



; push everything at once
%macro push 2-*
%rep %0
push %1
%rotate 1
%endrep
%endmacro

; push everything at once
%macro pop 2-*
%rep %0
%rotate -1
pop %1
%endrep
%endmacro


; General call executor, puts up to 6 arguments by convention.
%macro call 2-7 rsi,rdx,rcx,r8,r9
    push r10
    lea r10, [rsp + 8]
    %define rsp r10
    __pushne %7, r9
    __pushne %6, r8
    __pushne %5, rcx
    __pushne %4, rdx
    __pushne %3, rsi
    __pushne %2, rdi
    %undef rsp
    __popne  %2, rdi
    __popne  %3, rsi
    __popne  %4, rdx
    __popne  %5, rcx
    __popne  %6, r8
    __popne  %7, r9
    pop r10
    call %1

%endmacro

; put a thing into rax and return from function
%macro ret 1
%ifnidni %1,rax
    mov rax, %1
%endif
    ret
%endmacro
