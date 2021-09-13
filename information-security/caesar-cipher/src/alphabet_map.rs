use std::iter::FromIterator;

use rustc_hash::FxHashMap;

pub struct AlphabetMap
{
    idx_to_char: Vec<char>,
    char_to_idx: FxHashMap<char, usize>,
}

impl AlphabetMap
{
    pub fn by_idx(&self, idx: usize) -> char
    { self.idx_to_char[idx] }

    pub fn to_idx(&self, ch: char) -> usize
    {
        *self.char_to_idx.get(&ch)
            .unwrap_or_else(|| panic!("Bad character: {}", ch))
    }

    pub fn contains_char(&self, ch: char) -> bool
    { self.char_to_idx.contains_key(&ch) }
}

impl Default for AlphabetMap
{
    fn default() -> Self {
        let idx_to_char: Vec<_> = super::ALPHABET.chars().collect();
        From::from(idx_to_char)
    }
}

impl From<Vec<char>> for AlphabetMap
{
    fn from(idx_to_char: Vec<char>) -> Self {
        let char_to_idx: FxHashMap<_, _> = idx_to_char
            .iter()
            .enumerate()
            .map(|(idx, ch)| (*ch, idx))
            .collect();

        Self { idx_to_char, char_to_idx }
    }
}

impl FromIterator<char> for AlphabetMap
{
    fn from_iter<T: IntoIterator<Item = char>>(iter: T) -> Self {
        let idx_to_char: Vec<_> = iter.into_iter().collect();
        From::from(idx_to_char)
    }
}
