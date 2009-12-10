#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.64])
AC_INIT(sa4cob2db, 0.1, kkimmg@gmail.com)
AC_CONFIG_SRCDIR([jniclient.c])
AC_CONFIG_HEADERS([config.h])

# Checks for programs.
AC_PROG_CC
AC_PROG_LN_S

# Checks for libraries.
# FIXME: Replace `main' with a function in `-ljvm':
AC_CHECK_LIB([jvm], [main])

# Checks for header files.
AC_CHECK_HEADERS([arpa/inet.h netdb.h netinet/in.h stdlib.h string.h sys/param.h sys/socket.h unistd.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
AC_CHECK_FUNCS([gethostbyname memmove memset socket])

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
