package naturel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import naturel.generator.ASTAddThis;
import naturel.generator.ASTCondKorr;
import naturel.generator.ASTDecKorr;
import naturel.generator.ASTDefaultConstructor;
import naturel.generator.ASTDotExpr;
import naturel.generator.ASTDotListKorr;
import naturel.generator.ASTLiterals;
import naturel.generator.ASTOperators;
import naturel.generator.ASTVarCollect;
import naturel.generator.GenCode;
import naturel.generator.GenHeader;
import naturel.generator.SammleKlassenMethoden;
import naturel.lexer.Lexer;
import naturel.lexer.LexerException;
import naturel.node.Start;
import naturel.parser.Parser;
import naturel.parser.ParserException;
import naturel.semantics.TypeChecker;
import naturel.semantics.TypeChecker2;

public class Main {
	public static final String VERSION = "1.0.0";
	public static Settings settings = null;
	
	private static void prettyPrint(Start start) throws IOException, ParserException, LexerException {
		PrettyPrinter pp = new PrettyPrinter();
		pp.caseStart(start);
		System.out.println(pp);
		System.exit(0);
	}
	
    /**
     * Main-Funktion
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
    	// Parse commandline
    	settings = new Settings(args);
    	String path = settings.getPath();
    	
    	try {
			//ASTPrinter astPrinter = new ASTPrinter();
			ASTPrinter2 astPrinter2 = new ASTPrinter2();

			Lexer lex = new Lexer(new PushbackReader(new FileReader(settings.getNaturelfile())));
			Parser par = new Parser(lex);
	    	Start start = par.parse();
	    	
	    	if (settings.getPrettyprint()) {
	    		prettyPrint(start);
	    	}

	    	// Default-Konstruktoren anlegen wo nötig.
			ASTDefaultConstructor defCon = new ASTDefaultConstructor();
			defCon.caseStart(start);

			// Methoden sammeln
			SammleKlassenMethoden skm = new SammleKlassenMethoden();
			skm.caseStart(start);

			// num_new(2) + num_new(3) -> num_new(2).addNum(num_new(3))
			ASTOperators operators = new ASTOperators();
			operators.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			operators.caseStart(start);
			
			// 3 -> num_new(3)   "test" -> str_new("test");
			ASTLiterals strings = new ASTLiterals();
			strings.caseStart(start);

			TypeChecker tc = new TypeChecker();
			tc.caseStart(start);
			String tcOut = tc.getContent();
			if (tcOut.length() > 0) {
				System.out.println("The following errors occured during semantic analysis:");
				System.out.println(tcOut);
				System.exit(0);
			}
			
			TypeChecker2 tc2 = new TypeChecker2();
			tc2.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			tc2.caseStart(start);
			String tcOut2 = tc2.getContent();
			if (tcOut2.length() > 0) {
				System.out.println("The following errors occured during semantic analysis:");
				System.out.println(tcOut2);
				System.exit(0);
			}
			
			//astPrinter.caseStart(start);
			//System.out.println(astPrinter);
			if (settings.getVerbose()) {
				System.out.println("-----------------------------------------------------------------");
			}
			
			// AST Überarbeiten
			ASTDotExpr handleDots = new ASTDotExpr();
			handleDots.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			//Zweimal starten ist RICHTIG!
			handleDots.caseStart(start);
			handleDots.caseStart(start);

			ASTCondKorr condKorr = new ASTCondKorr();
			condKorr.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			condKorr.caseStart(start);
			
			if (settings.getVerbose()) {
				astPrinter2.caseStart(start);
				System.out.println(astPrinter2);
			}
			
			ASTAddThis addthis = new ASTAddThis();
			addthis.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			addthis.caseStart(start);
			
			if (settings.getVerbose()) {
				System.out.println("-----------------------------------------------------------------");
			}
			ASTDotListKorr dotListKorr = new ASTDotListKorr();
			dotListKorr.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			dotListKorr.caseStart(start);

			// a:num:=x; -> a:num; a:=x;
			ASTDecKorr deckKorr = new ASTDecKorr();
			deckKorr.caseStart(start);
			
			// Header mit den neuen Klassen neu erzeugen
			GenHeader header = new GenHeader();
			header.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			header.caseStart(start);
			
			if (settings.getVerbose()) {
				System.out.println("-----------------------------------------------------------------");
			}
			
			deckKorr.caseStart(start);
			
			// Variablendeklarationen an den Start der Methoden schieben
			ASTVarCollect varCollect = new ASTVarCollect();
			varCollect.caseStart(start);
			

			if (settings.getVerbose()) {
				System.out.println("-----------------------------------------------------------------");
				astPrinter2.caseStart(start);
				System.out.println(astPrinter2);
			}

			GenCode code = new GenCode();
			code.setAlleKlassenMethoden(skm.getAlleKlassenMethoden());
			code.starte(start, skm.getMainClassName(), skm.getMainClassName() + ".h");
			
			// Write the code files
			final String codeFile = path + skm.getMainClassName() + ".c";
			final String headerFile = path + skm.getMainClassName() + ".h"; 

			if (settings.getVerbose()) {
				System.out.println("Writing " + headerFile);
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(headerFile));
			out.write(header.getContent().toString());
			out.close();
			
			if (settings.getVerbose()) {
				System.out.println("Writing " + codeFile);
			}
			out = new BufferedWriter(new FileWriter(codeFile));
			out.write(code.getContent().toString());
			out.close();
			
			// Compile code if necessary
			if (!settings.getConly()) {
				String command = settings.getSigsegv() ? settings.getCompilerSigsegv() : settings.getCompiler();
				command += " -I. -I" + path;
				if (settings.getProgName() == null) {
					command += " -c";
				} else {
					command += " -o " + settings.getProgName() + " " + path + "Core.c";
				}
				command +=  " " + codeFile;
				
				if (settings.getVerbose()) {
					System.out.println(command);
				}
	
				Process p = Runtime.getRuntime().exec(command);
	            BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	            String s = null;
	            while ((s = stdErr.readLine()) != null) {
	                System.out.println(s);
	            }
				p.waitFor();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
