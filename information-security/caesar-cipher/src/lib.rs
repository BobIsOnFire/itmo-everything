#[macro_use]
extern crate lazy_static;
mod alphabet_map;
mod caesar_iter;

use alphabet_map::AlphabetMap;
use caesar_iter::{CaesarEncryptIter, CaesarDecryptIter};

#[derive(Debug, Clone)]
pub struct CaesarSolver {
    letter_mapper: AlphabetMap,
}

fn validate_alphabet(word: &str)
{
    word.chars()
        .for_each(alphabet_map::validate_char)
}

impl CaesarSolver {
    pub fn new(keyword: String, shift: usize) -> Self {
        let alphabet_size = alphabet_map::alphabet_size();
        let keyword = keyword.to_uppercase();

        validate_alphabet(&keyword);

        // Example: keyword = KEYWORD, shift = 3
        let letter_mapper = alphabet_map::original_alphabet()
            .chars()                                // ABCDEF...VWXYZ
            .filter(|&ch| keyword.find(ch) == None) // ABCFGH...UVXYZ (without letters from KEYWORD)
            .chain(keyword.chars())                 // ABCFGH...UVXYZKEYWORD
            .cycle()                                // ABCFGH...UVXYZKEYWORDABCFGH...
            .skip(shift % alphabet_size)            // FGHIJL...UVXYZKEYWORDABCFGH...
            .take(alphabet_size)                    // FGHIJL...UVXYZKEYWORDABC
            .collect();

        CaesarSolver { letter_mapper }
    }

    pub fn encrypt_char(&self, ch: char) -> char
    { self.letter_mapper.to_mapped(ch) }

    pub fn decrypt_char(&self, ch: char) -> char
    { self.letter_mapper.to_origin(ch) }

    pub fn into_encryptor(self) -> impl Fn(char) -> char
    { move |ch: char| self.encrypt_char(ch) }

    pub fn into_decryptor(self) -> impl Fn(char) -> char
    { move |ch: char| self.decrypt_char(ch) }

    pub fn construct_encryptor(&self) -> impl Fn(char) -> char
    { self.clone().into_encryptor() }

    pub fn construct_decryptor(&self) -> impl Fn(char) -> char
    { self.clone().into_decryptor() }
}

pub fn encrypt_str(input: &str, solver: &CaesarSolver) -> String
{
    input
        .chars()
        .encrypt(solver)
        .collect()
}

pub fn decrypt_str(input: &str, solver: &CaesarSolver) -> String
{
    input
        .chars()
        .decrypt(solver)
        .collect()
}
