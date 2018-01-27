#!/usr/bin/perl -w

use strict;
use warnings;

use Getopt::Long;

my $gradleData = getDataFromGradle();

my $SRG_PATH = $ENV{HOME} . '/.gradle/caches/minecraft/de/oceanlabs/mcp/mcp_snapshot/' . $gradleData->{'snapshot'} . '/' . $gradleData->{'minecraft.version'} . '/srgs/mcp-srg.srg';

my $class;
my $field;
my $method;
GetOptions(
	"class=s" => \$class,
	"field=s" => \$field,
	"method=s" => \$method,
);

if(!defined($class) || (!defined($field) && !defined($method))) {
	printf("Specify a --class and either a --field or a --method!\n");
	exit 1;
}

my $genData = {};
open(my $fh, '<', $SRG_PATH) or die("Can not open file " . $SRG_PATH . " for reading\n" . $!);
while(my $line = <$fh>) {
	chomp($line);
	if($line =~ m/^CL: (.*?) (.*?)\r$/) {
		my $mcp = $1;
		my $srg = $2;
	} elsif($line =~ m/^FD: (.*?) (.*?)\r$/) {
		my $mcp = $1;
		my $srg = $2;

		my @packages = split('/', $mcp);
		my $mcpName = pop @packages;
		my $srgName = pop(@{[split('/', $srg)]});
		my $className = join('.', @packages);
		if(!defined($genData->{$className})) {
			$genData->{$className} = { fields => [], methods => [] };
		}

		push @{$genData->{$className}->{'fields'}}, { mcp => $mcpName, srg => $srgName };
	} elsif($line =~ m/^MD: (.*?) \((.*?)\)(.*?) (.*?) \((.*?)\)(.*?)\r$/) {
		my $mcp = $1;
		my $mcpParam = $2;
		my $mcpReturn = $3;
		my $srg = $4;
		my $srgParam = $5;
		my $srgReturn = $6;

		$mcpParam =~ s/L.*?;/X/g;
		$mcpParam =~ s/\[//g;

		my @packages = split('/', $mcp);
		my $mcpName = pop @packages;
		my $srgName = pop(@{[split('/', $srg)]});
		my $className = join('.', @packages);
		if(!defined($genData->{$className})) {
			$genData->{$className} = { fields => [], methods => [] };
		}

		my $paramCount = length($mcpParam);

		push @{$genData->{$className}->{'methods'}}, { mcp => $mcpName, srg => $srgName, params => $paramCount };
	}
}

close($fh);

if(!defined($genData->{$class})) {
	printf "Class not found :(\n";
	exit 1;
}

my $classData = $genData->{$class};
if(defined($field)) {
	if(!defined($classData->{'fields'})) {
		printf("Class %s has no fields!?\n");
		exit 2;
	}

	foreach my $rawfield (@{$classData->{'fields'}}) {
		if($rawfield->{'mcp'} eq $field) {
			printf "%s\n", $rawfield->{'srg'};
			exit 0;
		}

		if($rawfield->{'srg'} eq $field) {
			printf "%s\n", $rawfield->{'mcp'};
			exit 0;
		}
	}

	printf("Field not found :(\n");
	exit 3;
}

if(defined($method)) {
	if(!defined($classData->{'methods'})) {
		printf("Class %s has no methods!?\n");
		exit 2;
	}

	foreach my $rawmethod (@{$classData->{'methods'}}) {
		if($rawmethod->{'mcp'} eq $method) {
			printf "%s\n", $rawmethod->{'srg'};
			exit 0;
		}

		if($rawmethod->{'srg'} eq $method) {
			printf "%s\n", $rawmethod->{'mcp'};
			exit 0;
		}
	}

	printf("Method not found :(\n");
	exit 3;
}



sub getDataFromGradle {
	my $result = {};
	open(my $fh, '<', 'build.gradle');
	while(my $line = <$fh>) {
		chomp($line);
		if($line =~ m/\s+mappings = "snapshot_(.*?)"/) {
			$result->{'snapshot'} = $1;
		}

		if($line =~ m/\s+version = "(.*?)-/ && !defined($result->{'minecraft.version'})) {
			$result->{'minecraft.version'} = $1;
		}
	}
	close($fh);

	# Hack around the fact that the minecraft version is read from build.properties
	# and I don't want to waste further time on this for now.
	$result->{'minecraft.version'} = '1.12.2';

	return $result;
}
