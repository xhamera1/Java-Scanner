package pl.own.projects;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            if (currChar == '0') {
                return handleStartsWithZero();
            } else {
                return handleDecimalOrFloat(String.valueOf(currChar));
            }
        }
        else if(Character.isLetter(currChar) || currChar == '_' || currChar == '$') {
            return handleIdentifierOrKeyword(String.valueOf(currChar));
        }
        throw new MyScannerException("Nieznany token rozpoczynajacy sie znakiem: " + currChar);
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