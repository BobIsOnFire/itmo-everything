use crate::word::Word;

use std::convert::{From, TryInto};
use std::fs::File;
use std::io;
use std::io::prelude::*;

pub struct Encryptor {
    key: [Word; 4],
}

impl Encryptor {
    pub fn new(key_string: String) -> Self {
        let number = u128::from_str_radix(key_string.trim_start_matches("0x"), 16).unwrap();
        let bytes = number.to_ne_bytes();

        Self {
            key: [
                Word::from(&bytes[0..4]),
                Word::from(&bytes[4..8]),
                Word::from(&bytes[8..12]),
                Word::from(&bytes[12..16]),
            ],
        }
    }

    fn read_input(&self, filename: String) -> io::Result<Vec<u8>> {
        let mut input = File::open(filename)?;

        let mut input_bytes = Vec::new();
        input.read_to_end(&mut input_bytes)?;

        // number of bytes should be a multiple of 8
        input_bytes.append(&mut vec![0; 8 - (input_bytes.len() % 8)]);

        Ok(input_bytes)
    }

    fn write_output(&self, filename: String, output_bytes: Vec<u8>) -> io::Result<()> {
        let mut output = File::create(filename)?;
        output.write_all(&output_bytes)?;
        Ok(())
    }

    fn encrypt_block(&self, block: [u8; 8]) -> [u8; 8] {
        let mut sum = Word(0);
        let delta = Word(0x9E3779B9);

        let mut left = Word::from(&block[0..4]);
        let mut right = Word::from(&block[4..8]);

        for _ in 0..32 {
            sum += delta;
            left += ((right << 4) + self.key[0]) ^ (right + sum) ^ ((right >> 5) + self.key[1]);
            right += ((left << 4) + self.key[2]) ^ (left + sum) ^ ((left >> 5) + self.key[3]);
        }

        Word::to_ne_byte_array((left, right))
    }

    fn decrypt_block(&self, block: [u8; 8]) -> [u8; 8] {
        let mut sum = Word(0xC6EF3720);
        let delta = Word(0x9E3779B9);

        let mut left = Word::from(&block[0..4]);
        let mut right = Word::from(&block[4..8]);

        for _ in 0..32 {
            right -= ((left << 4) + self.key[2]) ^ (left + sum) ^ ((left >> 5) + self.key[3]);
            left -= ((right << 4) + self.key[0]) ^ (right + sum) ^ ((right >> 5) + self.key[1]);
            sum -= delta;
        }

        Word::to_ne_byte_array((left, right))
    }

    pub fn encrypt_data(&self, data: &[u8]) -> Vec<u8> {
        data.chunks(8)
            .flat_map(|block| self.encrypt_block(block.try_into().unwrap()))
            .collect::<Vec<_>>()
    }

    pub fn decrypt_data(&self, data: &[u8]) -> Vec<u8> {
        data.chunks(8)
            .flat_map(|block| self.decrypt_block(block.try_into().unwrap()))
            .collect::<Vec<_>>()
    }

    pub fn encrypt_file(&self, input: String, output: String) -> io::Result<()> {
        let data = self.read_input(input)?;
        self.write_output(output, self.encrypt_data(&data))?;
        Ok(())
    }

    pub fn decrypt_file(&self, input: String, output: String) -> io::Result<()> {
        let data = self.read_input(input)?;
        self.write_output(output, self.decrypt_data(&data))?;
        Ok(())
    }
}
