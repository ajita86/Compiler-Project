.class public sample3
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static analysis Ljava/lang/String;
.field private static encryption Ljava/lang/String;
.field private static funct Ljava/lang/String;
.field private static plain Ljava/lang/String;
.field private static polyalphabet Ljava/lang/String;
.field private static shift I

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	sample3/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Lsample3;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; FUNCTION cypher
;
.method private static cypher(ILjava/lang/String;)Ljava/lang/String;

.var 0 is amount I
.var 2 is cypher Ljava/lang/String;
.var 4 is original Ljava/lang/String;
.var 1 is plaintext Ljava/lang/String;
.var 3 is result Ljava/lang/String;
;
; Sresult
;
;
; result=plaintext>>amount
;
	aload_1
	iload_0
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	astore_3
;
; Soriginal
;
;
; original=result>>[26-amount]
;
	aload_3
	bipush	26
	iload_0
	isub
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	astore	4
;
; cypher='encrypted and decrypted!'
;
	ldc	"encrypted and decrypted!"
	astore_2

	aload_2
	areturn

.limit locals 5
.limit stack 4
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
; FScypher[Iamount,Splaintext]{Sresult;result=plaintext>>amount;Soriginal;original=result>>[26-amount];cypher='encrypted and decrypted!';}
;
;
; print['\nEXECUTING: SAMPLE3\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\nEXECUTING: SAMPLE3\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Splain
;
;
; plain='lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam'
;
	ldc	"lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam"
	putstatic	sample3/plain Ljava/lang/String;
;
; Sfunct
;
;
; Ishift
;
;
; shift=14
;
	bipush	14
	putstatic	sample3/shift I
;
; funct=cypher[shift,plain]
;
	getstatic	sample3/shift I
	getstatic	sample3/plain Ljava/lang/String;
	invokestatic	sample3/cypher(ILjava/lang/String;)Ljava/lang/String;
	putstatic	sample3/funct Ljava/lang/String;
;
; print[funct]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample3/funct Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Sanalysis
;
;
; print['if statement:']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"if statement:"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; if[funct=='encrypted and decrypted!']{Sencryption;encryption=plain>>shift;analysis=encryption@;print['Encryption String analysis = '];print[analysis];print['\n\n'];Spolyalphabet;polyalphabet=encryption<<'poly';print['Encryption Polyalphabet = '];print[polyalphabet];print['\n\n'];}else{analysis=plain@;print['Plaintext String analysis = '];print[analysis];print['\n\n'];}
;
	getstatic	sample3/funct Ljava/lang/String;
	ldc	"encrypted and decrypted!"
	invokevirtual	java/lang/String.compareTo(Ljava/lang/String;)I
	ifeq	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifne	L001
;
; analysis=plain@
;
	getstatic	sample3/plain Ljava/lang/String;
	invokestatic	cypher/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample3/analysis Ljava/lang/String;
;
; print['Plaintext String analysis = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Plaintext String analysis = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[analysis]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample3/analysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
	goto	L002
L001:
;
; Sencryption
;
;
; encryption=plain>>shift
;
	getstatic	sample3/plain Ljava/lang/String;
	getstatic	sample3/shift I
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	putstatic	sample3/encryption Ljava/lang/String;
;
; analysis=encryption@
;
	getstatic	sample3/encryption Ljava/lang/String;
	invokestatic	cypher/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample3/analysis Ljava/lang/String;
;
; print['Encryption String analysis = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Encryption String analysis = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[analysis]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample3/analysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Spolyalphabet
;
;
; polyalphabet=encryption<<'poly'
;
	getstatic	sample3/encryption Ljava/lang/String;
	ldc	"poly"
	invokestatic	cypher/poly(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample3/polyalphabet Ljava/lang/String;
;
; print['Encryption Polyalphabet = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Encryption Polyalphabet = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[polyalphabet]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample3/polyalphabet Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
L002:
;
; print['DONE!\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"DONE!\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V

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
.limit stack 12
.end method
