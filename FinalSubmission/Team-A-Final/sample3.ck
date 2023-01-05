P sample3[parameters]
{

F S cypher[I amount, S plaintext]
{
    S result;
    result = plaintext >> amount;    %encryption%

	S original;
	original = result >> [26 - amount];     %decryption%
	
	cypher = 'encrypted and decrypted!';
};

print['\nEXECUTING: SAMPLE3\n\n'];

S plain;
plain = 'lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam';

S funct;
I shift;
shift = 14;
funct = cypher[shift, plain];
print[funct];
print['\n\n'];

S analysis;

print['if statement:'];
print['\n\n'];
if[funct == 'encrypted and decrypted!']
{
	S encryption;
	encryption = plain >> shift;
	
	analysis = encryption @;
	print['Encryption String analysis = '];
	print[analysis];
	print['\n\n'];	
	
	S polyalphabet;
	polyalphabet = encryption << 'poly';
	print['Encryption Polyalphabet = '];
	print[polyalphabet];
	print['\n\n'];
}
else
{
	analysis = plain @;
	print['Plaintext String analysis = '];
	print[analysis];
	print['\n\n'];
};

print['DONE!\n'];

}