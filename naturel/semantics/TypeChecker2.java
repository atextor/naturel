package naturel.semantics;

import java.util.Map;

import naturel.generator.DepthVarManager;
import naturel.generator.SammleKlassenMethoden.ClassDef;
import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.Sichtbarkeit;
import naturel.generator.definitions.VariableDef;

public class TypeChecker2 extends DepthVarManager {
	
	private String messages = "";
	
	private void addMessage(String str) {
		messages = messages + str + "\n";
	}
	
	
	/**
	 * Übergeben des Objektes, das alle Klassen und Methoden enthält
	 * Überladen, damit hier gleich die ersten Checks gemacht werden
	 * @param alleKlassenMethoden
	 */
	public void setAlleKlassenMethoden(Map<String, ClassDef> alleKlassenMethoden) {
		super.setAlleKlassenMethoden(alleKlassenMethoden);
		// Jetzt alle Klassen testen - ob die Interfaces implementiert wurden, etc.
		for (ClassDef akt : alleKlassenMethoden.values()) {
			if (akt.isInterface && (akt.sichtbarkeit == Sichtbarkeit.PRIVATE)) {
				addMessage("Interface " + akt.name + " at " + akt.position + " is Private.");
				//TODO: Überschriebene Methoden - A) gleiche Struktur, b) public
			}
			
			// Super-Klasse vorhanden?
			if ((akt.superof != null) && (!akt.superof.equals(""))) {
				ClassDef sup = alleKlassenMethoden.get(akt.superof);
				if (sup == null) {
					addMessage("Superclass " + akt.superof + " not found. Class: " + akt.name + " at " + akt.position);
				} else if (sup.isInterface) {
					addMessage("Referred Superclass " + sup.name + " is not a Class. Class: " + akt.name + " at " + akt.position);
				}
			}
			
			// Interfaces testen
			if (akt.interfaces != null) {
				for (String interfs : akt.interfaces) {
					ClassDef interf = alleKlassenMethoden.get(interfs);
					if (interf == null) {
						addMessage("Interface " + interfs + " not found. Class: " + akt.name + " at " + akt.position);
					} else 	if (!interf.isInterface) {
						addMessage("Referred Interface " + interfs + " is not an Interface. Class: " + akt.name + " at " + akt.position);
					} else {
						for (MethodenDef im : interf.methods.values()) {
							MethodenDef am = akt.methods.get(im.name);
							if (am == null) {
								addMessage("Class doesn't implement Method " + im.name + " from Interface " + interfs + ". Class: " + akt.name + " at " + akt.position);
							} else if (!am.rettype.equals(im.rettype)) {
								addMessage("Method " + im.name + " " + im.position + ": Wrong type. Found: " + am.rettype + ", expected " + im.rettype + " from Interface " + interfs);
							} else {
								// Parameter testen: Die müssen da (und gleich) sein
	/*							if (im.params.size() != am.params.size()) {
									addMessage("Method " + im.name + " " + im.position + ": Parameter count of Parameters (" + am.params.size() + ") wrong. From Interface " + interfs + ": " + im.params.size() );
								} else {
									for (int x = 0; x < im.params.size(); x++) {
										VariableDef av = am.params.keySet().
									}
								}*/

								for (VariableDef iv : im.params.values()) {
									VariableDef av = am.params.get(iv.name);
									if (av == null) {
										addMessage("Method " + im.name + " " + im.position + ": Parameter " + iv.name + " missing. From Interface " + interfs);
									} else if (!av.typ.equals(iv.typ)) {
										addMessage("Method " + im.name + " " + im.position + ": Parameter " + iv.name + ": Wrong type. Found: " + av.typ + ", expected " + iv.typ + " from Interface " + interfs);
									} else if (av.paramposition != iv.paramposition) {
										addMessage("Method " + im.name + " " + im.position + ": Parameter " + iv.name + ": Wrong Position. Found: " + av.paramposition + ", expected " + iv.paramposition + " from Interface " + interfs);
									}
								}
								
								// Parameter testen: "aktuell" darf keine Parameter mehr haben. 
								for (VariableDef av : am.params.values()) {
									VariableDef iv = im.params.get(av.name);
									if (iv == null) {
										addMessage("Method " + am.name + " " + am.position + ": Parameter " + av.name + " isn't declared in method in Interface " + interfs);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public String getContent() {
		return messages;
	}	
}
