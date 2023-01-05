.class public sample
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static i I
.field private static j F
.field private static plain Ljava/lang/String;

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	sample/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Lsample;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; MAIN
;
.method public static main([Ljava/lang/String;)V
.var 0 is args [Ljava/lang/String;
.var 1 is _start Ljava/time/Instant;
.var 2 is _end Ljava/time/Instant;
.var 3 is _elapsed J

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_1

;
; print['hello\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"hello\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['test\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"test\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Splain
;
;
; plain='The quick red fox jumped over the lazy brown dog'
;
	ldc	"The quick red fox jumped over the lazy brown dog"
	putstatic	sample/plain Ljava/lang/String;
;
; Ii
;
;
; i=0
;
	iconst_0
	putstatic	sample/i I
;
; Dj
;
;
; j=1.1
;
	ldc	1.100000023841858
	putstatic	sample/j F
;
; print[plain]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample/plain Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[i]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample/i I
	invokevirtual	java/io/PrintStream/print(I)V
;
; print['\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[j]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample/j F
	invokevirtual	java/io/PrintStream/print(F)V
;
; print['\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; while[i<10]{print[i];i=i+1;}
;
L001:
	getstatic	sample/i I
	bipush	10
	if_icmplt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
;
; print[i]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample/i I
	invokevirtual	java/io/PrintStream/print(I)V
;
; i=i+1
;
	getstatic	sample/i I
	iconst_1
	iadd
	putstatic	sample/i I
	goto	L001
L002:

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_2
	aload_1
	aload_2
	invokestatic	java/time/Duration/between(Ljava/time/temporal/Temporal;Ljava/time/temporal/Temporal;)Ljava/time/Duration;
	invokevirtual	java/time/Duration/toMillis()J
	lstore_3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n[%,d milliseconds execution time.]\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	lload_3
	invokestatic	java/lang/Long/valueOf(J)Ljava/lang/Long;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

	return

.limit locals 6
.limit stack 7
.end method
