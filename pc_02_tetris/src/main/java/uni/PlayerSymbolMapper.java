package uni;

public class PlayerSymbolMapper {
    
    /**
     * Convierte un ID de jugador (1, 2, 3...) en un símbolo visual.
     * 1-26: A-Z
     * 27-52: Ä-Z̈ (con diéresis)
     * 53-78: Á-Ź (con acento agudo)
     */
    public static String getSymbolForId(int playerId) {

        int zeroBasedIndex = playerId - 1;
        char baseChar = (char) ('A' + (zeroBasedIndex % 26));
        int diacriticLevel = zeroBasedIndex / 26;

        StringBuilder symbol = new StringBuilder().append(baseChar);

        switch (diacriticLevel) {
            case 0 -> {} // Sin diacrítico
            case 1 -> symbol.append('\u0308'); // Diéresis
            case 2 -> symbol.append('\u0301'); // Acento agudo
            case 3 -> symbol.append('\u0302'); // Acento circunflejo
            default -> symbol.append(diacriticLevel); // Failsafe para demasiados jugadores
        }
        
        // Retornamos el símbolo con espacios para que se vea bien en la consola
        return symbol.toString();
    }
}