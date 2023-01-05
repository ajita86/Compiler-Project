.class public sample1
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static cypher Ljava/lang/String;
.field private static cypheranalysis Ljava/lang/String;
.field private static i I
.field private static j F
.field private static plain Ljava/lang/String;
.field private static plaintextanalysis Ljava/lang/String;
.field private static polyalphabet Ljava/lang/String;
.field private static polyalphabetanalysis Ljava/lang/String;
.field private static result Ljava/lang/String;

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	sample1/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Lsample1;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; FUNCTION deshift
;
.method private static deshift(ILjava/lang/String;)Ljava/lang/String;

.var 0 is amount I
.var 1 is cyphertext Ljava/lang/String;
.var 2 is deshift Ljava/lang/String;
;
; deshift=cyphertext>>[26-amount]
;
	aload_1
	bipush	26
	iload_0
	isub
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	astore_2

	aload_2
	areturn

.limit locals 3
.limit stack 3
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
; FSdeshift[Iamount,Scyphertext]{deshift=cyphertext>>[26-amount];}
;
;
; print['\nEXECUTING: SAMPLE1\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\nEXECUTING: SAMPLE1\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Splain
;
;
; plain='the quick red fox jumped over the lazy brown dog'
;
	ldc	"the quick red fox jumped over the lazy brown dog"
	putstatic	sample1/plain Ljava/lang/String;
;
; Scypher
;
;
; cypher=plain>>2
;
	getstatic	sample1/plain Ljava/lang/String;
	iconst_2
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	putstatic	sample1/cypher Ljava/lang/String;
;
; Spolyalphabet
;
;
; polyalphabet=plain<<'polyalphabet'
;
	getstatic	sample1/plain Ljava/lang/String;
	ldc	"polyalphabet"
	invokestatic	cypher/poly(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample1/polyalphabet Ljava/lang/String;
;
; print['Cypher = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Cypher = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[cypher]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/cypher Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['Polyalphabet = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Polyalphabet = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[polyalphabet]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/polyalphabet Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; ScypherAnalysis
;
;
; cypherAnalysis=cypher@
;
	getstatic	sample1/cypher Ljava/lang/String;
	invokestatic	cypher/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample1/cypheranalysis Ljava/lang/String;
;
; SpolyalphabetAnalysis
;
;
; polyalphabetAnalysis=polyalphabet@
;
	getstatic	sample1/polyalphabet Ljava/lang/String;
	invokestatic	cypher/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample1/polyalphabetanalysis Ljava/lang/String;
;
; print['Cypher String Analysis = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Cypher String Analysis = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[cypherAnalysis]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/cypheranalysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['Polyalphabet String Analysis = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Polyalphabet String Analysis = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[polyalphabetAnalysis]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/polyalphabetanalysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Ii
;
;
; i=0
;
	iconst_0
	putstatic	sample1/i I
;
; Dj
;
;
; j=1.1
;
	ldc	1.100000023841858
	putstatic	sample1/j F
;
; print['while loop:']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"while loop:"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; while[i<10]{i=i+1;j=j*i;print[i];print['\n'];print[j];print['\n'];}
;
L001:
	getstatic	sample1/i I
	bipush	10
	if_icmplt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
;
; i=i+1
;
	getstatic	sample1/i I
	iconst_1
	iadd
	putstatic	sample1/i I
;
; j=j*i
;
	getstatic	sample1/j F
	getstatic	sample1/i I
	i2f
	fmul
	putstatic	sample1/j F
;
; print[i]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/i I
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
	getstatic	sample1/j F
	invokevirtual	java/io/PrintStream/print(F)V
;
; print['\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
	goto	L001
L002:
;
; print['\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Sresult
;
;
; result=deshift[2,cypher]
;
	iconst_2
	getstatic	sample1/cypher Ljava/lang/String;
	invokestatic	sample1/deshift(ILjava/lang/String;)Ljava/lang/String;
	putstatic	sample1/result Ljava/lang/String;
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
; if[plain==result]{print['decyphered!'];print['\n\n'];print['Plaintext = '];print[result];print['\n\n'];SplainTextAnalysis;plainTextAnalysis=result@;print['Plaintext Analysis = '];print[plainTextAnalysis];print['\n\n'];}else{print['bad logic!'];print['\n\n'];}
;
	getstatic	sample1/plain Ljava/lang/String;
	getstatic	sample1/result Ljava/lang/String;
	invokevirtual	java/lang/String.compareTo(Ljava/lang/String;)I
	ifeq	L007
	iconst_0
	goto	L008
L007:
	iconst_1
L008:
	ifne	L005
;
; print['bad logic!']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"bad logic!"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
	goto	L006
L005:
;
; print['decyphered!']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"decyphered!"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['Plaintext = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Plaintext = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[result]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/result Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; SplainTextAnalysis
;
;
; plainTextAnalysis=result@
;
	getstatic	sample1/result Ljava/lang/String;
	invokestatic	cypher/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample1/plaintextanalysis Ljava/lang/String;
;
; print['Plaintext Analysis = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Plaintext Analysis = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[plainTextAnalysis]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample1/plaintextanalysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
L006:
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
.limit stack 11
.end method
