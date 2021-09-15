use std::io;

use feistel_cipher::Encryptor;

use clap::{AppSettings, Clap};

#[derive(Clap, Debug)]
#[clap(
    version = "0.1.0",
    author = "Nikita Akatyev <akatyevnl@gmail.com>",
    about = "Encrypts and decrypts files based on TEA algorithm"
)]
#[clap(setting = AppSettings::ColoredHelp)]
struct Opts {
    #[clap(short, long, about = "Secret key (128 bit)")]
    key: String,
    #[clap(short, about = "Toggle in to decipher")]
    decipher: bool,
    input: String,
    output: String,
}

fn main() -> io::Result<()> {
    let opts = Opts::parse();

    let encryptor = Encryptor::new(opts.key);

    if opts.decipher {
        encryptor.decrypt_file(opts.input, opts.output)
    } else {
        encryptor.encrypt_file(opts.input, opts.output)
    }?;

    Ok(())
}
