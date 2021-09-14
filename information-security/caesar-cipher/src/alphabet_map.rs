use std::{hash::BuildHasherDefault, iter::FromIterator};

use bimap::BiHashMap;
use rustc_hash::{FxHasher};

type FxHasherBuilder = BuildHasherDefault<FxHasher>;

const ALPHABET: &str = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

#[derive(Debug, Clone)]
pub struct AlphabetMap
{
    mapper: BiHashMap<char, char, FxHasherBuilder, FxHasherBuilder>, // map from original to mapped
}

impl AlphabetMap
{
    pub fn to_mapped(&self, origin: char) -> char
    {
        *self.mapper.get_by_left(&origin)
            .unwrap_or_else(|| self.panic_on_bad_char(origin))
    }

    pub fn to_origin(&self, mapped: char) -> char
    {
        *self.mapper.get_by_right(&mapped)
            .unwrap_or_else(|| self.panic_on_bad_char(mapped))
    }

// private
    fn panic_on_bad_char<Ret>(&self, ch: char) -> Ret
    {
        panic!("Unexpected character: '{}' ({:x}), alphabet: {:?}", ch, ch as usize, self.mapper.left_values().collect::<Vec<_>>())
    }
}

impl Default for AlphabetMap
{
    fn default() -> Self {
        ALPHABET.chars().collect()
    }
}

impl From<Vec<char>> for AlphabetMap
{
    fn from(idx_to_char: Vec<char>) -> Self {
        idx_to_char.into_iter().collect()
    }
}

impl FromIterator<char> for AlphabetMap
{
    fn from_iter<T: IntoIterator<Item = char>>(iter: T) -> Self {
        let mapper = ALPHABET
            .chars()
            .zip(iter.into_iter())
            .collect();
        Self { mapper }
    }
}

lazy_static! {
    static ref ID_ALPHABET: AlphabetMap = Default::default();
}

pub fn alphabet_size() -> usize
{ ALPHABET.len() }

pub fn original_alphabet() -> &'static str
{ ALPHABET }

/// panics on bad char
pub fn validate_char(ch: char)
{ ID_ALPHABET.to_mapped(ch); }
