#define     _GNU_SOURCE
#include    <stdio.h>
#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
#include    <getopt.h>
#include    <dlfcn.h>
#include    <sys/param.h>
#include    <sys/types.h>

int main(int argc, char *argv[]) {
    int ret;
    void *handle;
    int (*main_too)(int, char**);
    char *error;

    handle = dlopen ("libsqlnetserver.so", RTLD_LAZY);
    if (!handle) {
        fprintf (stderr, "%s\n", dlerror());
        exit(1);
    }

    dlerror();
    main_too = dlsym(handle, "main_too");
    if ((error = dlerror()) != NULL)  {
        fprintf (stderr, "%s\n", error);
        exit(1);
    }

    ret = main_too(argc, argv);
    dlclose(handle);
    return ret;
}
