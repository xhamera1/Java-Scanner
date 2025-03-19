package pl.own.projects;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyScanner {
    private String source;
    private int currentLinePosition;
    private int currentLineNumber;
    List<String> lines;
    FileReader fileReader;

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "for", "while", "if", "else", "class", "public", "private", "protected",
            "static", "void", "return", "new", "import", "package", "try", "catch", "finally",
            "switch", "case", "default"
    ));

    public MyScanner(String source) throws MyScannerException{
        this.source = source;
        this.fileReader = new FileReader(source);
        try {
            this.lines = fileReader.readLinesToList();
        } catch (IOException e) {
            throw new MyScannerException("Could not open the source file");
        }
        this.currentLinePosition = 0;
        this.currentLineNumber = 0;
    }

    public Token nextToken() throws MyScannerException {
        if (!hasNextToken()) {
            return null;
        }

        char currChar = getNextChar();

        if (Character.isDigit(currChar)) {
            return parseNumber(currChar);
        }
        else if (Character.isLetter(currChar) || currChar == '_' || currChar == '$') {
            return handleIdentifierOrKeyword(String.valueOf(currChar));
        }
        else if (currChar == '"') {
            return parseStringLiteral();
        }
        else {
            return parseOperatorOrSymbol(currChar);
        }
    }


    private Token parseNumber(char firstDigit) throws MyScannerException {
        if (firstDigit == '0') {
            return handleStartsWithZero();
        } else {
            return handleDecimalOrFloat(String.valueOf(firstDigit));
        }
    }

    private Token parseStringLiteral() throws MyScannerException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char currChar = getNextChar();
            if (currChar == '"') {
                break;
            } else {
                sb.append(currChar);
            }
        }
        return new Token(TokenType.NAZWA_WLASNA, "\"" + sb.toString() + "\"");
    }


    private Token parseOperatorOrSymbol(char currChar) throws MyScannerException {
        switch (currChar) {
            case '=':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_ROWNOSCI, "==");
                }
                return new Token(TokenType.OPERATOR_PRZYPISANIA, "=");

            case '+':
                if (checkNextChar() == '+') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_INKREMENTACJI, "++");
                } else if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_DODANIA_PRZYPISANIA, "+=");
                }
                return new Token(TokenType.OPERATOR_PLUS, "+");

            case '-':
                if (checkNextChar() == '-') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_DEKREMENTACJI, "--");
                } else if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_ODJECIA_PRZYPISANIA, "-=");
                }
                return new Token(TokenType.OPERATOR_MINUS, "-");

            case '*':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_MNOZENIA_PRZYPISANIA, "*=");
                } else if (checkNextChar() == '/') {
                    getNextChar();
                    return new Token(TokenType.KOMENTARZ_WIELOLINIOWY_LUB_DOKUMENTACYJNY_KONIEC, "*/");
                }
                return new Token(TokenType.OPERATOR_MNOZENIA, "*");

            case '/':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_DZIELENIA_PRZYPISANIA, "/=");
                } else if (checkNextChar() == '/') {
                    getNextChar();
                    matchPatternFromCurrentPosition(Pattern.compile(".*"));
                    return new Token(TokenType.KOMENTARZ_JEDNOLINIOWY, "//");
                } else if (checkNextChar() == '*') {
                    getNextChar();
                    if (checkNextChar() == '*') {
                        getNextChar();
                        return new Token(TokenType.KOMENTARZ_DOKUMENTACYJNY_POCZATEK, "/**");
                    }
                    skipMultiLineComment();
                    return new Token(TokenType.KOMENTARZ_WIELOLINIOWY_POCZATEK, "/*");
                }
                return new Token(TokenType.OPERATOR_DZIELENIA, "/");

            case '%':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_MODULO_PRZYPISANIA, "%=");
                }
                return new Token(TokenType.OPERATOR_MODULO, "%");

            case '&':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_BITOWE_I_PRZYPISANIA, "&=");
                }
                if (checkNextChar() == '&') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_LOGICZNE_I, "&&");
                }
                return new Token(TokenType.OPERATOR_BITOWE_I, "&");

            case '|':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_BITOWE_LUB_PRZYPISANIA, "|=");
                }
                if (checkNextChar() == '|') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_LOGICZNE_LUB, "||");
                }
                return new Token(TokenType.OPERATOR_BITOWE_LUB, "|");

            case '^':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_BITOWE_XOR_PRZYPISANIA, "^=");
                }
                return new Token(TokenType.OPERATOR_BITOWE_XOR, "^");

            case '!':
                if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_NIEROWNOSCI, "!=");
                }
                return new Token(TokenType.OPERATOR_NEGACJI, "!");

            case '~':
                return new Token(TokenType.OPERATOR_BITOWE_NEGACJI, "~");

            case '<':
                if (checkNextChar() == '<') {
                    if (checkNextChar() == '=') {
                        getNextChar();
                        getNextChar();
                        return new Token(TokenType.OPERATOR_PRZESUNIECIA_W_LEWO_PRZYPISANIA, "<<=");
                    }
                    getNextChar();
                    return new Token(TokenType.OPERATOR_PRZESUNIECIA_W_LEWO, "<<");
                } else if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_MNIEJSZE_LUB_ROWNE, "<=");
                }
                return new Token(TokenType.OPERATOR_MNIEJSZE, "<");

            case '>':
                if (checkNextChar() == '>') {
                    if (checkNextChar() == '=') {
                        getNextChar();
                        getNextChar();
                        return new Token(TokenType.OPERATOR_PRZESUNIECIA_W_PRAWO_PRZYPISANIA, ">>=");
                    }
                    getNextChar();
                    return new Token(TokenType.OPERATOR_PRZESUNIECIA_W_PRAWO, ">>");
                } else if (checkNextChar() == '=') {
                    getNextChar();
                    return new Token(TokenType.OPERATOR_WIEKSZE_LUB_ROWNE, ">=");
                }
                return new Token(TokenType.OPERATOR_WIEKSZE, ">");

            case '(':
                return new Token(TokenType.NAWIAS_OKRAGLY_OTWARCIE, "(");

            case ')':
                return new Token(TokenType.NAWIAS_OKRAGLY_ZAMKNIECIE, ")");

            case '{':
                return new Token(TokenType.NAWIAS_KLAMROWY_OTWARCIE, "{");

            case '}':
                return new Token(TokenType.NAWIAS_KLAMROWY_ZAMKNIECIE, "}");

            case '[':
                return new Token(TokenType.NAWIAS_KWADRATOWY_OTWARCIE, "[");

            case ']':
                return new Token(TokenType.NAWIAS_KWADRATOWY_ZAMKNIECIE, "]");

            case ';':
                return new Token(TokenType.SREDNIK, ";");

            case ',':
                return new Token(TokenType.PRZECINEK, ",");

            case '.':
                char nextChar = checkNextChar();
                if (nextChar == '.') {
                    getNextChar();
                    char nextNextChar = checkNextNextChar();
                    if (nextNextChar == '.') {
                        getNextChar();
                        return new Token(TokenType.TRZY_KROPKI, "...");
                    }
                }
                return new Token(TokenType.KROPKA, ".");

            default:
                throw new MyScannerException("Nieznany token rozpoczynajacy sie znakiem: " + currChar);
        }
    }

    private Token handleIdentifierOrKeyword(String s) throws MyScannerException{
        StringBuilder sb = new StringBuilder(s);
        Pattern restPattern = Pattern.compile("[0-9a-zA-Z_$]*");
        String rest = matchPatternFromCurrentPosition(restPattern);
        sb.append(rest);
        String word = sb.toString();
        if (word.equals("true") || word.equals("false") ){
            return new Token(TokenType.LITERA_BOOLEAN, word);
        }
        else if (KEYWORDS.contains(word)) {
            return new Token(TokenType.SLOWO_KLUCZOWE, word);
        }
        else if (word.equals("null")) {
            return new Token(TokenType.LITERA_NULL, word);
        }
        else return new Token(TokenType.IDENTYFIKATOR, word);



    }

    private Token handleStartsWithZero() throws MyScannerException {
        char next = checkNextChar();
        if (next == 'x' || next == 'X') {
            return handleHexNumber();
        }
        else if (next == 'b' || next == 'B') {
            return handleBinNumber();
        }
        else if (next == '.') {
            return handleFloatNumber("0");
        }
        else if (Character.isDigit(next)) {
            if (next >= '0' && next <= '7') {
               return handleOctalNumber();
            }
            else {
                throw new MyScannerException("Niepoprawna cyfra w liczbie osemkowej: 0" + next);
            }
        }
        else if (Character.isLetter(next)) {
            throw new MyScannerException("Niepoprawny literal liczbowy zaczynajacy sie od 0: 0" + next);
        }
        else {
            return new Token(TokenType.LICZBA_CALKOWITA, "0");
        }
    }

    private Token handleHexNumber() throws MyScannerException{
        char x = getNextChar();
        Pattern hexDigitsPattern = Pattern.compile("[0-9a-fA-F]+");
        String hexDigits = matchPatternFromCurrentPosition(hexDigitsPattern);
        if (hexDigits.isEmpty()) {
            throw new MyScannerException("Niepoprawny literal szesnastkowy");
        }
        else return new Token(TokenType.LICZBA_CALKOWITA_SZESNASTKOWA, "0" + x + hexDigits);
    }

    private Token handleBinNumber() throws  MyScannerException {
        char b = getNextChar();
        Pattern binDigitsPattern = Pattern.compile("[01]+");
        String binDigits = matchPatternFromCurrentPosition(binDigitsPattern);
        if (binDigits.isEmpty()) {
            throw new MyScannerException("Niepoprawny literal w zapisie binarnym");
        }
        else return new Token(TokenType.LICZBA_CALKOWITA_BINARNA, "0" + b + binDigits);
    }

    private Token handleOctalNumber() throws MyScannerException{
        Pattern octalDigitsPattern = Pattern.compile("[0-7]+");
        String octalDigits = matchPatternFromCurrentPosition(octalDigitsPattern);
        if (octalDigits.isEmpty()) {
            throw new MyScannerException("Niepoprawny literal w liczbie osomkowej");
        }
        return new Token(TokenType.LICZBA_CALKOWITA_OKTALNA, "0" + octalDigits);
    }

    private Token handleFloatNumber(String integerPart) throws MyScannerException {
        char dot = getNextChar();
        StringBuilder sb = new StringBuilder(integerPart);
        sb.append(dot);

        Pattern fractionalPattern = Pattern.compile("\\d*");
        String fractionalPart = matchPatternFromCurrentPosition(fractionalPattern);
        sb.append(fractionalPart);

        // obsluga czesci wykladniczej
        String remaining = getRemainingLine();
        if (!remaining.isEmpty() && (remaining.charAt(0) == 'e' || remaining.charAt(0) == 'E')) {
            char e = getNextChar();
            sb.append(e);
            remaining = getRemainingLine();
            if (!remaining.isEmpty() && (remaining.charAt(0) == '+' || remaining.charAt(0) == '-')) {
                char sign = getNextChar();
                sb.append(sign);
            }
            Pattern exponentPattern = Pattern.compile("\\d+");
            String exponentDigits = matchPatternFromCurrentPosition(exponentPattern);
            if (exponentDigits.isEmpty()) {
                throw new MyScannerException("Brak cyfr w czesci wykladniczej literalu zmiennoprzecinkowego");
            }
            sb.append(exponentDigits);
        }
        return new Token(TokenType.LICZBA_RZECZYWISTA, sb.toString());
    }


    private String matchPatternFromCurrentPosition(Pattern pattern) throws MyScannerException {
        String remaining = getRemainingLine();
        Matcher matcher = pattern.matcher(remaining);
        if (matcher.lookingAt()) {
            String match = matcher.group();
            currentLinePosition += match.length();
            return match;
        }
        return "";
    }

    private String getRemainingLine() throws MyScannerException {
        if (currentLineNumber >= lines.size()) {
            throw new MyScannerException("Brak kolejnych linii");
        }
        String line = lines.get(currentLineNumber);
        if (currentLinePosition >= line.length()) {
            return "";
        }
        return line.substring(currentLinePosition);
    }


    private Token handleDecimalOrFloat(String integerPart) throws MyScannerException {
        StringBuilder sb = new StringBuilder(integerPart);
        Pattern digitsPattern = Pattern.compile("\\d*");
        String restDigits = matchPatternFromCurrentPosition(digitsPattern);
        sb.append(restDigits);
        if (checkNextChar() == '.') {
            return handleFloatNumber(sb.toString());
        }
        return new Token(TokenType.LICZBA_CALKOWITA, sb.toString());
    }


    private char getNextChar() throws MyScannerException{
        if (currentLineNumber >= this.lines.size()) {
            throw new MyScannerException("No more characters in file");
        }
        String line = lines.get(currentLineNumber);
        if (currentLinePosition >= line.length()) {
            currentLineNumber++;
            currentLinePosition = 0;
            return '\n';
        }
        char ch = line.charAt(currentLinePosition);
        currentLinePosition++;
        return ch;
    }

    private char checkNextChar() throws MyScannerException{
        if (currentLineNumber >= this.lines.size()) {
            throw new MyScannerException("No more characters in file");
        }
        String line = lines.get(currentLineNumber);
        if (currentLinePosition >= line.length()) {
            return '\n';
        }
        return line.charAt(currentLinePosition);
    }

    private char checkNextNextChar() throws MyScannerException {
        if (currentLineNumber >= this.lines.size()) {
            throw new MyScannerException("No more characters in file");
        }
        String line = lines.get(currentLineNumber);
        if (currentLinePosition + 1 >= line.length()) {
            return '\n';
        }
        return line.charAt(currentLinePosition + 1);
    }

    public boolean hasNextToken(){
        while (currentLineNumber < lines.size()) {
            String currentLine = lines.get(currentLineNumber);
            if (currentLinePosition >= currentLine.length()) {
                currentLineNumber++;
                currentLinePosition = 0;
                continue;
            }
            char next = currentLine.charAt(currentLinePosition);
            if (Character.isWhitespace(next)) {
                currentLinePosition++;
                continue;
            }
            return true;
        }
        return false;
    }

    private void skipMultiLineComment() throws MyScannerException {
        while (true) {
            char currChar = getNextChar();
            if (currChar == '*' && checkNextChar() == '/') {
                getNextChar();
                break;
            }
        }
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCurrentLinePosition() {
        return currentLinePosition;
    }

    public void setCurrentLinePosition(int currentLinePosition) {
        this.currentLinePosition = currentLinePosition;
    }

    public int getCurrentLineNumber() {
        return currentLineNumber;
    }

    public void setCurrentLineNumber(int currentLineNumber) {
        this.currentLineNumber = currentLineNumber;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public FileReader getFileReader() {
        return fileReader;
    }

    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }
}