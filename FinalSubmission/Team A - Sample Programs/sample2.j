.class public sample2
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static amount I
.field private static cypher Ljava/lang/String;
.field private static cypheranalysis Ljava/lang/String;
.field private static j I
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
	putstatic	sample2/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Lsample2;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; FUNCTION analysis
;
.method private static analysis(Ljava/lang/String;)Ljava/lang/String;

.var 1 is analysis Ljava/lang/String;
.var 0 is text Ljava/lang/String;
;
; analysis=text@
;
	aload_0
	invokestatic	cypher/analysis(Ljava/lang/String;)Ljava/lang/String;
	astore_1

	aload_1
	areturn

.limit locals 2
.limit stack 1
.end method

;
; FUNCTION encypher
;
.method private static encypher(ILjava/lang/String;)Ljava/lang/String;

.var 0 is amount I
.var 2 is encypher Ljava/lang/String;
.var 1 is plaintext Ljava/lang/String;
;
; encypher=plaintext>>amount
;
	aload_1
	iload_0
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	astore_2

	aload_2
	areturn

.limit locals 3
.limit stack 2
.end method

;
; FUNCTION poly
;
.method private static poly(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

.var 0 is key Ljava/lang/String;
.var 1 is plaintext Ljava/lang/String;
.var 2 is poly Ljava/lang/String;
;
; poly=plaintext<<key
;
	aload_1
	aload_0
	invokestatic	cypher/poly(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
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
; FSencypher[Iamount,Splaintext]{encypher=plaintext>>amount;}
;
;
; FSpoly[Skey,Splaintext]{poly=plaintext<<key;}
;
;
; FSanalysis[Stext]{analysis=text@;}
;
;
; print['\nEXECUTING: SAMPLE2\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\nEXECUTING: SAMPLE2\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; Splain
;
;
; plain='lets go san jose state university spartans'
;
	ldc	"lets go san jose state university spartans"
	putstatic	sample2/plain Ljava/lang/String;
;
; Scypher
;
;
; ScypherAnalysis
;
;
; Iamount
;
;
; amount=1024
;
	sipush	1024
	putstatic	sample2/amount I
;
; Ij
;
;
; j=2
;
	iconst_2
	putstatic	sample2/j I
;
; print['while loop:']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"while loop:"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n------------------------------------------------------------------\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n------------------------------------------------------------------\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; while[amount>10]{cypher=encypher[amount,plain];cypherAnalysis=analysis['cypher'];print['shift amount = '];print[amount];print['\n\n'];print['Cypher = '];print[cypher];print['\n\n'];print['Cypher String Analysis = '];print[cypherAnalysis];print['\n------------------------------------------------------------------\n'];amount=amountDIVj;}
;
L001:
	getstatic	sample2/amount I
	bipush	10
	if_icmpgt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
;
; cypher=encypher[amount,plain]
;
	getstatic	sample2/amount I
	getstatic	sample2/plain Ljava/lang/String;
	invokestatic	sample2/encypher(ILjava/lang/String;)Ljava/lang/String;
	putstatic	sample2/cypher Ljava/lang/String;
;
; cypherAnalysis=analysis['cypher']
;
	ldc	"cypher"
	invokestatic	sample2/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample2/cypheranalysis Ljava/lang/String;
;
; print['shift amount = ']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"shift amount = "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print[amount]
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	getstatic	sample2/amount I
	invokevirtual	java/io/PrintStream/print(I)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
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
	getstatic	sample2/cypher Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
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
	getstatic	sample2/cypheranalysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n------------------------------------------------------------------\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n------------------------------------------------------------------\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; amount=amountDIVj
;
	getstatic	sample2/amount I
	getstatic	sample2/j I
	idiv
	putstatic	sample2/amount I
	goto	L001
L002:
;
; Sresult
;
;
; result=cypher>>10
;
	getstatic	sample2/cypher Ljava/lang/String;
	bipush	10
	invokestatic	cypher/shift(Ljava/lang/String;I)Ljava/lang/String;
	putstatic	sample2/result Ljava/lang/String;
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
; if[plain==result]{Spolyalphabet;polyalphabet=poly['sjsu',plain];SpolyalphabetAnalysis;polyalphabetAnalysis=analysis[polyalphabet];print['Polyalphabet = '];print[polyalphabet];print['\n\n'];print['Polyalphabet String Analysis = '];print[polyalphabetAnalysis];print['\n\n'];print['decyphered!'];print['\n\n'];print['Plaintext = '];print[result];print['\n\n'];SplainTextAnalysis;plainTextAnalysis=analysis[result];print['Plaintext Analysis = '];print[plainTextAnalysis];print['\n\n'];}else{print['bad logic!'];}
;
	getstatic	sample2/plain Ljava/lang/String;
	getstatic	sample2/result Ljava/lang/String;
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
	goto	L006
L005:
;
; Spolyalphabet
;
;
; polyalphabet=poly['sjsu',plain]
;
	ldc	"sjsu"
	getstatic	sample2/plain Ljava/lang/String;
	invokestatic	sample2/poly(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample2/polyalphabet Ljava/lang/String;
;
; SpolyalphabetAnalysis
;
;
; polyalphabetAnalysis=analysis[polyalphabet]
;
	getstatic	sample2/polyalphabet Ljava/lang/String;
	invokestatic	sample2/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample2/polyalphabetanalysis Ljava/lang/String;
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
	getstatic	sample2/polyalphabet Ljava/lang/String;
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
	getstatic	sample2/polyalphabetanalysis Ljava/lang/String;
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; print['\n\n']
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
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
	getstatic	sample2/result Ljava/lang/String;
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
; plainTextAnalysis=analysis[result]
;
	getstatic	sample2/result Ljava/lang/String;
	invokestatic	sample2/analysis(Ljava/lang/String;)Ljava/lang/String;
	putstatic	sample2/plaintextanalysis Ljava/lang/String;
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
	getstatic	sample2/plaintextanalysis Ljava/lang/String;
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
.limit stack 12
.end method
