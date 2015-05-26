#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.64])
AC_INIT(sa4cob2db, 0.1, kkimmg@gmail.com)
AC_CONFIG_SRCDIR([jniclient.c])
AC_CONFIG_HEADERS([config.h])
AC_CANONICAL_SYSTEM

if test "" == "$ACM_HOME"; then
    ACM_HOME="/opt/sa4cob2db"
fi

AC_SUBST(ACM_HOME)
AC_DEFINE_UNQUOTED(ACM_HOME, "/opt/sa4cob2db")
AC_ARG_VAR(ACM_HOME, install directory)

# Checks for programs.
AC_PROG_CC
AC_PROG_INSTALL
AC_PROG_LN_S

#AC_SUBST(JAVA_HOME, /usr/lib/jvm/java-6-sun)
AC_ARG_VAR(JAVA_HOME, java home)
AC_CHECK_FILE($JAVA_HOME, , AC_MSG_ERROR(Prease Set JAVA_HOME Environment))

# OS NAME
AC_SUBST(OS_NAME, $host_os)
case "$OS_NAME" in
	*freebsd*)
		OS_NAME="freebsd"
		;;
	*linux*)
		OS_NAME="linux"
		;;
esac 
AC_ARG_VAR(OS_NAME, os name)
AC_CHECK_FILE($JAVA_HOME/include/$OS_NAME, , AC_MSG_ERROR(Prease Set OS_NAME Environment))

# OS ARCH
AC_SUBST(OS_ARCH, $host_cpu)
case "$OS_ARCH" in
	*i*86*)
		OS_ARCH="i386"
		;;
	*64*)
		OS_ARCH="amd64"
		;;
esac
AC_ARG_VAR(OS_ARCH, os arch)
AC_CHECK_FILE($JAVA_HOME/jre/lib/$OS_ARCH/server, , AC_MSG_ERROR(Prease Set OS_ARCH Environment))

# Checks for libraries.
# FIXME: Replace `main' with a function in `-ljvm':
JAVA_CHECK_LIB="-L${JAVA_HOME}/jre/lib/$OS_ARCH/server"
LIBS="${JAVA_CHECK_LIB} ${defaultlibs}"
AC_CHECK_LIB([jvm], [JNI_CreateJavaVM], [JAVA_LDFLAGS="${JAVA_CHECK_LIB} -ljvm"], [AC_MSG_ERROR([could not find libjvm])])

# Checks for header files.
AC_CHECK_HEADERS([arpa/inet.h netdb.h netinet/in.h stdlib.h string.h sys/param.h sys/socket.h unistd.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
AC_CHECK_FUNCS([gethostbyname memmove memset socket])

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
