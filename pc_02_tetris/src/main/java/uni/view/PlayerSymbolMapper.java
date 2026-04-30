package uni.view;

public class PlayerSymbolMapper {
    
    // Arreglo de diacríticos. El índice 0 es nulo para representar la letra base.
    private static final char[] DIACRITICS = {
        '\0',      // Nivel 0: Sin diacrítico
        '\u0308',  // Nivel 1: Diéresis (Ä)
        '\u0301',  // Nivel 2: Acento agudo (Á)
        '\u0302',  // Nivel 3: Acento circunflejo (Â)
        '\u0303',  // Nivel 4: Tilde (Ã)
        '\u0304',  // Nivel 5: Macrón superior (Ā)
        '\u0306',  // Nivel 6: Breve (Ă)
        '\u0307',  // Nivel 7: Punto superior (Ȧ)
        '\u030A',  // Nivel 8: Anillo superior (Å)
        '\u030C',  // Nivel 9: Carón o Hacec (Ǎ)
        '\u0332',  // Nivel 10: Subrayado (A̲)
        '\u0336',  // Nivel 11: Tachado largo (A̶)
        '\u0338'   // Nivel 12: Barra diagonal superpuesta (A̸)
    };

    /**
     * Convierte un ID de jugador (1, 2, 3...) en un símbolo visual.
     * Con 26 letras y 13 niveles (base + 12 diacríticos),
     * soporta hasta 338 jugadores únicos antes de usar números.
     */
    public static String getSymbolForId(int playerId) {
        int zeroBasedIndex = playerId - 1;
        char baseChar = (char) ('A' + (zeroBasedIndex % 26));
        int diacriticLevel = zeroBasedIndex / 26;

        StringBuilder symbol = new StringBuilder().append(baseChar);

        if (diacriticLevel < DIACRITICS.length) {
            // Solo añadimos el carácter si no es el nivel 0
            if (diacriticLevel > 0) {
                symbol.append(DIACRITICS[diacriticLevel]);
            }
        } else {
            // Failsafe visual para cuando superes los 338 clientes
            symbol.append(diacriticLevel); 
        }
        
        return symbol.toString();
    }
}