use caesar_kw::{CaesarSolver, decrypt_str, encrypt_str};
use clap::{AppSettings, Clap};

#[derive(Clap, Debug)]
#[clap(
    version = "1.0",
    author = "Nikita Akatyev <akatyevnl@gmail.com>",
    about = "Encrypts and decrypts Caesar-ciphered strings based on keywords"
)]
#[clap(setting = AppSettings::ColoredHelp)]
struct Opts {
    #[clap(short, long, default_value = "ЦЕЗАРЬ", about = "Target keyword")]
    keyword: String,
    #[clap(short, long, default_value = "3", about = "Target shifting")]
    shift: usize,
    #[clap(short, about = "Toggle in to decipher")]
    decipher: bool,
    input: String,
}

fn main() {
    let opts = Opts::parse();

    let solver = CaesarSolver::new(opts.keyword, opts.shift);
    let result: String = if opts.decipher {
        decrypt_str(&opts.input, &solver)
    } else {
        encrypt_str(&opts.input, &solver)
    };

    println!("{}", result)
}
