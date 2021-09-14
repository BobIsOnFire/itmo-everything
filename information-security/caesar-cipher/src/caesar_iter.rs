use std::char::ToUppercase;

use crate::CaesarSolver;

pub enum MappingSide
{
    Encrypt,
    Decrypt,
}

pub struct CaesarMappingIter<'a, It>
{
    iter: It,
    curr_upper: Option<ToUppercase>,
    caesar_mapping: &'a CaesarSolver,
    side: MappingSide,
}

impl<'a, It> CaesarMappingIter<'a, It>
    where It: Iterator<Item = char>
{
    fn next_upper(&mut self) -> Option<ToUppercase>
    { self.iter.next().map(char::to_uppercase) }
}

impl<'a, It> Iterator for CaesarMappingIter<'a, It>
    where It: Iterator<Item = char>
{
    type Item = char;

    fn next(&mut self) -> Option<Self::Item> {
        if self.curr_upper.is_none() {
            self.curr_upper = self.next_upper();
        }

        loop {
            if self.curr_upper.is_none() {
                break None;
            }

            match self.curr_upper.as_mut().unwrap().next() {
                Some(ch) => {
                    break Some(
                        match self.side {
                            MappingSide::Encrypt => self.caesar_mapping.encrypt_char(ch),
                            MappingSide::Decrypt => self.caesar_mapping.decrypt_char(ch),
                        }
                    );
                },
                None => {
                    self.curr_upper = self.next_upper();
                }
            }
        }
    }

}

pub trait CaesarEncryptIter
{
    fn encrypt(self, solver: &CaesarSolver) -> CaesarMappingIter<'_, Self>
        where Self: Iterator<Item = char> + Sized
    {
        CaesarMappingIter { iter: self, curr_upper: None, caesar_mapping: solver, side: MappingSide::Encrypt }
    }
}

pub trait CaesarDecryptIter
{
    fn decrypt(self, solver: &CaesarSolver) -> CaesarMappingIter<'_, Self>
        where Self: Iterator<Item = char> + Sized
    {
        CaesarMappingIter { iter: self, curr_upper: None, caesar_mapping: solver, side: MappingSide::Decrypt }
    }
}

impl<It> CaesarEncryptIter for It
    where It: Iterator<Item = char>
{}

impl<It> CaesarDecryptIter for It
    where It: Iterator<Item = char>
{}