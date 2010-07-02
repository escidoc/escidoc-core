1. Verzeichnisse und darin enthaltene Dateien

Verzeichnis docbook
Reference.xml 
Eine Dokumentation im Docbook Format erzeugt aus JavaDoc mit DbDoclet. Eine solche Datei wird als Eingabe f�r das xsl dienen. Vollst�ndig dokumentiert ist hier nur die Methode create.

organizational-unit.xml
Enth�lt Zusatz Informationen, um 
  i. die REST Dokumentation zu erstellen (die notwendige Uri und woher welcher Parameter genommen wird)
 ii. zus�tzliche Info dar�ber, ob diese Methode �berhaupt dokumentiert wird (visible Attribut im <documentation visible="false|true"/> Element, ist das Attribut nicht vorhanden, wird angenommen, das es den Wert true hat) und 
iii. in welcher Interface Dokumentation die Methode enthalten sein soll (available Attribut im <documentation available="both|rest|soap"/> Element, hier ist both default wenn das Attribute nicht gefunden wird)

RestOrganizationalUnitApi.xml und SoapOrganizationalUnitApi.xml
Diese Dateien enthalten jeweils ein Kapitel mit der jeweiligen Dokumentation aller Methoden. Diese sollen vom xsl aus den 2 Eingabe Dateien erzeugt werden.

Verzeichnis example
Hier habe ich aus den Dateien im docbook Verzeichnis die relevanten Teile f�r die create Methode zusammengestellt. Das ist vielleicht ein bisschen �bersichtlicher.
