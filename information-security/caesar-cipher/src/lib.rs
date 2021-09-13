const ALPHABET: &str = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

fn to_index(ch: char) -> usize {
    ALPHABET
        .chars()
        .position(|x| x == ch)
        .unwrap_or_else(|| panic!("There is no char '{}' (#{}) in alphabet", ch, ch as u32))
}

fn from_index(index: usize) -> char {
    ALPHABET
        .chars()
        .nth(index)
        .unwrap_or_else(|| panic!("Invalid index '{}'", index))
}

fn check_alphabet(input: &str) {
    input.chars().for_each(|ch| {
        to_index(ch);
    });
}

pub struct CaesarSolver {
    letter_mapper: Vec<char>,
}

impl CaesarSolver {
    pub fn new(keyword: String, shift: usize) -> Self {
        let alphabet_size = ALPHABET.chars().count();
        let keyword = keyword.to_uppercase();
        
        check_alphabet(&keyword);

        // Example: keyword = KEYWORD, shift = 3
        let letter_mapper: Vec<char> = ALPHABET
            .chars()                                // ABCDEF...VWXYZ
            .filter(|&ch| keyword.find(ch) == None) // ABCFGH...UVXYZ (without letters from KEYWORD)
            .chain(keyword.chars())                 // ABCFGH...UVXYZKEYWORD
            .cycle()                                // ABCFGH...UVXYZKEYWORDABCFGH...
            .skip(shift % alphabet_size)            // FGHIJL...UVXYZKEYWORDABCFGH...
            .take(alphabet_size)                    // FGHIJL...UVXYZKEYWORDABC
            .collect();

        CaesarSolver { letter_mapper }
    }

    pub fn encrypt(&self, input: String) -> String {
        let input = input.to_uppercase();
        check_alphabet(&input);

        input
            .chars()
            .map(|ch| self.letter_mapper[to_index(ch)])
            .collect()
    }

    pub fn decrypt(&self, input: String) -> String {
        let input = input.to_uppercase();
        check_alphabet(&input);

        input
            .chars()
            .map(|ch| from_index(self.letter_mapper.iter().position(|&x| x == ch).unwrap()))
            .collect()
    }
}
