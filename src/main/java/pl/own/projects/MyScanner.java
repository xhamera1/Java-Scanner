package pl.own.projects;

import java.io.IOException;
import java.util.List;

public class MyScanner {
    private String source;
    private int currentPosition;
    private int currentLine;
    private int currentColumn;
    List<String> lines;
    FileReader fileReader;

    public MyScanner(String source) throws MyScannerException{
        this.source = source;
        this.fileReader = new FileReader(source);
        try {
            this.lines = fileReader.readLinesToList();
        } catch (IOException e) {
            throw new MyScannerException("Could not open the source file");
        }
        this.currentPosition = 0;
        this.currentLine = 0;
        this.currentColumn = 0;
    }

    public Token nextToken() throws MyScannerException {
        return null;
    }


}