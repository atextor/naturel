Naturel Version 1.0.0 - Februar 2008
Andreas Textor und Ralph Erdt.
====================================

Note: The detailed documentation and some parts of this README are available in German only, sorry!

Introduction
------------

turel (not to confuse with (http://en.wikipedia.org/wiki/NATURAL "NATURAL") is a programming language
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

Naturel's grammar was transformed into a parser using the parser generator (http://sablecc.org/ "SableCC").
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
		doku.pdf                  - Dokumentation im PDF-Format
		doku.xml                  - Dokumentation-Quelldatei
	
	grammatik/
			grammatik-naturel.txt - Grammatik der Sprache im SableCC-Format
			Core.h, Core.c        - Standardbibliothek
			*.naturel             - Beispiel- und Testprogramme
			mkProjekt.sh          - Generiert aus der Grammatik den Parser
	
		naturel/
			Quellcode und Klassendateien
	
	demo-bin/
		fibonacci    \
		helloworld    |_____ Vorkompilierte Testprogramme
		punkt         |
		zahlenraten  /

Hinweis: Dieses Verzeichnis ist auch ein Eclipse-Projekt-Verzeichnis
und kann daher in Eclipse importiert werden.

Quickstart
----------

# Hilfe:
java naturel.Main -h

# Kompilieren:
java naturel.Main -o zahlenraten grammatik/zahlenraten.naturel

# Parser bauen:
cd grammatik
./mkProjekt.sh

