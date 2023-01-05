P sample2[parameters]
{

%encyphers plaintext by given amount%
F S encypher[I amount, S plaintext]
{
	encypher = plaintext >> amount;
};

%encyphers plaintext based on key%
F S poly[S key, S plaintext]
{
	poly = plaintext << key;
};

%performs string analysis%
F S analysis[S text]
{
	analysis = text @;
};

print['\nEXECUTING: SAMPLE2\n\n'];

S plain;
plain = 'lets go san jose state university spartans';
S cypher;
S cypherAnalysis;

I amount;
amount = 1024;

I j;
j = 2;

print['while loop:'];
print['\n'];
print['\n------------------------------------------------------------------\n'];
while[amount > 10]
{
	cypher = encypher[amount, plain];
	cypherAnalysis = analysis['cypher'];
	print['shift amount = '];
	print[amount];
	print['\n\n'];
	print['Cypher = '];
	print[cypher];
	print['\n\n'];
	print['Cypher String Analysis = '];
	print[cypherAnalysis]; %String analysis%
	print['\n------------------------------------------------------------------\n'];
	amount = amount DIV j;
};

S result;
result = cypher >> 10;

print['if statement:'];
print['\n\n'];
if[plain == result]
{
	S polyalphabet;
	polyalphabet = poly['sjsu', plain];
   	
	S polyalphabetAnalysis;
	polyalphabetAnalysis = analysis[polyalphabet];
	
	print['Polyalphabet = '];
	print[polyalphabet];
	print['\n\n'];
	
	print['Polyalphabet String Analysis = '];
	print[polyalphabetAnalysis]; %String analysis%
	print['\n\n'];

	print['decyphered!'];
	print['\n\n'];
	print['Plaintext = '];
	print[result];
	print['\n\n'];
	
	S plainTextAnalysis;
	plainTextAnalysis = analysis[result];

	print['Plaintext Analysis = '];
	print[plainTextAnalysis]; %String analysis%
	print['\n\n'];
}
else
{
	print['bad logic!'];
	print['\n\n'];
};

print['DONE!\n'];

}