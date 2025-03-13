package pl.own.projects;

public enum TokenType {
    /**
     * Identyfikator – nazwa zmiennej, metody, klasy itp.
     * Może zawierać litery, cyfry (ale nie na początku), znak podkreślenia '_' i '$'.
     * Przykłady: a1, _var2, $count, Main
     */
    IDENTYFIKATOR,
    LICZBA_CALKOWITA,                   // tylko nieujemne np: 0, 123, 42
    LICZBA_CALKOWITA_SZESNASTKOWA,      // Przykłady: 0xFA, 0X1A, 0x10
    LICZBA_CALKOWITA_OKTALNA,           // Przykłady: 0123
    LICZBA_CALKOWITA_BINARNA,           // Przykłady: 0b1010, 0B110
    LICZBA_RZECZYWISTA,                 // Przykłady: 3.14, 1.0, .5, 1e10, 2.5f
    LITERA_BOOLEAN,                     // true lub false
    LITERA_NULL,                        // null

    // Słowa kluczowe
    SŁOWO_KLUCZOWE,

    // Operatory arytmetyczne
    OPERATOR_PLUS,              // +
    OPERATOR_MINUS,             // -
    OPERATOR_MNOŻENIA,          // *
    OPERATOR_DZIELENIA,         // /
    OPERATOR_MODULO,            // %
    OPERATOR_INKREMENTACJI,     // ++
    OPERATOR_DEKREMENTACJI,     // --

    // Operatory przypisania
    OPERATOR_PRZYPISANIA,                   // =
    OPERATOR_DODANIA_PRZYPISANIA,           // +=
    OPERATOR_ODJĘCIA_PRZYPISANIA,           // -=
    OPERATOR_MNOŻENIA_PRZYPISANIA,          // *=
    OPERATOR_DZIELENIA_PRZYPISANIA,         // /=
    OPERATOR_MODULO_PRZYPISANIA,            // %=
    OPERATOR_BITOWE_I_PRZYPISANIA,          // &=
    OPERATOR_BITOWE_LUB_PRZYPISANIA,        // |=
    OPERATOR_BITOWE_XOR_PRZYPISANIA,        // ^=
    OPERATOR_PRZESUNIĘCIA_W_LEWO_PRZYPISANIA, // <<=
    OPERATOR_PRZESUNIĘCIA_W_PRAWO_PRZYPISANIA,// >>=
    OPERATOR_PRZESUNIĘCIA_BEZ_ZNAKU_PRZYPISANIA, // >>>=

    // Operatory porównania
    OPERATOR_RÓWNOŚCI,         // ==
    OPERATOR_NIERÓWNOŚCI,      // !=
    OPERATOR_MNIŻSZE,          // <
    OPERATOR_WIĘKSZE,          // >
    OPERATOR_MNIŻSZE_LUB_RÓWNO, // <=
    OPERATOR_WIĘKSZE_LUB_RÓWNO,  // >=

    // Operatory logiczne
    OPERATOR_LOGICZNE_I,       // &&
    OPERATOR_LOGICZNE_LUB,     // ||
    OPERATOR_NEGACJI,          // !

    // Operatory bitowe
    OPERATOR_BITOWE_NEGACJI,   // ~
    OPERATOR_BITOWE_I,         // &
    OPERATOR_BITOWE_LUB,       // |
    OPERATOR_BITOWE_XOR,       // ^
    OPERATOR_PRZESUNIĘCIA_W_LEWO,  // <<
    OPERATOR_PRZESUNIĘCIA_W_PRAWO, // >>
    OPERATOR_PRZESUNIĘCIA_BEZ_ZNAKU, // >>>

    // Separatory (znaki interpunkcyjne)
    NAWIAS_OKRĄGŁY_OTWARCIE,   // (
    NAWIAS_OKRĄGŁY_ZAMKNIĘCIE,  // )
    NAWIAS_KLAMKOWY_OTWARCIE,   // {
    NAWIAS_KLAMKOWY_ZAMKNIĘCIE,  // }
    NAWIAS_KWADRATOWY_OTWARCIE, // [
    NAWIAS_KWADRATOWY_ZAMKNIĘCIE, // ]
    ŚREDNIK,                  // ;
    PRZECINEK,                // ,
    KROPKA,                   // .
    TRZY_KROPKI,              // ... (np. dla varargs)

    // Komentarze
    KOMENTARZ_JEDNOLINIOWY,    // // komentarz
    KOMENTARZ_WIELOLINIOWY,     // /* ... */
    KOMENTARZ_DOKUMENTACYJNY,  // /** ... */

    // Specjalny token oznaczający koniec pliku
    KONIEC_PLIKU
}