
#[macro_use]
extern crate lazy_static;
mod alphabet_map;

use alphabet_map::AlphabetMap;

const ALPHABET: &str = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

lazy_static! {
    static ref ALPHABET_MAP: AlphabetMap = Default::default();
}

fn alphabetic_num(ch: char) -> usize {
    ALPHABET_MAP.to_idx(ch)
}

fn from_alphabetic_num(index: usize) -> char {
    ALPHABET_MAP.by_idx(index)
}

fn check_alphabet(input: &str) -> Result<&str, char> {
    input.chars()
        .find(|ch| !ALPHABET_MAP.contains_char(*ch))
        .map_or(Ok(input), Err)
}

fn panic_on_bad_char<Out>(ch: char) -> Out {
    panic!("Character {} is not found in alphabet: [{}]", ch, ALPHABET)
}

pub struct CaesarSolver {
    letter_mapper: AlphabetMap,
}

impl CaesarSolver {
    pub fn new(keyword: String, shift: usize) -> Self {
        let alphabet_size = ALPHABET.chars().count();
        let keyword = keyword.to_uppercase();
        
        check_alphabet(&keyword)
            .unwrap_or_else(panic_on_bad_char);

        // Example: keyword = KEYWORD, shift = 3
        let letter_mapper = ALPHABET
            .chars()                                // ABCDEF...VWXYZ
            .filter(|&ch| keyword.find(ch) == None) // ABCFGH...UVXYZ (without letters from KEYWORD)
            .chain(keyword.chars())                 // ABCFGH...UVXYZKEYWORD
            .cycle()                                // ABCFGH...UVXYZKEYWORDABCFGH...
            .skip(shift % alphabet_size)            // FGHIJL...UVXYZKEYWORDABCFGH...
            .take(alphabet_size)                    // FGHIJL...UVXYZKEYWORDABC
            .collect();

        CaesarSolver { letter_mapper }
    }

    pub fn encrypt(&self, input: impl Iterator<Item = char>) -> String {
        input
            .flat_map(char::to_uppercase)
            .map(|ch| self.letter_mapper.by_idx(alphabetic_num(ch)))
            .collect()
    }

    pub fn decrypt(&self, input: impl Iterator<Item = char>) -> String {
        input
            .flat_map(char::to_uppercase)
            .map(|ch| from_alphabetic_num(self.letter_mapper.to_idx(ch)))
            .collect()
    }
}
