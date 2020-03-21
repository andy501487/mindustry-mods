use directories::ProjectDirs;
use reqwest::{
    header::{HeaderMap, HeaderValue, AUTHORIZATION, USER_AGENT},
    Client,
};
use serde::Deserialize;
use std::{collections::HashMap, path::PathBuf};
use structopt::StructOpt;
use tokio::prelude::*;

#[derive(Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
enum Encoding {
    Base64,
}

#[derive(Deserialize, Debug)]
struct Contents {
    encoding: Encoding,
    content: String,
}

struct GitHub {
    client: Client,
}

impl GitHub {
    fn new(token: &str) -> Result<Self, Box<dyn std::error::Error>> {
        let mut headers = HeaderMap::new();
        headers.insert(AUTHORIZATION, HeaderValue::from_str(&token)?);
        headers.insert(USER_AGENT, HeaderValue::from_str("Mindustry-Mods-Backend")?);

        let client = reqwest::Client::builder()
            .default_headers(headers)
            .build()?;

        Ok(Self { client })
    }

    async fn get(&self, url: &str) -> Result<Contents, Box<dyn std::error::Error>> {
        Ok(self
            .client
            .get(url)
            .send()
            .await?
            .json::<Contents>()
            .await?)
    }
}

/// Deserializes mods from list at: https://github.com/Anuken/MindustryMods/blob/master/mods.json
#[derive(Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
struct ModSource {
    /// ex: `"What42Pizza/Mindustry-Production-Mod"`
    repo: String,

    /// ex: `"Mindustry-Production-Mod"`
    name: String,

    /// ex: `"[orange]What42Pizza"`
    author: String,

    /// ex: `"2020-03-18T16:35:29Z"`
    last_updated: String,

    /// ex: `25`
    stars: u32,

    /// ex: `"[white]This mod gives you [orange]iron[white]..."`
    description: String,
}

/// Midustry-Mods backend CLI.
#[derive(StructOpt, Debug)]
#[structopt(name = "mindustry-mods")]
struct Opt {
    /// Run templates right away
    #[structopt(short, long)]
    instant: bool,

    /// Push said changes to GitHub.
    #[structopt(short, long)]
    push: bool,

    /// Keep running hourly.
    #[structopt(short, long)]
    hourly: bool,

    /// Clear cache and stuff.
    #[structopt(short, long)]
    clean: bool,

    /// No update, just get to the end.
    #[structopt(short, long)]
    fast: bool,

    /// Path to root of directory.
    #[structopt(short = "d", long, default_value = ".", parse(from_os_str))]
    path: PathBuf,
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let opt = Opt::from_args();

    let dirs = ProjectDirs::from("", "Mindustry-Mods", "Mindustry-Mods-Backend")
        .expect("Project directories returned None.");
    tokio::fs::create_dir_all(dirs.config_dir()).await?;

    let token_path = dirs.config_dir().join("github-token");
    let mut file = match tokio::fs::File::open(token_path).await {
        Err(e) if e.kind() == std::io::ErrorKind::NotFound => {
            println!("Github token file not found.");
            return Ok(());
        }

        other => other,
    }?;

    let mut token = String::new();
    file.read_to_string(&mut token).await?;

    let github = GitHub::new(&token);
    let resp = github?
        .get("https://api.github.com/repos/Anuken/MindustryMods/contents/mods.json")
        .await?;
    let content = match resp.encoding {
        Encoding::Base64 => {
            String::from_utf8(base64::decode(str::replace(&resp.content, "\n", ""))?)
        }
    }?;

    let mods_source: Vec<ModSource> = serde_json::from_str(&content).unwrap();

    println!("{:?}", mods_source);

    // .json::<Vec<core::Mod>>()
    // .await?;

    // let yaml_path = dirs.config_dirs() / "mindustry-mods.yaml";

    // let mut file = File::open().await?;

    // // Authorization: token OAUTH-TOKEN
    // let resp = reqwest::get("https://api.github.com/user")
    //     .await?
    //     .json::<HashMap<String, String>>()
    //     .await?;
    // println!("{:#?}", resp);
    Ok(())
}
