# Fridge Flats

Your fridge is an apartment building. Every grocery is a resident with a personality
(Dramatic, Stoic, Needy, Smug or Anxious). As their expiry date gets closer they get
worried, sweaty, and passive-aggressive, and they'll tell you about it in a push notification.

## What it does

- **Move residents in**: name, type (leafy greens, dairy, meat…), expiry date. Each type has a
  rough default shelf life you can adjust with − / +.
- **The building**: rooms sorted by who expires first. Faces change with mood:
  happy → flat → worried → sweating on the last day → ghost once expired.
  A `!` bubble means someone needs you.
- **Notifications** every morning around 9am, one per stage per resident:
  2 days left, tomorrow, today, and an "incident report" once it's expired.
- **"I ate it" / "I threw it out"** moves them out with a farewell line.
- **Test nag** (top right) sends a sample notification right away.

Everything is stored on the phone. No account, no internet.

## Getting the APK (built for free on GitHub)

1. Create a free account at github.com and make a **new repository** (any name, can be private).
2. On the repo page click **Add file → Upload files** and drag in **everything inside this
   folder**, including the hidden `.github` folder. (On a Mac press Cmd+Shift+. in Finder to show
   hidden folders. On Windows enable View → Hidden items.) Click **Commit changes**.
3. Open the **Actions** tab. A run called **Build APK** starts by itself and takes about 5 minutes.
   If it doesn't start, pick **Build APK** on the left and press **Run workflow**.
4. When it shows a green tick, open the run and download **FridgeFlats-apk** at the bottom.
   It's a zip with `app-debug.apk` inside.
5. Put the APK on your phone and open it. Android will ask you to allow installing apps from
   that source (your file manager or browser). Allow it, then install.
6. Open Fridge Flats and allow notifications when asked.

Requires Android 8.0 or newer.

## Tweaking

- Notification wording: `app/src/main/java/com/fridgeflats/data/Lines.kt`
- Default shelf lives and colours: `app/src/main/java/com/fridgeflats/data/Model.kt`
- Notification time (9am): `NAG_HOUR` in `notify/ExpiryWorker.kt`

Push a change and GitHub builds a fresh APK automatically.
