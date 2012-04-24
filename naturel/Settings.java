package naturel;

import java.io.File;

public class Settings {
	private enum Switch {
		ILLEGAL, h, help, debug, sigsegv, version, conly, compiler, prettyprint, v, c, o;
		public static Switch toSwitch(String s) {
			try {
				return valueOf(s);
			} catch (Exception e) {
				return ILLEGAL;
			}
		}
	}
	
	// Settings
	private Boolean cleanCode = true;
	private Boolean sigsegv = false;
	private Boolean conly = false;
	private Boolean prettyprint = false;
	private Boolean verbose = false;
	private String compiler = "gcc -g -Wall -pedantic -ansi";
	private String compilerSigsegv = "gcc -g -Wall -pedantic -DHANDLE_SIGSEGV -rdynamic";
	private String progName = "a.out";
	private File naturelfile = null;

	public Settings(String [] args) {
		int i = 0;
		while (i < args.length) {
			switch(Switch.toSwitch(args[i].substring(1))) {
				case h:
				case help:
					printHelp();
					break;
				case debug:
					cleanCode = false;
					break;
				case sigsegv:
					sigsegv = true;
					break;
				case version:
					printVersion();
					System.exit(0);
					break;
				case conly:
					conly = true;
					break;
				case compiler:
					i++;
					check(i, args.length);					
					compiler = args[i];
					break;
				case prettyprint:
					prettyprint = true;
					break;
				case v:
					verbose = true;
					break;
				case c:
					progName = null;
					break;
				case o:
					i++;
					check(i, args.length);
					progName= args[i];
					break;
				default:
					File f = new File(args[i]);
					if (f.canRead()) {
						naturelfile = f;
						return;
					} else {
						System.out.println("Illegal argument");
						System.exit(0);
					}
					break;
			}
			i++;
		}
		
		if (naturelfile == null) {
			System.out.println("Missing file argument");
			System.exit(0);
		}
	}
		
	private void check(int i, int len) {
		if (i == len) {
			System.out.print("Missing or illegal argument");
			System.exit(0);
		}
	}
	
	private void printVersion() {
		System.out.println("Naturel Version " + Main.VERSION + ", Feb. 2008");
		System.out.println("R. Erdt, A. Textor");
    }

	private void printHelp() {
		StringBuffer out = new StringBuffer();
	    printVersion();
	    
	    out.append("\nUsage: java naturel.Main [-h|-help|-version][-debug][-c][-o name]\n"
	    		+ "  [-sigsegv][-conly][-compiler name][-prettyprint][-v] [file]\n");
	    out.append(" -h | -help: Display this message and exit\n");
	    out.append(" -debug    : Enable debug messages in the generated code. Default: " + !cleanCode + "\n");
	    out.append(" -c        : Compile, but don't link.\n");
	    out.append(" -o name   : Set name of compiled program. Default: " + progName + ".\n");
	    out.append(" -sigsegv  : Enable internal SIGSEGV handler in generated program. Default: " + sigsegv + "\n");
	    out.append(" -conly    : Generate C-code, but don't compile it. Default: " + conly + "\n");
	    out.append(" -compiler name: Set the C-compiler to use including parameters.\n"
	    		+ "      Default for use with SIGSEGV-Handler: " + compilerSigsegv + "\n"
	    		+ "      Default for use without SIGSEGV-Handler: " + compiler + "\n");
	    out.append(" -prettyprint: Prettyprint the code and exit. Default: " + prettyprint + "\n");
	    out.append(" -v        : Be verbose: Print AST while generating code. Default: " + verbose + "\n");
	    out.append(" -version  : Print compiler version and exit\n");
	    out.append("\nMake sure that Core.h and Core.c exist in the same directory as the source file.\n");
	    
	    System.out.println(out.toString());
	    System.exit(0);
    }

	public Boolean getCleanCode() {
		return cleanCode;
	}
	
	public Boolean getSigsegv() {
		return sigsegv;
	}

	public File getNaturelfile() {
    	return naturelfile;
    }

	public Boolean getPrettyprint() {
    	return prettyprint;
    }

	public String getCompiler() {
    	return compiler;
    }

	public String getCompilerSigsegv() {
    	return compilerSigsegv;
    }

	public Boolean getConly() {
    	return conly;
    }

	public Boolean getVerbose() {
    	return verbose;
    }
	
	public String getPath() {
		return naturelfile.getParent() == null ? "" : naturelfile.getParent() + File.separator;
	}

	public String getProgName() {
    	return progName;
    }
	
}
