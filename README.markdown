Naturel Version 1.0.0 
=====================
February 2008 - Andreas Textor und Ralph Erdt.

Note: The detailed documentation is available in German only, sorry!

Introduction
------------

Naturel (not to confuse with [NATURAL](http://en.wikipedia.org/wiki/NATURAL)) is a programming language
that was developed by Ralph Erdt and Andreas Textor for the Compiler Construction class at the
University of Applied Sciences Wiesbaden in winter semester 07/08.
Naturel stands for "Naturel is the Andreas Textor Und Ralph Erdt Language" ("und" is the german word for "and").

We wrote a minimal standard library for Naturel and a compiler in Java that produces C code. The language
is object-oriented and its syntax is inspired by C, Java, Pascal and UML. Many keywords that are not
necessary for the semantics like class were left out and other keywords were replaced by symbols, like +
instead of public. Comments are written like in Java and C++ using // and /* ... */. Methods and attributes
can have the visibilities public, private and protected that are expressed using the UML-Modifiers +, - and #.
A class variable uses a double modifier character instead of the keyword static. Overwriting of methods is
allowed in Naturel, overloading is not allowed.

	+a:num;  // public
	-b:num;  // private
	#c:num;  // protected
	++d:num; // public static
	--e:num; // private static
	##f:num; // protected static

Naturel's grammar was transformed into a parser using the parser generator [SableCC](http://sablecc.org/).
The complete documentation, sample programs and source code of the compiler are contained in this directory.
The following block shows a short sample program that can be compiled using the compiler. num and str are
the built-in data types for integers and strings, the static new method is the constructor of the class.

	+Point {
		-x:num;
		-y:num;
	
		+getX():num { return(x); }
		+getY():num { return(y); }
		++new(px:num, py:num) { x := px; y := py; }
	
		+addPoint(p:Point) {
			x := x + p.getX();
			y := y + p.getY();
		}
	
		+tostr():str {
			return("[" + x.tostr() + "," + y.tostr() + "]");
		}
	
		++main(args:str[]):num {
			p1:Point := Point.new(1, 2);
			p2:Point := Point.new(3, 4);
			out("Point 1: " + p1.tostr() + "\n");
			out("Point 2: " + p2.tostr() + "\n");
			p1.addPoint(p2);
			out("Point 1: " + p1.tostr() + "\n");
			return(0);
		}
	}

Note: Since the whole language including idea, grammar and compiler were created during a
single semester, not all features are implemented in the compiler, especially a Garbage
Collector is missing. Thus, Naturel should not be used in productive environments.


Contents
--------

	dokumentation/
		doku.pdf                  - Documentation in PDF format
		doku.xml                  - Documentation source file
	
	grammatik/
			grammatik-naturel.txt - Grammar of the language in SableCC format
			Core.h, Core.c        - Standard library
			*.naturel             - Sample and test programs
			mkProjekt.sh          - Generates the parser from the grammar
	
		naturel/
			Source code and class files
	
	demo-bin/
		fibonacci    \
		helloworld    |_____ Precompiled test programs
		punkt         |
		zahlenraten  /

Note: This directory is also an Eclipse project.

Quickstart
----------

Help:

	java naturel.Main -h

Compile:

	java naturel.Main -o zahlenraten grammatik/zahlenraten.naturel

Generate parser:

	cd grammatik
	./mkProjekt.sh

