// Arithmetic is wrapped, everything else is normal

use std::convert::{From, TryInto};
use std::ops::{Add, AddAssign, BitXor, Shl, Shr, Sub, SubAssign};

#[derive(Clone, Copy)]
pub struct Word(pub u32);

impl Word {
    pub fn to_ne_byte_array(words: (Word, Word)) -> [u8; 8] {
        [words.0 .0.to_ne_bytes(), words.1 .0.to_ne_bytes()]
            .concat()
            .try_into()
            .unwrap()
    }
}

impl From<&[u8]> for Word {
    fn from(slice: &[u8]) -> Self {
        Word(u32::from_ne_bytes(slice.try_into().unwrap()))
    }
}

impl Add<Word> for Word {
    type Output = Self;

    fn add(self, other: Self) -> Self {
        Word(self.0.wrapping_add(other.0))
    }
}

impl Sub<Word> for Word {
    type Output = Self;

    fn sub(self, other: Self) -> Self {
        Word(self.0.wrapping_sub(other.0))
    }
}

impl AddAssign<Word> for Word {
    fn add_assign(&mut self, other: Self) {
        self.0 = self.0.wrapping_add(other.0);
    }
}

impl SubAssign<Word> for Word {
    fn sub_assign(&mut self, other: Self) {
        self.0 = self.0.wrapping_sub(other.0);
    }
}

impl Shl<usize> for Word {
    type Output = Self;

    fn shl(self, other: usize) -> Self {
        Word(self.0 << other)
    }
}

impl Shr<usize> for Word {
    type Output = Self;

    fn shr(self, other: usize) -> Self {
        Word(self.0 >> other)
    }
}

impl BitXor<Word> for Word {
    type Output = Self;

    fn bitxor(self, other: Self) -> Self {
        Word(self.0 ^ other.0)
    }
}
