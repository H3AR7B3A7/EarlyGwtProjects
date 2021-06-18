package be.steven.d.dog.shared;

public class FieldVerifier {

    public static boolean isInvalidTickerSymbol(String symbol) {
        return !symbol.matches("^[0-9A-Z.]{1,10}$");
    }
}