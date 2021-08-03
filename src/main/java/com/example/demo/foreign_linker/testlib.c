#include <stdlib.h>

int square(int x) {
  return x * x;
}

int *  allocmemory(int bytes) {
  return (int *) malloc(bytes);
}

void freeMemory(int *mem) {
  free(mem);
}
