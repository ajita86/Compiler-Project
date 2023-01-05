P sampleSmall[parameters]
{

F S deshift[I amount, S cyphertext]
{
	deshift = cyphertext >> [26 - amount];
};

S plain;
plain = 'the quick red fox jumped over the lazy brown dog';
S cypher;
cypher = plain >> 2; %encyphers the string by shifting by the amount given%
S polyalphabet;
polyalphabet = plain << 'polyalphabet'; %encyphers the string based on the key given%

print[cypher];
print[polyalphabet];

S polyalphabetAnalysis;
polyalphabetAnalysis = polyalphabet @;
print[polyalphabetAnalysis]; %String analysis%

I i;
i = 0;

D j;
j = 1.1;

while[i < 10]
{
	i = i + 1;
	j = j * i;
	print[j];
};

S result;
result = deshift[2, cypher];

if[plain == result]
{
	S plainTextAnalysis;
   	plainTextAnalysis = result @;
	print[plainTextAnalysis]; %String analysis%
}
else
{
	print['bad logic!'];
};

}