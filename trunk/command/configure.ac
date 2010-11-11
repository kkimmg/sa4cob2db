#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.64])
AC_INIT(sa4cob2db, 0.1, kkimmg@gmail.com)
AC_CONFIG_SRCDIR([sqlnetserver.c])
AC_CONFIG_HEADERS([config.h])

AC_DEFINE_UNQUOTED(ACM_HOME,    "/opt/sa4cob2db")

# Checks for programs.
AC_PROG_CC
AC_PROG_INSTALL


#AC_SUBST(JAVA_HOME, /usr/lib/jvm/java-6-sun)
AC_ARG_VAR(JAVA_HOME, java home)
AC_CHECK_FILE($JAVA_HOME, , AC_MSG_ERROR(Prease Set JAVA_HOME Environment))

# Checks for libraries.
# FIXME: Replace `main' with a function in `-ljvm':
JAVA_CHECK_LIB="-L${JAVA_HOME}/jre/lib/i386/server"
LIBS="${JAVA_CHECK_LIB} ${defaultlibs}"
AC_CHECK_LIB([jvm], [JNI_CreateJavaVM], [JAVA_LDFLAGS="${JAVA_CHECK_LIB} -ljvm"], [AC_MSG_ERROR([could not find libjvm])])

# Checks for header files.
AC_CHECK_HEADERS([stdlib.h string.h sys/param.h unistd.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
AC_FUNC_MALLOC

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
