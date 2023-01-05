P sample1[parameters]
{

F S deshift[I amount, S cyphertext]
{
	deshift = cyphertext >> [26 - amount];
};

print['\nEXECUTING: SAMPLE1\n\n'];

S plain;
plain = 'the quick red fox jumped over the lazy brown dog';
S cypher;
cypher = plain >> 2; %encyphers the string by shifting by the amount given%
S polyalphabet;
polyalphabet = plain << 'polyalphabet'; %encyphers the string based on the key given%

print['Cypher = '];
print[cypher];
print['\n\n'];
print['Polyalphabet = '];
print[polyalphabet];
print['\n\n'];

S cypherAnalysis;
cypherAnalysis = cypher @;
S polyalphabetAnalysis;
polyalphabetAnalysis = polyalphabet @;

print['Cypher String Analysis = '];
print[cypherAnalysis]; %String analysis%
print['\n\n'];
print['Polyalphabet String Analysis = '];
print[polyalphabetAnalysis]; %String analysis%
print['\n\n'];

I i;
i = 0;

D j;
j = 1.1;

print['while loop:'];
print['\n\n'];
while[i < 10]
{
	i = i + 1;
	j = j * i;
	print[i];
	print['\n'];
	print[j];
	print['\n'];
};

print['\n'];

S result;
result = deshift[2, cypher];

print['if statement:'];
print['\n\n'];
if[plain == result]
{
	print['decyphered!'];
	print['\n\n'];
	print['Plaintext = '];
	print[result];
	print['\n\n'];
	
	S plainTextAnalysis;
   	plainTextAnalysis = result @;

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