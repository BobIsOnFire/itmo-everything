/*
 * Разработать программу на языке С, которая осуществляет следующие действия
 *
 * 1. Создает область памяти размером 234 мегабайт, начинающихся с адреса 0x36483936
 * (если возможно) при помощи mmap, заполненную случайными числами /dev/urandom в
 * 122 потоков. Используя системные средства мониторинга определите адрес начала в
 * адресном пространстве процесса и характеристики выделенных участков памяти.
 *
 * Замеры виртуальной/физической памяти необходимо снять:
 *
 * - До аллокации
 * - После аллокации
 * - После заполнения участка данными
 * - После деаллокации
 *
 * 2. Записывает область памяти в файлы одинакового размера 33 мегабайт с использованием
 * некешируемого обращения к диску. Размер блока ввода-вывода 6 байт. Преподаватель выдает
 * в качестве задания последовательность записи/чтения блоков - случайный.
 * Генерацию данных и запись осуществлять в бесконечном цикле.
 *
 * 3. В отдельных 200 потоках осуществлять чтение данных из файлов и подсчитывать
 * агрегированные характеристики данных - минимальное значение.
 *
 * Чтение и запись данных в/из файла должна быть защищена примитивами синхронизации sema.
 * 
 * 4. По заданию преподавателя изменить приоритеты потоков и описать изменения в
 * характеристиках программы.
 * 
 * 5. Измерить значения затраченного процессорного времени на выполнение программы и на
 * операции ввода-вывода используя системные утилиты.
 *
 * 6. Отследить трассу системных вызовов.
 * 
 * 7. Используя stap построить графики системных характеристик.
 */

#include <sys/mman.h>
#include <sys/random.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <limits.h>

#include <pthread.h>
#include <semaphore.h>

#define MEMORY_ADDRESS 0x36483936
#define MEMORY_SIZE (234 * 1024 * 1024) /* 234 MB */
#define WRITE_THREADS_COUNT 122
#define FILE_SIZE (33 * 1024 * 1024) /* 33 MB */
#define FILE_COUNT (MEMORY_SIZE / FILE_SIZE + 1)
#define IO_BLOCK_SIZE 6 /* 6 B */
#define READ_THREADS_COUNT 200

#define FILE_OPEN_FLAGS O_RDWR | O_CREAT | __O_DIRECT
#define FILE_OPEN_MODE S_IWUSR | S_IRUSR | S_IRGRP | S_IROTH
#define FILE_TEMPLATE "/tmp/os-rand-file-%lu"

static sem_t tmp_sem[FILE_COUNT];
static int tmp_fd[FILE_COUNT];

static volatile int thread_initialized;
static pthread_t write_threads[WRITE_THREADS_COUNT];
static pthread_t read_threads[READ_THREADS_COUNT];

struct write_thread_args {
    char *start;
    size_t size;
};

struct read_thread_args {
    size_t num;
    off_t offset;
    size_t size;
};

void sem_setup( void ) {
    size_t i;
    for (i = 0; i < FILE_COUNT; i++) {     
        char filename[22];

        /* create a sparse tmp file and a semaphore for it (1 slot) */
        sprintf(filename, FILE_TEMPLATE, i);
        tmp_fd[i] = open(filename, FILE_OPEN_FLAGS, FILE_OPEN_MODE);
        ftruncate(tmp_fd[i], FILE_SIZE);

        sem_init(&tmp_sem[i], 0, 1);
    }
}

void do_write_block(size_t count, char *start, size_t size) {
    thread_initialized = 1;
    size_t lock_count = 0;
    while (1) {
        size_t num, i;
        /* write random bytes into designated memory space */
        getrandom(start, size, 0);
        
        /* acquire a lock on a random file */
        num = rand() % FILE_COUNT;
        sem_wait(&tmp_sem[num]);
        lock_count++;

        /* write bytes from memory space to random offsets in the file */
        fprintf(stderr, "[write %3lu] Acquired lock to file #%lu (for the %lu time)\n", count, num, lock_count);
        for (i = 0; i < size; i += IO_BLOCK_SIZE) {
            off_t offset = rand() % (FILE_SIZE - IO_BLOCK_SIZE);
            lseek(tmp_fd[num], offset, SEEK_SET);
            write(tmp_fd[num], start + i, IO_BLOCK_SIZE);
        }

        /* release lock */
        fprintf(stderr, "[write %3lu] Lock to file #%lu released\n", count, num);
        sem_post(&tmp_sem[num]);
    }
}

void *write_block(void *args) {
    static size_t count = 0;

    struct write_thread_args *v_args = args;
    char *start = v_args->start;
    size_t size = v_args->size;
        
    do_write_block(count, start, size);

    return NULL;
}

void write_threads_init(char *p) {
    size_t i;
    
    /* give each thread a dedicated memory space */
    size_t size = MEMORY_SIZE / WRITE_THREADS_COUNT;

    for (i = 0; i < WRITE_THREADS_COUNT; i++) {
        thread_initialized = 0;

        struct write_thread_args args = {p + i * size, size};
        pthread_create(&write_threads[i], NULL, write_block, &args);
        while (!thread_initialized);
        fprintf(stdout, "%3lu. Created write thread for block at %ld\n", (i + 1), (long) (p + i * size));
    }
}

void do_read_block(size_t count, size_t num, off_t offset, size_t size) {
    thread_initialized = 1;
    size_t lock_count = 0;
    while (1) {
        size_t i;
        char min = CHAR_MAX;
        char block[size];
        
        /* acquire lock on a file */
        sem_wait(&tmp_sem[num]);
        lock_count++;
        fprintf(stderr, " [read %3lu] Acquired lock to file #%lu (for the %lu time)\n", count, num, lock_count);

        /* read a block at the offset */
        lseek(tmp_fd[num], offset, SEEK_SET);
        read(tmp_fd[num], block, size);
        for (i = 0; i < size; i++) {
            if (min > block[i]) min = block[i];
        }

        /* release lock */
        fprintf(stderr, " [read %3lu] Lock to file #%lu released, min is %hhd\n", count, num, min);
        sem_post(&tmp_sem[num]);
    }
}

void *read_block(void *args) {
    static size_t count = 0;

    struct read_thread_args *v_args = args;
    size_t num = v_args->num;
    off_t offset = v_args->offset;
    size_t size = v_args->size;
        
    do_read_block(++count, num, offset, size);

    return NULL;

}

void read_threads_init(void) {
    size_t i, j;

    size_t threads_per_file = READ_THREADS_COUNT / FILE_COUNT;
    size_t size = FILE_SIZE / threads_per_file;

    for (i = 0; i < FILE_COUNT; i++) {
        for (j = 0; j < threads_per_file; j++) {
            thread_initialized = 0;
            struct read_thread_args args = {i, (j * size), size};

            pthread_create(&read_threads[i], NULL, read_block, &args);
            while (!thread_initialized);
            fprintf(stdout, "%3lu. Created read thread for file %lu, offset %ld, size %lu\n", (i * threads_per_file + j + 1), i, (j * size), size);
        }
    }
}

int main( void ) {
    srand((unsigned) time(NULL));
    sem_setup();

    size_t i;
    char *p = mmap(
        (void *) MEMORY_ADDRESS,
        MEMORY_SIZE,
        PROT_READ | PROT_WRITE,
        MAP_PRIVATE | MAP_ANONYMOUS,
        -1,
        0
    );

    write_threads_init(p);
    read_threads_init();
    while (1);
    return 0;
}
