package naturel.generator.definitions;

public class VariableDef {
	public VariableDef(){}
	public String name = "";
	/*
	 * Hierarchie:
	 *    Da die Variablen Block-Lokal sein können, wird hier der Block mitgezählt,
	 *    in den gerade abgestiegen wurde. (bei jeden Block werden alle vorhanden 
	 *    variablen.hierarchy += 1, bei Ende-Block -= 1. Sobald eine Variable -1 erreicht,
	 *    wird diese aus den Speicher gelöscht.  
	 */
	public int hierarchy = 0;
	public String typ;
	// Name, den die Variable im C-Code haben wird
	public String cCodeName = "";
	public String className = null;
	public String pos = "";
	public boolean statisch = false;
	public boolean parameter = false;
	// Wenn das ein Parameter ist, die Position des Parameters
	public int paramposition = -1;
}
