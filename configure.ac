#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.64])
AC_INIT(sa4cob2db, 0.1, kkimmg@gmail.com)
AC_CONFIG_SRCDIR([cs/jniclient.c])
AC_CONFIG_HEADERS([cs/config.h])

if test "" == "$ACM_HOME"; then
    ACM_HOME="/opt/sa4cob2db"
fi

AC_SUBST(ACM_HOME)
AC_DEFINE_UNQUOTED(ACM_HOME, "/opt/sa4cob2db")
AC_ARG_VAR(ACM_HOME, install directory)

# Checks for programs.
AC_PROG_CC
AC_PROG_INSTALL
AC_CHECK_PROG(ANT, ant, yes, no)

# Checks for libraries.

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile])
AC_CONFIG_SUBDIRS([cs command])
AC_OUTPUT
