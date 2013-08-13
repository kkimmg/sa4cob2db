#include <dlfcn.h>

int main(int argc, char *argv[]) {
	int ret;
    void *handle;
    int (*main_too)(int, char);
    char *error;

    handle = dlopen ("libacm2sql.so", RTLD_LAZY);
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
    return 0;
}
