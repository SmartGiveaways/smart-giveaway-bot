[![codacy-rating](https://app.codacy.com/project/badge/Grade/623f090b77d94b26861915bac0db5309)](https://app.codacy.com/gh/SmartGiveaways/smart-giveaway-bot/dashboard)
[![github-issues](https://img.shields.io/github/issues/SmartGiveaways/smart-giveaway-bot)](https://github.com/SmartGiveaways/smart-giveaway-bot/issues)
[![github-prs](https://img.shields.io/github/issues-pr/SmartGiveaways/smart-giveaway-bot)](https://github.com/SmartGiveaways/smart-giveaway-bot/pulls)
![project-license](https://img.shields.io/github/license/SmartGiveaways/smart-giveaway-bot)
[![discord-widget](https://discord.com/api/guilds/751886048623067186/widget.png)](https://discord.gg/aS4PebKZpe)

# Smart Giveaway Bot

- Create easy and dynamic giveaways with custom reactions, presets and more.
- Take control over your giveaways with the ability to ban & shadow ban users.
- (WIP) Control your giveaways via an easy web interface
  
![Example One](https://zak.pink/2020/12/Selfish-Ichthyostega-10753.png)<br/>
![Example Two](https://zak.pink/2020/11/Foolhardy-Africancivet-9917.png)
  
## Welcome us in
Invite SmartGiveaways to your server in one click -> [here](https://smartgiveaways.xyz/invite)

## Bugs / Suggestions
If you have a bug or suggestion please make a new issue right [here](https://github.com/SmartGiveaways/smart-giveaway-bot/issues) on GitHub and don't be sad if your suggestion is denied, there are lots of reasons and we'll try to explain why.

## Development
SmartGiveaway is actively maintained, has new features added and bugs fixed at the moment. We welcome pull requests and direct fixes from the community here on GitHub but please,  don't make low quality or spammy pull requests. Make them at least a bit contentful.

## Da license bro
Pwease abide by the LGPL-2.1 license for this project and bear in mind that this still applies if you're self hosting the bot. If you're on GitHub, click [here](https://github.com/SmartGiveaways/smart-giveaway-bot/blob/main/LICENSE) and GitHub will display some important points of the license. This is not legal advice.

## Self hosting
No support is officially provided for self hosting but if you have issues directly related to the bot (not how you've set it up or getting it set up), we may help you if you're nice. Here's just some basic information about self hosting this bot.

### Prerequisites
- Some basic experience in how a discord bot works, hopefully some basic java experience as well.
- A decent processor, 1 core will do for nearly everyone.
- At least 50MB RAM (we'd hope a bit more, varies on what JDK you use)
- Java 14 / Control over JVM arguments (--enable-preview on)
- MongoDB
- InfluxDB (Optional, used for [metrics](https://zak.pink/2020/11/Narrow-Minded-Lacewing-9912.png))
- Us telling you that you **must** manually copy the resource files (including lang) and make a file for ping testing messages.
