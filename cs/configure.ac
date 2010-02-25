#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.64])
AC_INIT(sa4cob2db, 0.1, kkimmg@gmail.com)
AC_CONFIG_SRCDIR([jniclient.c])
AC_CONFIG_HEADERS([config.h])

AC_DEFINE_UNQUOTED(ACM_HOME,    "/opt/sa4cob2db")

# Checks for programs.
AC_PROG_CC
AC_PROG_INSTALL
AC_PROG_LN_S

#AC_SUBST(JAVA_HOME, /usr/lib/jvm/java-6-sun)
AC_ARG_VAR(JAVA_HOME, java home)
AC_CHECK_FILE($JAVA_HOME, , AC_MSG_ERROR(Prease Set JAVA_HOME Environment))

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